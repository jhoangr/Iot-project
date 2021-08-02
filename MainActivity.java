package com.example.ble;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ble.FireBaseComunication;

//Aquesta activitat gestiona els events de pulsacions de botons que permeten buscar dispositius, mostrar els dispositius que ja han estat units
//Enviar les dades rebudes del GattManager al cloud i Mostrar les dades en l'aplicació.
public class MainActivity extends AppCompatActivity {
    BLEManager BLEManager;//ens permetrá gestionar tot lo relacionat amb la búsqueda de dispositius i la obtenció de dades del servidor Gatt
    FireBaseComunication fireBaseComunication;//ens permetrá enviar dades al servidor cloud
    ListView pairedDevicesLV;
    ListView scannedDevicesLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannedDevicesLV = (ListView) findViewById(R.id.scan_devices_list);
        pairedDevicesLV = (ListView) findViewById(R.id.paired_devices_list);
        BLEManager = new BLEManager(MainActivity.this, this, pairedDevicesLV, scannedDevicesLV);
        //comprovem si el dispositiu es visible.
        BLEManager.visible();
        fireBaseComunication = new FireBaseComunication();



        final Button scann = findViewById(R.id.scan_devices);
        //listener para iniciar scann
        scann.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Iniciamos la busqueda de dispositivos y mostramos el mensaje de que el proceso ha comenzado
                if (BLEManager.bluetoothAdapterIsEnabled()) {
                    BLEManager.BLEScan();
                    Toast.makeText(getApplicationContext(), "Escaneando dispositivos", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "Bluetooth adapter not enabled!!", Toast.LENGTH_LONG).show();
            }
        });


        final Button pairedDevicesbutton = findViewById(R.id.paired_devices);
        //listener para moster dispositivos emparejados
        pairedDevicesbutton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                BLEManager.list_paired_devices(v);
                Toast.makeText(getApplicationContext(), "Mostrando dispositivos emparejados...", Toast.LENGTH_LONG).show();
            }
        });
        //listener para verificar si se ha pulsado un item de la lista de dispositvos escaneados.
        scannedDevicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                BLEManager.connectToDeviceSelectedScann(position);

            }
        });
        //listener para verificar si se ha pulsdao un item de la lista de emparejados
        pairedDevicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                BLEManager.connectToDeviceSelectedPaired(position);
            }
        });


        final Button send_to_database = findViewById(R.id.enviar_dades);
        //en cas de clicar el botó "Enviar dades" obtenim dades del BLEManager que prèviament ha obtingut de la comunicació amb el
        //servidor Gatt
        send_to_database.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String Time= BLEManager.GetDataTimeDataFromGattServer();
                if(Time != null)
                {
                    fireBaseComunication.enviarDades(BLEManager.GetDataBatteryLevelFromGattServer(),Time);
                }
                //fireBaseComunication.enviarDades();

            }
        });
        //Permet visualitzar les dades rebudes del GattManager al dispositu
        //BaterryLevel:34%
        //Time:dia/mes[hora:min:s]
        final Button dadesgatt=findViewById(R.id.dades_gatt_server);
        dadesgatt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Iniciamos la busqueda de dispositivos y mostramos el mensaje de que el proceso ha comenzado
                //add if null
                ((TextView)findViewById(R.id.Gattserverdata)).setText("BatteryLevel:"+BLEManager.GetDataBatteryLevelFromGattServer()+"%"+"\nTime:"+BLEManager.GetDataTimeDataFromGattServer());

            }
        });
    }//onCreate-end
}






