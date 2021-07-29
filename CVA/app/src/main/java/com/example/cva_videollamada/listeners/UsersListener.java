package com.example.cva_videollamada.listeners;

import com.example.cva_videollamada.activity.Usuario;

public interface UsersListener {

    void initiateVideoMeeting(Usuario user);
    void initiateCallMeeting(Usuario user);
    void onMultipleUsersAction(Boolean isMultipleUsersSelected);

}
