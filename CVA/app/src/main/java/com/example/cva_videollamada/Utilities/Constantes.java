package com.example.cva_videollamada.Utilities;

import java.util.HashMap;

public class Constantes {

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_APELLIDO = "apellido";
    public static final String KEY_EMAIL = "correo";
    public static final String KEY_PASSWORD = "contraseña";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    //acabo de crear esto para los roles, lo dejaré en comentarios porque creo que hacer la constante no es necesario
    //public static final String KEY_IS_ADMIN = "isAdmin";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";

    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    //agregué las constantes de el proyecto anterior
    public static final String URL_FOTO_POR_DEFECTO_USUARIO = "https://firebasestorage.googleapis.com/v0/b/cvadisp.appspot.com/o/foto_perfil%2Ffoto_porDefecto.png?alt=media&token=13e05a39-4cd0-4d65-8861-d7d2d9cfa4f9";
    public static final String NODO_USUARIO = "Usuarios";
    public static final String NODO_MENSAJES = "Mensajes";

    public static HashMap<String, String> getRemoteMessageHeaders(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constantes.REMOTE_MSG_AUTHORIZATION,
                "key=AAAA2IHOWss:APA91bECfvp31bRm3_hsrR2jiZA3RpuR5h44C51OD4LxSu3Dxf52xnAJVeDTB5AFHrFOlQiVaSWkakyxeb9_bHslmcth6u_NdxRJKrIsjdwBSz4gNORKhirMnRvCc7dSirdZASoDfk1E "
        );
        headers.put(Constantes.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }

}
