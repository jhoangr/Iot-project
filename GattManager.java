package com.example.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

//Aquesta clase permet gestionar la connexió amb un servidor Gatt que té un servei de data i nivell de bateria del dispositiu.
public class GattManager {

    // Various callback methods defined by the BLE API.
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SERVICE_C1=UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    public static UUID DESCRIPTOR= UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");



    private BluetoothGatt bluetoothGatt;
    private Context context;
    private android.app.Activity Activity;
    private BluetoothGattCharacteristic characteristic;
    private String DataFromGattServer_time;
    private int DataFromGattServer_battery_level;

    GattManager(Activity MainActivity,Context MainContext)
    {

        this.Activity=MainActivity;
        this.context=MainContext;
        //registrem el broadcastreciver i els seus filters
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GATT_CONNECTED);
        filter.addAction(ACTION_GATT_DISCONNECTED);
        filter.addAction(ACTION_GATT_SERVICES_DISCOVERED );
        filter.addAction(ACTION_DATA_AVAILABLE);
        Activity.registerReceiver(gattUpdateReceiver,filter);
        DataFromGattServer_time=null;
        DataFromGattServer_battery_level=0;
    }
    //métode per obtenir la hora del servidorGatt
    public String GetTimeDataFromGattServer() {

        return DataFromGattServer_time;
    }
    //métode per obtenir el nivell de bateria
    public int GetBatteryLevelDataFromGattServer() {

        return DataFromGattServer_battery_level;
    }
    //callback per gestionar l'estat de la connexió,el discovery de serveis,i la lectura o escritura d'una característica.
    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    Object connectionState;

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.i("GATT connection", "Connected to GATT server.");
                        Log.i("GATT connection", "Attempting to start service discovery:" +
                                gatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;
                        Log.i("GATT connection", "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);

                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                        Log.w("onServicesDiscovered", "onServicesDiscovered ok received: " + status);
                    } else {
                        Log.w("onServicesDiscovered", "onServicesDiscovered error received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }

                // Characteristic notification
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }
            };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
        System.out.println("Connection broadcastUpdate");
    }
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
        System.out.println("Characteristic broadcast update");


    }
    // Handles various events fired by the Service.
// ACTION_GATT_CONNECTED: connected to a GATT server.
// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
// ACTION_DATA_AVAILABLE: received data from the device. This can be a
// result of read or notification operations.
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (ACTION_GATT_CONNECTED.equals(action)) {
                System.out.println("connected");
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                System.out.println("disconnected");
            } else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //Si descobrim el servei, ens subscribim a la característica determinada
                //en el nostre cas aquesta característica ens retorna el nivell de bateria i la data
                System.out.println("services discovered");
                characteristic=bluetoothGatt.getService(UUID_SERVICE).getCharacteristic(UUID_SERVICE_C1);
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                System.out.println("data available");
                //una vegada hem rebut dades del servidorGatt les guardem
                int battery_level=(int)characteristic.getValue()[0];
                int month=(int)characteristic.getValue()[2];
                int day=(int)characteristic.getValue()[3];
                int hour=(int)characteristic.getValue()[4];
                int min=(int)characteristic.getValue()[5];
                int s=(int)characteristic.getValue()[6];

                DataFromGattServer_time=String.valueOf(day)+"/"+String.valueOf(month)+"["+String.valueOf(hour)+":"+
                      String.valueOf(min)+":"+String.valueOf(s)+"]";
                DataFromGattServer_battery_level=battery_level;

                System.out.println(DataFromGattServer_battery_level);
                System.out.println(DataFromGattServer_time);
            }
        }
    };
    //métode que ens permet conectarnos a un dispositiu
    public void connectToDeviceSelected(BluetoothDevice device)    {
        bluetoothGatt=device.connectGatt(this.context,false,gattCallback);
    }
    public void closeGatt() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;

    }
}
