package com.example.hershield;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class BluetoothService {

    private static final String TAG = "BluetoothService";

    // ESP32-C3 BLE device name — must match Arduino code
    private static final String ESP32_NAME = "ESP32_C3_BLE";

    // UUIDs — must match what your teammate sets in Arduino code
    // These are standard Nordic UART Service UUIDs (most common for ESP32 BLE)
    private static final UUID SERVICE_UUID =
            UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID CHARACTERISTIC_UUID =
            UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID DESCRIPTOR_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // Scan timeout — 10 seconds
    private static final long SCAN_TIMEOUT = 10000;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bluetoothGatt;
    private boolean isConnected = false;
    private boolean isScanning = false;

    private Context context;
    private BluetoothListener listener;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface BluetoothListener {
        void onConnected();
        void onDisconnected();
        void onSOSReceived();
        void onBatteryReceived(int level);
        void onConnectionFailed(String error);
    }

    public BluetoothService(Context context, BluetoothListener listener) {
        this.context = context;
        this.listener = listener;
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public void connect() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            listener.onConnectionFailed("Bluetooth is off. Please turn it on.");
            return;
        }

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            listener.onConnectionFailed("Bluetooth permission not granted");
            return;
        }

        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        isScanning = true;

        // Stop scan after timeout
        mainHandler.postDelayed(() -> {
            if (isScanning) {
                stopScan();
                if (!isConnected) {
                    listener.onConnectionFailed(
                            "ESP32 not found! Make sure it's on and named: " + ESP32_NAME);
                }
            }
        }, SCAN_TIMEOUT);

        bleScanner.startScan(scanCallback);
    }

    private void stopScan() {
        if (isScanning && bleScanner != null) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED) {
                bleScanner.stopScan(scanCallback);
            }
            isScanning = false;
        }
    }

    // BLE Scan Callback — finds ESP32
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) return;

            String deviceName = device.getName();
            if (deviceName != null && deviceName.equals(ESP32_NAME)) {
                Log.d(TAG, "Found ESP32: " + deviceName);
                stopScan();
                // Connect to found device
                bluetoothGatt = device.connectGatt(context, false, gattCallback);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            mainHandler.post(() ->
                    listener.onConnectionFailed("BLE scan failed: " + errorCode));
        }
    };

    // GATT Callback — handles connection and data
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) return;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnected = true;
                Log.d(TAG, "Connected to ESP32!");
                mainHandler.post(() -> listener.onConnected());
                // Discover services after connecting
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnected = false;
                Log.d(TAG, "Disconnected from ESP32");
                mainHandler.post(() -> listener.onDisconnected());
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Find the characteristic to receive data from ESP32
                BluetoothGattService service = gatt.getService(SERVICE_UUID);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic =
                            service.getCharacteristic(CHARACTERISTIC_UUID);
                    if (characteristic != null) {
                        if (ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.BLUETOOTH_CONNECT)
                                != PackageManager.PERMISSION_GRANTED) return;

                        // Enable notifications so we receive data
                        gatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor =
                                characteristic.getDescriptor(DESCRIPTOR_UUID);
                        if (descriptor != null) {
                            descriptor.setValue(
                                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                        Log.d(TAG, "Notifications enabled for ESP32");
                    }
                } else {
                    Log.e(TAG, "Service not found! Check UUID matches Arduino code.");
                    mainHandler.post(() ->
                            listener.onConnectionFailed(
                                    "Service UUID mismatch. Check Arduino code."));
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // Data received from ESP32
            String received = new String(characteristic.getValue()).trim();
            Log.d(TAG, "Received from ESP32: " + received);

            if (received.equals("1")) {
                // SOS button pressed
                mainHandler.post(() -> listener.onSOSReceived());

            } else if (received.startsWith("BAT:")) {
                // Battery level e.g. "BAT:75"
                try {
                    int level = Integer.parseInt(received.substring(4).trim());
                    mainHandler.post(() -> listener.onBatteryReceived(level));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid battery format: " + received);
                }
            }
        }
    };

    public void disconnect() {
        isConnected = false;
        stopScan();
        if (bluetoothGatt != null) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED) {
                bluetoothGatt.close();
            }
            bluetoothGatt = null;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
