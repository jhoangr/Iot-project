package com.example.ble;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import com.example.ble.DeviceScan;

//clase que gestiona la búsqueda de dispositius i la comunicació amb el GattServer fent servir el GattManager.
public class BLEManager {
    private static final int REQUEST_ENABLE_BT = 1 ;
    private final static int REQUEST_DISCOVERABLE_BT=2;
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private Context context;
    private Activity  Activity;
    private DeviceScan deviceScan;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> scannedDevices;
    private ListView pairedDevicesLV;
    private GattManager gattManager;
    private String DataFromGattServer;

    BLEManager(Activity mainActivity, Context ctx,
               ListView pairedDevicesLV, ListView scannedDevicesLV)
    {   this.Activity=mainActivity;
        this.context=ctx;
        bluetoothManager = (BluetoothManager) this.Activity.getSystemService(context.BLUETOOTH_SERVICE);
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.Activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        scannedDevices=new ArrayList();
        this.deviceScan=new DeviceScan( mainActivity, ctx,bluetoothAdapter, scannedDevicesLV,scannedDevices);
        this.pairedDevicesLV=pairedDevicesLV;
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features
        if (!this.Activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this.context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            this.Activity.finish();
        }
        ActivityCompat.requestPermissions(this.Activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        gattManager= new GattManager(mainActivity,ctx);
    }

    public void list_paired_devices(View v){
        pairedDevices = bluetoothAdapter.getBondedDevices();

        ArrayList list = new ArrayList();
        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
        }

        final ArrayAdapter adapter = new  ArrayAdapter(this.context ,android.R.layout.simple_list_item_1, list);


        pairedDevicesLV.setAdapter(adapter);
    }
    public  void visible(){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        this.Activity.startActivityForResult(getVisible,REQUEST_DISCOVERABLE_BT);
    }
    public boolean bluetoothAdapterIsEnabled()
    {
        return  bluetoothAdapter.isEnabled();

    }
    public void BLEScan(){
        deviceScan.scanLeDevice();
    }

    public void connectToDeviceSelectedScann(int pos){
        gattManager.connectToDeviceSelected(scannedDevices.get(pos));

    }

    public void connectToDeviceSelectedPaired(int pos){
        List<BluetoothDevice> List = new ArrayList<>(pairedDevices);
        gattManager.connectToDeviceSelected(List.get(pos));

    }
    public String GetDataTimeDataFromGattServer() {
        return gattManager.GetTimeDataFromGattServer();
    }
    public int GetDataBatteryLevelFromGattServer(){
        return gattManager.GetBatteryLevelDataFromGattServer();
    }

}
