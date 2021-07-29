package com.example.cva_videollamada.activity.ArchivosDAO;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.activity.Logica.LUsuario;
import com.example.cva_videollamada.activity.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsuarioDAO {

    public interface IDevolverUsuario{
        public void DevolverUsuario(LUsuario lUsuario);
        public void DevolverError(String error);
    }

    public interface IDdevolverURLFoto{
        public void DevolverUrlFoto(String url);
    }

    private static UsuarioDAO usuarioDAO;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference referenciaUsuarios;
    private StorageReference storageReferenceFotoPerfil;

    public static UsuarioDAO getInstance() {
        if (usuarioDAO == null) usuarioDAO = new UsuarioDAO();
        return usuarioDAO;
    }

    private UsuarioDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenciaUsuarios = FirebaseDatabase.getInstance().getReference();
        storageReferenceFotoPerfil = storage.getReference("Fotos/FotoPerfil/" + getKeyUsuario());
    }

    public static String getKeyUsuario(){
        return FirebaseAuth.getInstance().getUid();
    }

    public boolean isUsuarioLogeado(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser!=null;
    }

    public long fechaCreacionLong(){
        return  FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp();
    }

    public long fechaDeUltimaConexionLong(){
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp();
    }

    public void obtenerInformacionDeUsuarioPorLlave(final  String key, final IDevolverUsuario iDevolverUsuario){
        referenciaUsuarios.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                LUsuario lUsuario = new LUsuario(key, usuario);
                iDevolverUsuario.DevolverUsuario(lUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                iDevolverUsuario.DevolverError(error.getMessage());
            }
        });
    }

    public void añadirFotodePerfilAUsuariosQueNoTienen(){
        referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LUsuario> lUsuariosListas = new ArrayList<>();
                for(DataSnapshot childDataSnapshot : snapshot.getChildren()){
                    Usuario usuario = childDataSnapshot.getValue(Usuario.class);
                    LUsuario lUsuario = new LUsuario(childDataSnapshot.getKey(),usuario);
                    lUsuariosListas.add(lUsuario);
                }

                for(LUsuario lUsuario : lUsuariosListas){
                    if(lUsuario.getUsuario().getFotoPerfilURL()==null){
                        referenciaUsuarios.child(lUsuario.getKey()).child("fotoPerfilURL").setValue(Constantes.URL_FOTO_POR_DEFECTO_USUARIO);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void subirFotoUri(Uri uri, IDdevolverURLFoto iDdevolverURLFoto){
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS:ss-mm-hh-dd-MM-yyyyy", Locale.getDefault());
        nombreFoto = simpleDateFormat.format(date);
        final StorageReference fotoReferencia = storageReferenceFotoPerfil.child(nombreFoto);
        fotoReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    iDdevolverURLFoto.DevolverUrlFoto(uri.toString());
                }
            }
        });
    }

}

