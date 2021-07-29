package com.example.cva_videollamada.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.Utilities.Constantes;
import com.example.cva_videollamada.Utilities.PreferenceManager;
import com.example.cva_videollamada.activity.Logica.LUsuario;
import com.example.cva_videollamada.activity.Mensajeria.mensajeria_activity;
import com.example.cva_videollamada.activity.VideoLlamada.OutgoingInvitationActivity;
import com.example.cva_videollamada.adapters.UsersAdapter;
import com.example.cva_videollamada.listeners.UsersListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class verUsuarios_Activity extends AppCompatActivity implements UsersListener {

    private PreferenceManager preferenceManager;
    private RecyclerView rvUsuario;
    private FirebaseRecyclerAdapter adapter;
    private List<Usuario> usuario;
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_usuarios);

        rvUsuario = findViewById(R.id.rvUsuarios);

        preferenceManager = new PreferenceManager(getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvUsuario.setLayoutManager(linearLayoutManager);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constantes.NODO_USUARIO);

        FirebaseRecyclerOptions<Usuario> options =
                new FirebaseRecyclerOptions.Builder<Usuario>()
                        .setQuery(query, Usuario.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Usuario, Usuario_view_Holder>(options) {
            @Override
            public Usuario_view_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_usuario, parent, false);
                return new Usuario_view_Holder(view);
            }

            @Override
            protected void onBindViewHolder(Usuario_view_Holder holder, int position, Usuario model) {
                //Glide.with(verUsuarios_Activity.this).load(model.getFotoPerfilURL()).into(holder.getCivFotoPerfil());
                holder.getTxtNombreUsuario().setText(model.getNombre());

                LUsuario lUsuario = new LUsuario(getSnapshots().getSnapshot(position).getKey(),model);

                holder.getLayoutPrincipal().setOnClickListener(v -> {
                    Intent intent = new Intent(verUsuarios_Activity.this, mensajeria_activity.class);
                    intent.putExtra("key_receptor", lUsuario.getKey());
                    startActivity(intent);
                });
            }
        };


        usuario = new ArrayList<>();
        usersAdapter = new UsersAdapter(usuario, this);

        rvUsuario.setAdapter(adapter);

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constantes.KEY_NOMBRE),
                preferenceManager.getString(Constantes.KEY_APELLIDO)
        ));

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    //tuve que crear estos porque si no me generaba error
    @Override
    public void initiateVideoMeeting(Usuario usuario) {
        Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
        startActivity(intent);
    }

    @Override
    public void initiateCallMeeting(Usuario user) {

    }

    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {

    }
}
