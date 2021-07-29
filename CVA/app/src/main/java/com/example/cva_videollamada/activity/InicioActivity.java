package com.example.cva_videollamada.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.RegistroInicioSesion.SignInActivity;
import com.example.cva_videollamada.RegistroInicioSesion.SignUpActivity;

import java.util.Locale;

public class InicioActivity extends AppCompatActivity {

    Button boton1;
    Button boton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_inicio);

        boton1 = (Button) findViewById(R.id.iniciar);
        boton2 = (Button) findViewById(R.id.registrar);

        boton1.setOnClickListener(v -> startActivity(new Intent(InicioActivity.this, SignInActivity.class)));
        boton2.setOnClickListener(v -> startActivity(new Intent(InicioActivity.this, SignUpActivity.class)));

        Button changeLang = findViewById(R.id.cambio_lenguaje);
        changeLang.setOnClickListener(view -> showChangeLanguageDialog());
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Español", "Ingles"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(InicioActivity.this);
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

    //SE CONTROLA LA PULSASION DEL BOTON ATRAS
    // lo dejé en comentarios porque me generaba error
//    @Override
  //  public boolean onKeyDown(int keyCode, KeyEvent event){
    //    if(keyCode== KeyEvent.KEYCODE_BACK){
      //      AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //    builder.setMessage(R.string.desea_salir)
          //          .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            //            @Override
              //          public void onClick(DialogInterface dialog, int which) {
                //            Intent intent=new Intent(Intent.ACTION_MAIN);
                  //          intent.addCategory(Intent.CATEGORY_HOME);
                    //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                      //      startActivity(intent);
 //                       }
   //                 })
     //               .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
       //                 @Override
         //               public void onClick(DialogInterface dialog, int which) {
           //                 dialog.dismiss();
             //           }
               //     });
  //          builder.show();
    //    }
      //  return false;
   // }
}