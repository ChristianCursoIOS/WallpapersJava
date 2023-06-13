package com.appscloud.wallpapersjava.categoriaClienteFirebase;

public class ImagenCategoriaElegida {
    //creamos este objeto con el fin de que cuando el usuario cliente desee seleccionar una de las cuatro
    // categorias se puedan listar las imagenes de acuerdo a la categoria que a seleccionado , entonces esa lista de
    // categorias van a necesitar de un item asi como se hizo con las categorias que fueron subidas desde el dispositivo.
    private String id;
    private String imagen;
    private String nombre;
    private int vistas;

    public ImagenCategoriaElegida() {
    }


    public ImagenCategoriaElegida(String id, String imagen, String nombre, int vistas) {
        this.id = id;
        this.imagen = imagen;
        this.nombre = nombre;
        this.vistas = vistas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getVistas() {
        return vistas;
    }

    public void setVistas(int vistas) {
        this.vistas = vistas;
    }
}
