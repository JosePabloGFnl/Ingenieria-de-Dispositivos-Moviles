package com.example.cva_videollamada.RegistroInicioSesion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.example.cva_videollamada.activity.Comprador.cerrar_sesion_comprador;
import com.example.cva_videollamada.activity.VideoLlamada.MainActivity;
import com.example.cva_videollamada.activity.Vendedor.cerrar_sesion_vendedor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInPorgressBar;
    private PreferenceManager preferenceManager;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    //para la opción de olvidar contraseña
    private TextView forgotTextLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.textSignUp).setOnClickListener(v -> onBackPressed());

        //aquí también incluí el firebaseauth
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        findViewById(R.id.textSignUp).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        preferenceManager = new PreferenceManager(getApplicationContext());

        if(preferenceManager.getBoolean(Constantes.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        signInPorgressBar = findViewById(R.id.signInProgressBar);
        forgotTextLink = findViewById(R.id.forgetpassword);

        buttonSignIn.setOnClickListener(v -> {
            if(inputEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(SignInActivity.this, "Ingresa tu correo", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
                Toast.makeText(SignInActivity.this, "Valida tu correo", Toast.LENGTH_SHORT).show();
            }else if(inputPassword.getText().toString().trim().isEmpty()){
                Toast.makeText(SignInActivity.this, "Ingresa Contraseña", Toast.LENGTH_SHORT).show();
            }else{
                signIn();
            }
        });

        //olvidar contraseña

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("¿Desea reiniciar su contraseña?");
                passwordResetDialog.setMessage("Ingrese su correo para reiniciar su contraseña");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //se extrae el correo y se manda el código de reinicar contraseña
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignInActivity.this, "Se ha enviado a su correo el enlace para reiniciar su contraseña", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignInActivity.this, "Error al momento de enviar el correo para reiniciar su contraseña" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //se cierra el diálogo
                    }
                });

                passwordResetDialog.create().show();

            }
        });

    }

    private void signIn(){
       buttonSignIn.setVisibility(View.INVISIBLE);
       signInPorgressBar.setVisibility(View.VISIBLE);


       //metí la autenticación del correo aquí

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override

            // si este onSuccessListener interrumpe el proceso de abajo, entonces hay que borrarlo y dejar esta parte como antes
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignInActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    //dejé esta función como comentario porque "getUser" me causa un error
           //         checkUserAccessLevel(AuthResult.getUser().getUid());
                } else {
                    Toast.makeText(SignInActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



        //tenía planeado copiar y pegar esto en el if de arriba, pero me generaba error en "task"
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constantes.KEY_COLLECTION_USERS)
                .whereEqualTo(Constantes.KEY_EMAIL, inputEmail.getText().toString())
                .whereEqualTo(Constantes.KEY_PASSWORD, inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constantes.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constantes.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constantes.KEY_NOMBRE, documentSnapshot.getString(Constantes.KEY_NOMBRE));
                        preferenceManager.putString(Constantes.KEY_APELLIDO, documentSnapshot.getString(Constantes.KEY_APELLIDO));
                        preferenceManager.putString(Constantes.KEY_EMAIL, documentSnapshot.getString(Constantes.KEY_EMAIL));

                        //agregué estos ifs de la función que descarté para verificar el rol. así debe de funcionar al 100

                        if(documentSnapshot.getString("isVendedor") != null){
                            //el usuario es vendedor
                            Intent intent = new Intent(getApplicationContext(), cerrar_sesion_vendedor.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        if(documentSnapshot.getString("isComprador") != null){
                            Intent intent = new Intent(getApplicationContext(), cerrar_sesion_comprador.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    }else {
                        signInPorgressBar.setVisibility(View.INVISIBLE);
                        buttonSignIn.setVisibility(View.VISIBLE);
                        Toast.makeText(SignInActivity.this, "No puede acceder", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //esta función crea el rol

  //  private void checkUserAccessLevel(String uid) {
    //    DocumentReference df = fstore.collection("users").document(uid);
        //se extraen los datos
      //  df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        //    @Override
          //  public void onSuccess(DocumentSnapshot documentSnapshot) {
            //    Log.d("TAG", "onSuccess: " + documentSnapshot.getData());
              //  //se identifica el rol
               //        if(documentSnapshot.getString("isAdmin") != null){
        //el usuario es vendedor
      //  Intent intent = new Intent(getApplicationContext(), Admin.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      //  startActivity(intent);
   // }
//
  //                      if(documentSnapshot.getString("isUser") != null){
    //    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
   //     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
   //     startActivity(intent);
  //  }


            //}
       // });
   // }




    //esto es para verificar si un usuario ya está logeado, lo implementé al momento de hacer los roles
  //  @Override
//    protected void onStart() {
  //      super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null){
        //    DocumentReference df = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
      //      df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
    //            @Override
  //              public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    if(documentSnapshot.getString("isVendedor") != null){
                    //    startActivity(new Intent(getApplicationContext(),Admin.class));
                  //      finish();
                //    }
              //      if(documentSnapshot.getString("isComprador") != null){
            //            startActivity(new Intent(getApplicationContext(),MainActivity.class));
          //              finish();
        //            }
      //          }
    //        }).addOnFailureListener(new OnFailureListener() {
  //              @Override
//                public void onFailure(@NonNull Exception e) {
            //        FirebaseAuth.getInstance().signOut();
          //          startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        //            finish();
      //          }
    //        });
  //      }
//    }
}