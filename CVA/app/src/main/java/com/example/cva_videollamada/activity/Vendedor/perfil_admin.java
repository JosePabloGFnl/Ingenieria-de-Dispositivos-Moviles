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
import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Locale;

public class perfil_admin extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference CVA;

    TextView Nombre;
    TextView Correo;
    TextView Fecha_nac;
    TextView Genero;
    //ImageView Foto_perfil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_perfil_admin);

        preferenceManager = new PreferenceManager(getApplicationContext());

        //CONEXION BD
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        CVA = firebaseDatabase.getReference("Usuarios");

        Nombre = findViewById(R.id.Nombre);
        Correo = findViewById(R.id.Correo);
        Fecha_nac = findViewById(R.id.Fecha_nac);
        Genero = findViewById(R.id.Genero);
        //Foto_perfil = findViewById(R.id.Foto_perfil);

        /*//SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.perfil_admin);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cerrar_Sesion_admin:
                    startActivity(new Intent(getApplicationContext()
                            , cerrar_sesion_vendedor.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.perfil_admin:
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
                    startActivity(new Intent(getApplicationContext()
                            , vender.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });*/
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(perfil_admin.this);
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

    //METODO ON START
    @Override
    protected void onStart (){
        info();
        super.onStart();
    }


    //METODO INFO
    private void info(){
        if (user != null){
            Consulta();
        }
    }
    //METODO CONSULTA LLAMADO DE INFO
    private void Consulta(){
        Query query = CVA.orderByChild("correo").equalTo(user.getEmail());
        ((Query) query).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    //OBTENCION DE DATOS
                    String nombreString = ""+ds.child("nombre").getValue();
                    String correoString = ""+ds.child("correo").getValue();
                    String fecha_nacString = ""+ds.child("fechaDeNacimiento").getValue();
                    String generoString = ""+ds.child("genero").getValue();
                    //String foto_perfilString = ""+ds.child("fotoPerfilURL").getValue();

                    //SETEO DE DATOS
                    Nombre.setText(nombreString);
                    Correo.setText(correoString);
                    Fecha_nac.setText(fecha_nacString);
                    Genero.setText(generoString);
                    //Foto_perfil.setImageURI(foto_perfilString);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}