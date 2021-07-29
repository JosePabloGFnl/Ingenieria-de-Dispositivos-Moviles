package com.example.cva_videollamada.activity.Vendedor;

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
import com.example.cva_videollamada.adapters.AdaterCarrosAdmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Locale;

public class lista_carros extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    Button mSubirDatos;
    Button mEliminarDatos;
    private DatabaseReference mDatabase;

    private AdaterCarrosAdmin mAdapterCarrosAdmin;
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<carros_view_admin> mCarrosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_lista_carros);

        preferenceManager = new PreferenceManager(getApplicationContext());

        mSubirDatos = findViewById(R.id.subirDatos);
        mEliminarDatos = findViewById(R.id.ic_delete);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCarros);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getCarrosFromFirebase();

        //SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.lista_carro_admin);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cerrar_Sesion_admin:
                    startActivity(new Intent(getApplicationContext()
                            , cerrar_sesion_vendedor.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.chat_admin:
                    startActivity(new Intent(getApplicationContext()
                            , menu_chat_Admin.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.lista_carro_admin:
                    return true;

                case R.id.vender_admin:
                    startActivity(new Intent(getApplicationContext()
                            , vender.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        Button changeLang = findViewById(R.id.cambio_lenguaje);
        changeLang.setOnClickListener(view -> showChangeLanguageDialog());

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constantes.KEY_NOMBRE),
                preferenceManager.getString(Constantes.KEY_APELLIDO)
        ));
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Español", "Ingles"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(lista_carros.this);
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
                        mCarrosList.add(new carros_view_admin(marca, modelo, color, anio));
                    }
                    //este es lo que él tiene como mAdapter
                    mAdapterCarrosAdmin = new AdaterCarrosAdmin(mCarrosList, R.layout.card_view_admin);
                    mRecyclerView.setAdapter(mAdapterCarrosAdmin);

                    mAdapterCarrosAdmin.setOnItemClickListener(new AdaterCarrosAdmin.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            mCarrosList.get(position);
                        }
                        @Override
                        public void onDeleteClick(int position){
                            mCarrosList.remove(position);
                            mAdapterCarrosAdmin.notifyItemRemoved(position);
                            mDatabase.child("Carros").getRef().removeValue();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}