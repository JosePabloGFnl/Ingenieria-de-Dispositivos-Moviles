package com.example.cva_videollamada.activity.Comprador;

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

public class cerrar_sesion_comprador extends AppCompatActivity {

    private PreferenceManager preferenceManager;;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_sesion_comprador);

        //SECCION DE LA NAVEGACION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        preferenceManager = new PreferenceManager(getApplicationContext());

        //para enviar el correo de verificación de nuevo
        fAuth = FirebaseAuth.getInstance();

        //home selected
        bottomNavigationView.setSelectedItemId(R.id.cerrar_Sesion);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.cerrar_Sesion:
                    return true;

                case R.id.comprar:
                    startActivity(new Intent(getApplicationContext()
                            , comprar_Activity.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.mensajeria:
                    startActivity(new Intent(getApplicationContext()
                            ,Menu_chat_activity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        FirebaseUser user = fAuth.getCurrentUser();


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
                .addOnFailureListener(e -> Toast.makeText(cerrar_sesion_comprador.this, "Unable to sign out ", Toast.LENGTH_SHORT).show());
    }

}