package com.example.cva_videollamada.activity.Vendedor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class vender extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    DatabaseReference mRootReference;
    Button mSubirDatos;
    EditText marcaCarro, modeloCarro, anioCarro, colorCarro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_vender);

        preferenceManager = new PreferenceManager(getApplicationContext());

        mRootReference = FirebaseDatabase.getInstance().getReference();
        mSubirDatos = findViewById(R.id.subirDatos);
        marcaCarro = findViewById(R.id.carro_marca);
        modeloCarro = findViewById(R.id.carro_modelo);
        anioCarro = findViewById(R.id.carro_año);
        colorCarro = findViewById(R.id.carro_color);

        mSubirDatos.setOnClickListener(v -> {
            String marca = marcaCarro.getText().toString();
            String modelo = modeloCarro.getText().toString();
            int anio = Integer.parseInt(anioCarro.getText().toString());
            String color = colorCarro.getText().toString();

            Map<String, Object> datosCarro = new HashMap<>();
            datosCarro.put("marca", marca);
            datosCarro.put("modelo", modelo);
            datosCarro.put("anio", anio);
            datosCarro.put("color", color);

            mRootReference.child("Carros").push().setValue(datosCarro);
            Toast.makeText(vender.this, "Auto publicado con exito", Toast.LENGTH_SHORT).show();
        });

        //SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.vender_admin);

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
                    startActivity(new Intent(getApplicationContext()
                            , lista_carros.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.vender_admin:
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(vender.this);
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
}