package com.example.cva_videollamada.activity;

import java.io.Serializable;

public class Usuario implements Serializable {

    //implementé lo que estaba en el proyecto anterior

    public String inputNombre;
    public String nombre, apellido, email, token, genero, fotoPerfilURL;
    private long fechaDeNacimiento;

    public Usuario(){

    }

    public String getFotoPerfilURL() {
        return fotoPerfilURL;
    }

    public void setFotoPerfilURL(String fotoPerfilURL) {
        this.fotoPerfilURL = fotoPerfilURL;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return email;
    }

    public void setCorreo(String correo) {
        this.email = correo;
    }

    public long getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setFechaNacimiento(long fechaNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
