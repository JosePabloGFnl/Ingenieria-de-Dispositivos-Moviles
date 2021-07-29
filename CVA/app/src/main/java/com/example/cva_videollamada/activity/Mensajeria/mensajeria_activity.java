package com.example.cva_videollamada.activity.Mensajeria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.activity.Logica.LMensaje;
import com.example.cva_videollamada.activity.Logica.LUsuario;
import com.example.cva_videollamada.activity.ArchivosDAO.MensajeriaDAO;
import com.example.cva_videollamada.activity.Usuario;
import com.example.cva_videollamada.activity.ArchivosDAO.UsuarioDAO;
import com.example.cva_videollamada.adapters.AdaterMensajes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class mensajeria_activity extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;

    private AdaterMensajes adaptador;
    private ImageButton btnEnviarFoto;
    private static final int PHOTO_SEND=1;
    private static final int PHOTO_PERFIL=2;
    private String fotoPerfilCadena;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    FirebaseAuth fAuth;
    private String NOMBRE_USUARIO;

    private String KEY_RECEPTOR;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajeria);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            KEY_RECEPTOR = bundle.getString("key_receptor");
        }else{
            finish();
        }

        fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = "";


        storage = FirebaseStorage.getInstance();

        adaptador = new AdaterMensajes(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adaptador);

        fAuth = FirebaseAuth.getInstance();

        btnEnviar.setOnClickListener(v -> {
            String mensajeEnviar = txtMensaje.getText().toString();
            if(!mensajeEnviar.isEmpty()){
                Mensaje mensaje = new Mensaje();
                mensaje.setMensaje(mensajeEnviar);
                mensaje.setContieneFoto(false);
                mensaje.setKeyEmisor(UsuarioDAO.getKeyUsuario());
                MensajeriaDAO.getInstance().nuevoMensaje(UsuarioDAO.getKeyUsuario(),KEY_RECEPTOR,mensaje);
                txtMensaje.setText("");
            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("imagen/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Seleccion una foto"),PHOTO_SEND);
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("imagen/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Seleccion una foto"),PHOTO_PERFIL);
            }
        });

        // para cambiar la foto de perfil
        //   fotoPerfil.setOnClickListener(new View.OnClickListener(){
        //   @Override
        //  public void onClick(View view){

        //  }
        //  });

        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        FirebaseDatabase.getInstance().getReference(Constantes.NODO_MENSAJES + "/" + UsuarioDAO.getKeyUsuario() + "/" + KEY_RECEPTOR).addChildEventListener(new ChildEventListener() {

            Map<String, LUsuario> mapUsuariosTemporales = new HashMap<>();

            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                final Mensaje mensaje = snapshot.getValue(Mensaje.class);
                final LMensaje lMensaje = new LMensaje(snapshot.getKey(),mensaje);
                final int posicion = adaptador.addMensaje(lMensaje);

                if(mapUsuariosTemporales.get(mensaje.getKeyEmisor())!=null){
                    lMensaje.setlUsuario(mapUsuariosTemporales.get(mensaje.getKeyEmisor()));
                    adaptador.actualizarMensaje(posicion, lMensaje);
                }else{
                    UsuarioDAO.getInstance().obtenerInformacionDeUsuarioPorLlave(mensaje.getKeyEmisor(), new UsuarioDAO.IDevolverUsuario() {
                        @Override
                        public void DevolverUsuario(LUsuario lUsuario) {
                            mapUsuariosTemporales.put(mensaje.getKeyEmisor(),lUsuario);
                            lMensaje.setlUsuario(lUsuario);
                            adaptador.actualizarMensaje(posicion,lMensaje);
                        }

                        @Override
                        public void DevolverError(String error) {
                            Toast.makeText(mensajeria_activity.this, "Error: ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_SEND && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_chat");
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return fotoReferencia.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();
                    Mensaje mensaje = new Mensaje();
                    mensaje.setMensaje("El usuario ha enviado una foto");
                    mensaje.setUrlFoto(uri.toString());
                    mensaje.setContieneFoto(true);
                    mensaje.setKeyEmisor(UsuarioDAO.getKeyUsuario());
                    MensajeriaDAO.getInstance().nuevoMensaje(UsuarioDAO.getKeyUsuario(),KEY_RECEPTOR,mensaje);
                }
            });
        }/*else if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("Imagenes_perfiles");
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());

            fotoReferencia.putFile(u).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fotoReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        fotoPerfilCadena = u.toString();
                        mensajeEnviar m = new mensajeEnviar("Has actualizado foto de perfil",uri.toString(), NOMBRE_USUARIO,fotoPerfilCadena,"2",ServerValue.TIMESTAMP);
                        databaseReference.push().setValue(m);
                        Glide.with(mensajeria_Activity.this).load(uri.toString()).into(fotoPerfil);
                    }
                }
            });
        }*/
    }
}