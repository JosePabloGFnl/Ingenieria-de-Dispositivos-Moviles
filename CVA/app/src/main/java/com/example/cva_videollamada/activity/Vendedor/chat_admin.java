package com.example.cva_videollamada.activity.Vendedor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cva_videollamada.adapters.AdaterMensajes;
import com.example.cva_videollamada.activity.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat_admin extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;

    private AdaterMensajes adaptador;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    FirebaseAuth fAuth;
    private String NOMBRE_USUARIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadLocale();

        /*
        setContentView(R.layout.activity_chat_admin);

        fotoPerfil = (CircleImageView) findViewById(R.id.foto_perfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnMensajeEnviar);


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("ChatV2");//SALA DE CHAT V2

        adaptador = new AdaterMensajes(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adaptador);

        fAuth = FirebaseAuth.getInstance();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.push().setValue(new mensajeEnviar(txtMensaje.getText().toString(),NOMBRE_USUARIO,"","1", ServerValue.TIMESTAMP));
                txtMensaje.setText("");
            }
        });

        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                mensajeRecibir m = snapshot.getValue(mensajeRecibir.class);
                adaptador.addMensaje(m);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.chat_admin);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.cerrar_Sesion_admin:
                        startActivity(new Intent(getApplicationContext()
                                , Admin.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.perfil_admin:
                        startActivity(new Intent(getApplicationContext()
                                , perfil_admin.class));
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
            }
        });

       /* Button changeLang = findViewById(R.id.cambio_lenguaje);
        changeLang.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showChangeLanguageDialog();
            }
        });*/
    }
    /*private void showChangeLanguageDialog() {
    final String[] listItems = {"Español", "Ingles"};
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(chat_admin.this);
        mBuilder.setTitle("Lenguaje");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == 0){
                setLocale("es");
                recreate();
            }
            if(which == 1){
                setLocale("en");
                recreate();
            }

            dialog.dismiss();
        }
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
    }*/


    private void setScrollbar(){
        rvMensajes.scrollToPosition(adaptador.getItemCount()-1);
    }
    protected void onResume(){
        super.onResume();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if(currentUser!=null){
            btnEnviar.setEnabled(false);
            DatabaseReference reference = database.getReference("Usuarios/"+currentUser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    NOMBRE_USUARIO = usuario.getNombre();
                    nombre.setText(NOMBRE_USUARIO);
                    btnEnviar.setEnabled(true);
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }else{
            finish();
        }
    }

}