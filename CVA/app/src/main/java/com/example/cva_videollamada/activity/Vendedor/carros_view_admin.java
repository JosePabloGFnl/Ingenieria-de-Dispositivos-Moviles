package com.example.cva_videollamada.activity.Vendedor;

public class carros_view_admin {

    private String marca;
    private String color;
    private String modelo;
    private String anio;

    public carros_view_admin() {
    }

    public carros_view_admin(String marca, String color, String modelo, String anio) {
        this.marca = marca;
        this.color = color;
        this.modelo = modelo;
        this.anio = anio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }
}

