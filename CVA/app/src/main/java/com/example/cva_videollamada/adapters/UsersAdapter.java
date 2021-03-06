package com.example.cva_videollamada.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.listeners.UsersListener;
import com.example.cva_videollamada.activity.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<Usuario> users;
    private UsersListener usersListener;
    private List<Usuario> selectedUsers;

    public UsersAdapter(List<Usuario> users, UsersListener usersListener) {
        this.users = users;
        this.usersListener = usersListener;
        selectedUsers = new ArrayList<>();
    }

    public  List<Usuario> getSelectedUsers(){
        return selectedUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_user,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView textPrimerChar, textUsername, textEmail;
        ImageView imageAudioMeeting, imageVideoMeeting;
        ConstraintLayout userContainer;
        ImageView imageSelected;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textPrimerChar = itemView.findViewById(R.id.textPrimerChar);
            textUsername = itemView.findViewById(R.id.textUserName);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageAudioMeeting = itemView.findViewById(R.id.imageCallMeeting);
            imageVideoMeeting = itemView.findViewById(R.id.imageVideoMeeting);
            userContainer = itemView.findViewById(R.id.userContainer);
            imageSelected = itemView.findViewById(R.id.imageSelected);
        }

        void setUserData(Usuario user){
            //textPrimerChar.setText(user.nombre.substring(0,1));
            textUsername.setText(String.format("%s %s", user.nombre, user.apellido));
            textEmail.setText(user.email);
            imageAudioMeeting.setOnClickListener(v -> usersListener.initiateCallMeeting(user));
            imageVideoMeeting.setOnClickListener(v -> usersListener.initiateVideoMeeting(user));

            userContainer.setOnLongClickListener(v -> {
                if(imageSelected.getVisibility() != View.VISIBLE){
                    selectedUsers.add(user);
                    imageSelected.setVisibility(View.VISIBLE);
                    imageVideoMeeting.setVisibility(View.GONE);
                    imageAudioMeeting.setVisibility(View.GONE);
                    usersListener.onMultipleUsersAction(true);
                }
                return true;
            });

            userContainer.setOnClickListener(v -> {
                if (imageSelected.getVisibility() == View.VISIBLE){
                    selectedUsers.remove(user);
                    imageSelected.setVisibility(View.GONE);
                    imageVideoMeeting.setVisibility(View.VISIBLE);
                    imageAudioMeeting.setVisibility(View.VISIBLE);
                    if(selectedUsers.size() == 0){
                        usersListener.onMultipleUsersAction(false);
                    }
                }else{
                    if(selectedUsers.size() > 0){
                        selectedUsers.add(user);
                        imageSelected.setVisibility(View.VISIBLE);
                        imageVideoMeeting.setVisibility(View.GONE);
                        imageAudioMeeting.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

}
