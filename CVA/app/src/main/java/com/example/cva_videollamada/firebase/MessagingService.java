package com.example.cva_videollamada.firebase;


import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.activity.VideoLlamada.IncomingInvitationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

   @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        /*Log.e("newToken", token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();*/

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(Constantes.REMOTE_MSG_TYPE);

        if(type != null){
            if(type.equals(Constantes.REMOTE_MSG_INVITATION)){
                Intent intent = new Intent(getApplicationContext(), IncomingInvitationActivity.class);
                intent.putExtra(
                        Constantes.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(Constantes.REMOTE_MSG_MEETING_TYPE)
                );
                intent.putExtra(
                        Constantes.KEY_NOMBRE,
                        remoteMessage.getData().get(Constantes.KEY_NOMBRE)
                );
                intent.putExtra(
                        Constantes.KEY_APELLIDO,
                        remoteMessage.getData().get(Constantes.KEY_APELLIDO)
                );
                intent.putExtra(
                        Constantes.KEY_EMAIL,
                        remoteMessage.getData().get(Constantes.KEY_EMAIL)
                );
                intent.putExtra(
                        Constantes.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constantes.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        Constantes.REMOTE_MSG_MEETING_ROOM,
                        remoteMessage.getData().get(Constantes.REMOTE_MSG_MEETING_ROOM)
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else if(type.equals(Constantes.REMOTE_MSG_INVITATION_RESPONSE)){
                Intent intent = new Intent(Constantes.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constantes.REMOTE_MSG_INVITATION_RESPONSE,
                        remoteMessage.getData().get(Constantes.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }

    }

   /* public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }*/

}
