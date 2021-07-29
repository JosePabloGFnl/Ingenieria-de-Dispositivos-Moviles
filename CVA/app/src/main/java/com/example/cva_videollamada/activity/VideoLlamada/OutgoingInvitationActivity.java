package com.example.cva_videollamada.activity.VideoLlamada;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.example.cva_videollamada.activity.Usuario;
import com.example.cva_videollamada.network.ApiClient;
import com.example.cva_videollamada.network.ApiService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;

    private TextView textFirstChar;
    private TextView textUserName;
    private TextView textEmail;

    private int rejectionCount = 0;
    private int totalReceivers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        preferenceManager = new PreferenceManager(getApplicationContext());

        //AGREGE UN METODO QUE NO VIENE EN EL VIDEO 9 QUE ESTA HASTA EL FINAL

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingType = getIntent().getStringExtra("type");

        if(meetingType != null){
            if(meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_round_videocam);
            }else {
                imageMeetingType.setImageResource(R.drawable.ic_round_call);
            }
        }

        textFirstChar = findViewById(R.id.textFirstChar);
        textUserName = findViewById(R.id.textUserName);
        textEmail = findViewById(R.id.textEmail);

        Usuario user = (Usuario) getIntent().getSerializableExtra("user");
        if(user != null){
            textFirstChar.setText(user.nombre.substring(0,1));
            //Da error aqui estoy mandando a llamar el nombre y apellido del usuario
            //textUserName.setText(String.format("%s %s", user.nombre, user.apellido));
            textEmail.setText(user.email);
        }

        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(v -> {
            if(getIntent().getBooleanExtra("isMultiple", false)){
                Type type = new TypeToken<ArrayList<Usuario>>(){
                }.getType();
                ArrayList<Usuario> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                canceledInvitation( null, receivers);
            }else {
                if(user != null){
                    canceledInvitation(user.token, null);
                }
            }
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null){
                inviterToken = task.getResult();
                if (meetingType != null){
                    if(getIntent().getBooleanExtra("isMultiple", false)){
                        Type type = new TypeToken<ArrayList<Usuario>>(){
                        }.getType();
                        ArrayList<Usuario> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                        if(receivers != null){
                            totalReceivers = receivers.size();
                        }
                        initiateMeeting(meetingType, null, receivers);
                    }else{
                        if (user != null){
                            totalReceivers = 1;
                            initiateMeeting(meetingType, user.token, null);
                        }
                    }
                }
            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken, ArrayList<Usuario> receivers){
       try {

           JSONArray tokens = new JSONArray();

           if(receiverToken != null){
               tokens.put(receiverToken);
           }

           if(receivers != null && receivers.size() > 0){
               StringBuilder userNames = new StringBuilder();
               for (int i = 0; i < receivers.size(); i++){
                   tokens.put(receivers.get(i).token);
                   userNames.append(receivers.get(i).email).append("\n");
               }
               textFirstChar.setVisibility(View.VISIBLE);
               textEmail.setText(userNames.toString());
               //ESTO PROVOCA QUE LA VIDEOLLAMADA GRUPAL NO FUNCIONE
               //textUserName.setText(userNames.toString());
           }

           JSONObject body = new JSONObject();
           JSONObject data = new JSONObject();

           data.put(Constantes.REMOTE_MSG_TYPE, Constantes.REMOTE_MSG_INVITATION);
           data.put(Constantes.REMOTE_MSG_MEETING_TYPE, meetingType);
           data.put(Constantes.KEY_NOMBRE, preferenceManager.getString(Constantes.KEY_NOMBRE));
           data.put(Constantes.KEY_APELLIDO, preferenceManager.getString(Constantes.KEY_APELLIDO));
           data.put(Constantes.KEY_EMAIL, preferenceManager.getString(Constantes.KEY_EMAIL));
           data.put(Constantes.REMOTE_MSG_INVITER_TOKEN, inviterToken);

           meetingRoom =
                   preferenceManager.getString(Constantes.KEY_USER_ID) + "_" +
                           UUID.randomUUID().toString().substring(0, 5);
           data.put(Constantes.REMOTE_MSG_MEETING_ROOM, meetingRoom);

           body.put(Constantes.REMOTE_MSG_DATA, data);
           body.put(Constantes.REMOTE_MSG_REGISTRATION_IDS, tokens);

           sendRemoteMessage(body.toString(), Constantes.REMOTE_MSG_INVITATION);
           
       }catch (Exception exception){
           Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
           finish();
       }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constantes.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constantes.REMOTE_MSG_INVITATION)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation sent ", Toast.LENGTH_SHORT).show();
                    }else if(type.equals(Constantes.REMOTE_MSG_INVITATION_RESPONSE)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void canceledInvitation(String receiverToken, ArrayList<Usuario> receivers){
        try {

            JSONArray tokens = new JSONArray();

            if(receiverToken != null){
                tokens.put(receiverToken);
            }

            if(receivers != null && receivers.size() > 0){
                for(Usuario user : receivers){
                    tokens.put(user.token);
                }
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constantes.REMOTE_MSG_TYPE, Constantes.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constantes.REMOTE_MSG_INVITATION_RESPONSE, Constantes.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constantes.REMOTE_MSG_DATA, data);
            body.put(Constantes.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constantes.REMOTE_MSG_INVITATION_RESPONSE);

        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constantes.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constantes.REMOTE_MSG_INVITATION_ACCEPTED)){
                    try {
                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);
                        if(meetingType.equals("audio")){
                            builder.setVideoMuted(true);
                        }
                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this, builder.build());
                        finish();
                    }catch (Exception exception){
                        Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else if(type.equals(Constantes.REMOTE_MSG_INVITATION_REJECTED)){
                    rejectionCount += 1;
                    if(rejectionCount == totalReceivers){
                        Toast.makeText(context, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }
    };

    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constantes.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    protected void onStop(){
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    //ESTE METODO FUE AGREGADO PARA QUITAR UN ERROR NO SE SI A FUTURO AFECTE LA APLICACION
    /*private void inviterToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constantes.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constantes.KEY_USER_ID)
                );
        documentReference.update(Constantes.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> Toast.makeText(OutgoingInvitationActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }*/
}