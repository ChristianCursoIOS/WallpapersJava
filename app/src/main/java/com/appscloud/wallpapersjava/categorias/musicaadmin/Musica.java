package com.appscloud.wallpapersjava.categorias.musicaadmin;


public class Musica {

    //Atributos
    private String id;
    private String nombre;
    private String imagen;
    private int vistas;

    public Musica() {
    }

    //Inicializamos nuestros atributos con nuestro constructor

    public Musica(String id, String nombre, String imagen, int vistas) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.vistas = vistas;
    }

    //Creamos getter y setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getVistas() {
        return vistas;
    }

    public void setVistas(int vistas) {
        this.vistas = vistas;
    }
}
