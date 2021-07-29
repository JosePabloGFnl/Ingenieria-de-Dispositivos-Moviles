package com.example.cva_videollamada.RegistroInicioSesion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.example.cva_videollamada.activity.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    DatabaseReference mRootReference;
    private EditText inputNombre, inputApellido, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;
    private PreferenceManager preferenceManager;
    FirebaseAuth fAuth;
    //aquí metí el firestore para los roles
    FirebaseFirestore fStore;
    //checkboxes para los roles
    CheckBox isVendedorBox, isCompradorBox;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        preferenceManager = new PreferenceManager(getApplicationContext());

        mRootReference = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.textSignUp).setOnClickListener(v -> onBackPressed());

        inputNombre =findViewById(R.id.inputNombre);
        inputApellido = findViewById(R.id.inputApellido);
        inputEmail =findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword =findViewById(R.id.inputConfirmPassword);
        buttonSignUp =findViewById(R.id.buttonSignUp);
        signUpProgressBar =findViewById(R.id.signupProgressBar);

        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        isVendedorBox = findViewById(R.id.isVendedor);
        isCompradorBox = findViewById(R.id.isComprador);

        //lógica de checkboxes
        isCompradorBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isVendedorBox.setChecked(false);
                }
            }
        });

        isVendedorBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isCompradorBox.setChecked(false);
                }
            }
        });

        //lo creé para la autenticación
        fAuth = FirebaseAuth.getInstance();

        //para los roles
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            signUp();
        }

        buttonSignUp.setOnClickListener(v -> {

            //METER INFORMACION PARA LO DEL CHAT EN EL REALTIME
            final String nombre = inputNombre.getText().toString();
            final String apellido = inputApellido.getText().toString();
            final String correo = inputEmail.getText().toString();

            /*Map<String, Object> datosUsuario = new HashMap<>();
            datosUsuario.put("Nombre", nombre);
            datosUsuario.put("Apellido", apellido);
            datosUsuario.put("Correo", correo);

            mRootReference.child("Usuarios").push().setValue(datosUsuario);*/

            //validación de los checkboxes
            if(!(isVendedorBox.isChecked() || isCompradorBox.isChecked())){
                Toast.makeText(SignUpActivity.this, "Seleccione su rol dentro de la plataforma", Toast.LENGTH_SHORT).show();
                return;
            }


            //strings para autenticación
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if(inputNombre.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this, "Ingresa tu nombre", Toast.LENGTH_SHORT).show();
            }else if(inputApellido.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this, "Ingresa tu apellido", Toast.LENGTH_SHORT).show();
            }else if(inputEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this, "Ingresa tu correo electronico", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
                Toast.makeText(SignUpActivity.this, "Valida tu correo", Toast.LENGTH_SHORT).show();
            }else if(inputPassword.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            }else if(inputConfirmPassword.getText().toString().trim().isEmpty()){
                Toast.makeText(SignUpActivity.this, "Confirma tu contraseña", Toast.LENGTH_SHORT).show();
            }else if(!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())){
                Toast.makeText(SignUpActivity.this, "Las contraseñas deben ser iguales", Toast.LENGTH_SHORT).show();
            }else{
                //modifiqué aquí para hacer la autenticación válida
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // se manda el correo de autenticación
                            //no sé si ya se creó un FirebaseUser antes y si tiene otro nombre, solo estoy siguiendo el video
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Usuario usuario = new Usuario();
                                    usuario.setCorreo(correo);
                                    usuario.setApellido(apellido);
                                    usuario.setNombre(nombre);
                                    FirebaseUser currentUser = fAuth.getCurrentUser();
                                    DatabaseReference reference = database.getReference("Usuarios/" + currentUser.getUid());
                                    reference.setValue(usuario);
                                    Toast.makeText(SignUpActivity.this, "Favor de revisar su correo para el enlace de autenticación", Toast.LENGTH_SHORT).show();
                                    //esto lo acabo de meter para los roles, no sé si ya lo tenemos hecho porque vi que ya tenemos una colección en el firestore, lo dejaré como comentarios
                                    DocumentReference df = fStore.collection("users").document(fuser.getUid());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: Correo no enviado" + e.getMessage());
                                }
                            });


                            Toast.makeText(SignUpActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                            //moví el signup para acá
                            signUp();
                        }else {
                            Toast.makeText(SignUpActivity.this, "Error ! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void signUp(){

        buttonSignUp.setVisibility(View.INVISIBLE);
        //CHECAR PROGRESS BAR
        //signUpProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //esto lo hace en el segundo video de los roles, como ya está hecho pues lo dejaré
        HashMap<String, Object> user = new HashMap<>();

        //SE METE INFORMACION EN EL FIRESTORES
        user.put(Constantes.KEY_NOMBRE, inputNombre.getText().toString());
        user.put(Constantes.KEY_APELLIDO, inputApellido.getText().toString());
        user.put(Constantes.KEY_EMAIL, inputEmail.getText().toString());
        user.put(Constantes.KEY_PASSWORD, inputPassword.getText().toString());

        //aquí se especificará el rol del usuario, no cree una constante porque lo vi innecesario
        if(isVendedorBox.isChecked()){
            user.put("isVendedor", "1");
        }
        if(isCompradorBox.isChecked()){
            user.put("isComprador", "1");
        }

        database.collection(Constantes.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constantes.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constantes.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constantes.KEY_NOMBRE, inputNombre.getText().toString());
                    preferenceManager.putString(Constantes.KEY_APELLIDO, inputApellido.getText().toString());
                    preferenceManager.putString(Constantes.KEY_EMAIL, inputEmail.getText().toString());
                    //implementé lo mismo que en el login porque en el tutorial lo hace para evitar que solo entre al main
                    if(isVendedorBox.isChecked()){
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    if(isCompradorBox.isChecked()){
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }


                })
                .addOnFailureListener(e -> {
                    //CHECAR EL PROGRESS BAR
                    //signUpProgressBar.setVisibility(View.INVISIBLE);
                    buttonSignUp.setVisibility(View.VISIBLE);
                    Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

}

/*FirebaseFirestore database = FirebaseFirestore.getInstance();
    HashMap<String, Object> user = new HashMap<>();
        user.put("Nombre", "Nombre");
        user.put("Apellido", "Apelldio");
        user.put("Correo", "Correo");
        database.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
        @Override
        public void onSuccess(DocumentReference documentReference) {
            Toast.makeText(SignInActivity.this, "User Inserted", Toast.LENGTH_SHORT).show();
        }
    })
            .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(SignInActivity.this, "Error adding user", Toast.LENGTH_SHORT).show();
        }
    });*/