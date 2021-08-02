package com.example.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

//classe que permet gestionar la búsqueda(scann) BLE de dispositius.
public class DeviceScan {

    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    Context context;
    android.app.Activity Activity;
    ArrayList list;
    ListView scannedDevicesLV;
    private ArrayList<BluetoothDevice> scannedDevices ;

    DeviceScan(Activity mainActivity, Context ctx,BluetoothAdapter bluetoothAdapter, ListView scannedDevicesLV,
               ArrayList<BluetoothDevice> scannedDevices )
    {   this.Activity=mainActivity;
        this.context=ctx;
        this.scannedDevices= scannedDevices;
        this.scannedDevicesLV=scannedDevicesLV;
        this.bluetoothAdapter=bluetoothAdapter;
        list=new ArrayList();
    }
    //Callback per controlar quan ha finalitzar el scan i si ha estat correcte
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //si obtenim resultat l'afegim el dispositiu a la lista de dispostius escanejats
            if (result != null) {
                String dName=result.getScanRecord().getDeviceName();
                Log.d("ScanCallBack_check",result.getScanRecord().toString());
                Log.d("ScanCallBack_check", result.toString());
                Log.d("ScanCallBack_Check","Un dispositivo ha sido encontrado");
                // en BLE se requiere la MAC primordialmente
                scannedDevices.add(result.getDevice());
                if(dName != null) {
                    Log.d("ScanCallBack_Check", dName);
                }
                if(result.getDevice().getAddress()!= null)
                {
                    if(!list.contains(result.getDevice().getAddress()))  list.add(result.getDevice().getAddress());

                }
                System.out.println("found");
            }else
                Log.d("ScanCallBack_Check","Ningun dispositivo ha sido encontrado");
            if(!list.isEmpty())
            {final ArrayAdapter adapter = new  ArrayAdapter(context,android.R.layout.simple_list_item_1, list);
                //linkem la llista de dispositis mitjançant l'adaptador al listview per mostrar-lo a la app
                scannedDevicesLV.setAdapter(adapter);}
        }
        //métode per si l'escan a retornat una llista de resultats
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("ScanCallBack_Check","OnBatch");

        }
        @Override
        //métode que s'activa si l'escan ha estat incorrecte
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("ScanCallBack_Check",Integer.toString(errorCode));
            Log.d("ScanCallBack_Check","OnFailed");
        }
    };
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    // métode que permet realitzar l'escan durant un temps definit per SCAN_PERIOD mitjançant BLE
    public void scanLeDevice() {
        final BluetoothLeScanner mLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler=new Handler();
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mLeScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mLeScanner.startScan(mLeScanCallback);
    }




}
