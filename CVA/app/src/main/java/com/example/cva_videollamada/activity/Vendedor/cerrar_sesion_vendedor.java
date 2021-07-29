package com.example.cva_videollamada.activity.Vendedor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.RegistroInicioSesion.SignInActivity;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class cerrar_sesion_vendedor extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private Button resendCode;
    private TextView verifyMsg;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_sesion_vendedor);


        //SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        preferenceManager = new PreferenceManager(getApplicationContext());

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.cerrar_Sesion_admin);

        //para enviar el correo de verificación de nuevo
        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);
        fAuth = FirebaseAuth.getInstance();

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cerrar_Sesion_admin:
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
        });

        FirebaseUser user = fAuth.getCurrentUser();

        if(!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(v -> user.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(v.getContext(), "Favor de revisar su correo para el enlace de autenticación", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: Correo no enviado" + e.getMessage());
                }
            }));
        }

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constantes.KEY_NOMBRE),
                preferenceManager.getString(Constantes.KEY_APELLIDO)
        ));

        findViewById(R.id.textSignOut).setOnClickListener(v -> signOut());
    }

    private void signOut(){
        Toast.makeText(this, "Cerrando sesion...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constantes.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constantes.KEY_USER_ID)
                );
        HashMap<String , Object> update = new HashMap<>();
        update.put(Constantes.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(update)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clearPreference();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(cerrar_sesion_vendedor.this, "Unable to sign out ", Toast.LENGTH_SHORT).show());
    }
}