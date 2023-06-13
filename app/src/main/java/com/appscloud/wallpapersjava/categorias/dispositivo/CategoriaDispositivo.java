package com.appscloud.wallpapersjava.categorias.dispositivo;

public class CategoriaDispositivo {
    private String Categoria;
    private String Imagen;

    public CategoriaDispositivo() {
    }

    public CategoriaDispositivo(String categoria, String imagen) {
        Categoria = categoria;
        Imagen = imagen;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }
}
