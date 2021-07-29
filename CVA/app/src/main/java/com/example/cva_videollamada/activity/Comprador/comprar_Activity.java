package com.example.cva_videollamada.activity.Comprador;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.example.cva_videollamada.adapters.AdaterCarros;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class comprar_Activity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    Button mSubirDatos;
    private DatabaseReference mDatabase;

    private AdaterCarros mAdapterCarros;
    private RecyclerView mRecyclerView;
    private ArrayList<carros_view_descripccion> mCarrosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_comprar);

        mSubirDatos = findViewById(R.id.subirDatos);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        preferenceManager = new PreferenceManager(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCarros);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getCarrosFromFirebase();


        //SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.comprar);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cerrar_Sesion:
                    startActivity(new Intent(getApplicationContext()
                            , cerrar_sesion_comprador.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.comprar:
                    return true;

                case R.id.mensajeria:
                    startActivity(new Intent(getApplicationContext()
                            , Menu_chat_activity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constantes.KEY_NOMBRE),
                preferenceManager.getString(Constantes.KEY_APELLIDO)
        ));

        Button changeLang = findViewById(R.id.cambio_lenguaje);
        changeLang.setOnClickListener(view -> showChangeLanguageDialog());
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Español", "Ingles"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(comprar_Activity.this);
        mBuilder.setTitle("Lenguaje");
        mBuilder.setSingleChoiceItems(listItems, -1, (dialog, which) -> {
            if(which == 0){
                setLocale("es");
                recreate();
            }
            if(which == 1){
                setLocale("en");
                recreate();
            }

            dialog.dismiss();
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Configuraciones", MODE_PRIVATE).edit();
        editor.putString("Mi_lenguaje", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Configuraciones", Activity.MODE_PRIVATE);
        String language = prefs.getString("Mi_lenguaje", "");
        setLocale(language);
    }

    private void getCarrosFromFirebase(){
        mDatabase.child("Carros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot ds: snapshot.getChildren()){
                        String marca = ds.child("marca").getValue().toString();
                        String anio = ds.child("anio").getValue().toString();
                        String color = ds.child("color").getValue().toString();
                        String modelo = ds.child("modelo").getValue().toString();
                        mCarrosList.add(new carros_view_descripccion(marca, modelo, color, anio));
                    }

                    mAdapterCarros = new AdaterCarros(mCarrosList, R.layout.carros_view_layout);
                    mRecyclerView.setAdapter(mAdapterCarros);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}