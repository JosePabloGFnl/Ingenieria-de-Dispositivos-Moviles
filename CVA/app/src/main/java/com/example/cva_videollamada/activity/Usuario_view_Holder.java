package com.example.cva_videollamada.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.listeners.UsersListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Usuario_view_Holder extends RecyclerView.ViewHolder{

    private CircleImageView civFotoPerfil;
    private TextView txtNombreUsuario, inputNombre;
    private LinearLayout layoutPrincipal;
    ImageView imageVideoMeeting;
    private List<Usuario> usuario;
    public UsersListener usersListener;


    public Usuario_view_Holder(@NonNull View itemView) {
        super(itemView);
        //civFotoPerfil = itemView.findViewById(R.id.civFotoPerfil);
        txtNombreUsuario = itemView.findViewById(R.id.inputNombre);
        layoutPrincipal = itemView.findViewById(R.id.layoutPrincipal);
        //imageVideoMeeting = itemView.findViewById(R.id.imageVideoMeeting);
    }

    public TextView getInputNombre() {
        return inputNombre;
    }

    public void setInputNombre(TextView inputNombre) {
        this.inputNombre = inputNombre;
    }

    public CircleImageView getCivFotoPerfil() {
        return civFotoPerfil;
    }

    public void setCivFotoPerfil(CircleImageView civFotoPerfil) {
        this.civFotoPerfil = civFotoPerfil;
    }

    public TextView getTxtNombreUsuario() {
        return txtNombreUsuario;
    }

    public void setTxtNombreUsuario(TextView txtNombreUsuario) {
        this.txtNombreUsuario = txtNombreUsuario;
    }

    public LinearLayout getLayoutPrincipal() {
        return layoutPrincipal;
    }

    public void setLayoutPrincipal(LinearLayout layoutPrincipal) {
        this.layoutPrincipal = layoutPrincipal;
    }

}
