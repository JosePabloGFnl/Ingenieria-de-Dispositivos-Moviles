package com.example.cva_videollamada.activity.Vendedor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.example.cva_videollamada.activity.VideoLlamada.MainActivity;
import com.example.cva_videollamada.activity.verUsuarios_Activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import javax.annotation.Nullable;

public class menu_chat_Admin extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    private Button btnVerUsuarios, btnVerVideollamada;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_menu_chat_admin);

        btnVerUsuarios = findViewById(R.id.btn_verUsuarios);
        btnVerVideollamada = findViewById(R.id.btn_verVideollamada);

        preferenceManager = new PreferenceManager(getApplicationContext());

        //SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.chat_admin);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cerrar_Sesion_admin:
                    startActivity(new Intent(getApplicationContext()
                            , cerrar_sesion_vendedor.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.chat_admin:
                    return true;

                case R.id.lista_carro_admin:
                    startActivity(new Intent(getApplicationContext()
                            , lista_carros.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.vender_admin:
                    startActivity(new Intent(getApplicationContext()
                            , vender.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        btnVerUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(menu_chat_Admin.this, verUsuarios_Activity.class);
            startActivity(intent);
        });

        btnVerVideollamada.setOnClickListener(v -> {
            Intent intent = new Intent(menu_chat_Admin.this, MainActivity.class);
            startActivity(intent);
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(menu_chat_Admin.this);
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
