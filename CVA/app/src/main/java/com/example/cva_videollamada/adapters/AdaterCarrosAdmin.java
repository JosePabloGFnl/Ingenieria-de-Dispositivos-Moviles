package com.example.cva_videollamada.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.activity.Vendedor.carros_view_admin;

import java.util.ArrayList;

public class AdaterCarrosAdmin extends RecyclerView.Adapter<AdaterCarrosAdmin.ViewHolder> {

    private int resources;
    private ArrayList<carros_view_admin> carrosList;
    private OnItemClickListener mListener;



    public AdaterCarrosAdmin(ArrayList<carros_view_admin> carrosList, int resources){
        this.carrosList = carrosList;
        this.resources = resources;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);
        ViewHolder vh = new ViewHolder(view, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterCarrosAdmin.ViewHolder holder, int position) {

        carros_view_admin carro = carrosList.get(position);
        //en el tutorial, nuestro carro es currentItem, carros_view_admin es ExampleItem, carroslist es mExampleList, etc.
        holder.marca_View.setText(carro.getMarca());
        holder.modelo_View.setText(carro.getModelo());
        holder.anio_View.setText(carro.getAnio());
        holder.color_View.setText(carro.getColor());

    }

    @Override
    public int getItemCount() {
        return carrosList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    //esto está mero arriba en el video, en el archivo que tiene llamado ExamplerViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView marca_View;
        private TextView modelo_View;
        private TextView color_View;
        private TextView anio_View;
        public View view;
        public Button mEliminarDatos;

        public ViewHolder(View view, OnItemClickListener listener){
            super(view);

            this.view = view;
            this.marca_View = (TextView) view.findViewById(R.id.marca_view);
            this.modelo_View = (TextView) view.findViewById(R.id.modelo_view);
            this.color_View = (TextView) view.findViewById(R.id.color_view);
            this.anio_View = (TextView) view.findViewById(R.id.anio_view);
            mEliminarDatos = (Button) view.findViewById(R.id.ic_delete);

            //view en nuestro programa es itemView para él
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mEliminarDatos.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

        }


    }


}

