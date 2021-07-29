package com.example.cva_videollamada.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cva_videollamada.R;
import com.example.cva_videollamada.activity.Comprador.carros_view_descripccion;

import java.util.ArrayList;

public class AdaterCarros extends RecyclerView.Adapter<AdaterCarros.ViewHolder> {

    private int resources;
    private ArrayList<carros_view_descripccion> carrosList;

    public AdaterCarros(ArrayList<carros_view_descripccion> carrosList, int resources){
        this.carrosList = carrosList;
        this.resources = resources;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resources, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterCarros.ViewHolder holder, int position) {

        carros_view_descripccion carro = carrosList.get(position);

        holder.marca_View.setText(carro.getMarca());
        holder.modelo_View.setText(carro.getModelo());
        holder.anio_View.setText(carro.getAnio());
        holder.color_View.setText(carro.getColor());

    }

    @Override
    public int getItemCount() {
        return carrosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView marca_View;
        private TextView modelo_View;
        private TextView color_View;
        private TextView anio_View;
        public View view;

        public ViewHolder(View view){
            super(view);

            this.view = view;
            this.marca_View = (TextView) view.findViewById(R.id.marca_view);
            this.modelo_View = (TextView) view.findViewById(R.id.modelo_view);
            this.color_View = (TextView) view.findViewById(R.id.color_view);
            this.anio_View = (TextView) view.findViewById(R.id.anio_view);

        }

    }

}

