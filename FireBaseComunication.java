package com.example.ble;

import android.os.BatteryManager;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
//clase que simplement crea una instancia FireBase i envia la hora i el nivell de bateria a cloudFirestore
public class FireBaseComunication {
    private Map<String, Object> Temperatura;
    private FirebaseFirestore db;


    public void enviarDades(int BatteryLevel,String hora)
    {
        db = FirebaseFirestore.getInstance();
        Temperatura = new HashMap<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        String format = simpleDateFormat.format(new Date());

        Temperatura.put("BatteryLevel",BatteryLevel);
        Temperatura.put("Timestamp",format);
        Temperatura.put("Hora",hora);


        db.collection("BatteryLevel")
                .add(Temperatura)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d("dbCollection", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("addonfailure", "Error adding document", e);
                    }
                });
    }


}
