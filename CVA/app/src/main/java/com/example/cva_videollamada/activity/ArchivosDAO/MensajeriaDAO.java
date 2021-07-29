package com.example.cva_videollamada.activity.ArchivosDAO;

import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.activity.Mensajeria.Mensaje;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MensajeriaDAO {

    private static MensajeriaDAO mensajeriaDAO;
    private FirebaseDatabase database;
    private DatabaseReference referenciaMensaje;

    public static MensajeriaDAO getInstance(){
        if(mensajeriaDAO == null) mensajeriaDAO = new MensajeriaDAO();
        return mensajeriaDAO;
    }

    private MensajeriaDAO(){
        database = FirebaseDatabase.getInstance();
        referenciaMensaje = database.getReference(Constantes.NODO_MENSAJES);
        //storage = FirebaseStorage.getInstance();
        //referenciaUsuarios = FirebaseDatabase.getInstance().getReference();
        //storageReferenceFotoPerfil = storage.getReference("Fotos/FotoPerfil/" + getKeyUsuario());
    }

    public void nuevoMensaje(String keyEmisor, String keyReceptor, Mensaje mensaje){
        DatabaseReference referenceEmisor = referenciaMensaje.child(keyEmisor).child(keyReceptor);
        DatabaseReference referenceReceptor = referenciaMensaje.child(keyReceptor).child(keyEmisor);
        referenceEmisor.push().setValue(mensaje);
        referenceReceptor.push().setValue(mensaje);
    }

}
