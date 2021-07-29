package com.example.cva_videollamada.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cva_videollamada.R;
import com.example.cva_videollamada.activity.Mensajeria.HolderMensaje;
import com.example.cva_videollamada.activity.Logica.LMensaje;
import com.example.cva_videollamada.activity.Logica.LUsuario;
import com.example.cva_videollamada.activity.ArchivosDAO.UsuarioDAO;

import java.util.ArrayList;
import java.util.List;

public class AdaterMensajes extends RecyclerView.Adapter<HolderMensaje> {

    private List<LMensaje> listmensaje = new ArrayList<>();
    private Context c;

    public AdaterMensajes(Context c) {
        this.c = c;
    }

    public int addMensaje(LMensaje lMensaje){
        listmensaje.add(lMensaje);
        int posicion = listmensaje.size()-1;
        notifyItemInserted(listmensaje.size());
        return posicion;
    }

    public void actualizarMensaje(int posicion, LMensaje lMensaje){
        listmensaje.set(posicion, lMensaje);
        notifyItemChanged(posicion);
    }

    @Override
    public HolderMensaje onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==1){
            view = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes_emisor,parent,false);
        }else{
            view = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes_receptor,parent,false);
        }
        return new HolderMensaje(view);
    }

    @Override
    public void onBindViewHolder(HolderMensaje holder, int position) {

        LMensaje lMensaje = listmensaje.get(position);
        LUsuario lUsuario = lMensaje.getlUsuario();


        //REVISAR MAÑANA YA QUE AL HABILITARLO PROVOCA ERROR, ESTE METODO MANDA A LLAMAR EL NOMBRE DEL USUARIO Y SU FOTO DE PERFIL
        if(lUsuario!=null){
            //holder.getNombre().setText(lUsuario.getUsuario().getNombre());
            //Glide.with(c).load(lUsuario.getUsuario().getFotoPerfilURL()).into(holder.getFotoMensajePerfil());
        }


        holder.getMensaje().setText(lMensaje.getMensaje().getMensaje());
        if(lMensaje.getMensaje().isContieneFoto()){
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(lMensaje.getMensaje().getUrlFoto()).into(holder.getFotoMensaje());
        }else{
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }

        holder.getHora().setText(lMensaje.fechaCreacionMensaje());

    }

    @Override
    public int getItemCount() {
        return listmensaje.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(listmensaje.get(position).getlUsuario()!=null){
            if(listmensaje.get(position).getlUsuario().getKey().equals(UsuarioDAO.getKeyUsuario())){
                return 1;
            }else{
                return -1;
            }
        }else{
            return -1;
        }
        //return super.getItemViewType(position);
    }
}


