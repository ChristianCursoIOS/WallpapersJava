package com.appscloud.wallpapersjava.categorias.peliculasadmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class ViewHolderPelicula extends RecyclerView.ViewHolder {

    private final View mView;

    private ViewHolderPelicula.ClickListener mClickListener;

    public interface ClickListener {

        //va recibir una vista y la posicion del elemento que leeido de firebase va tener una posición
        void OnItemClick(View view, int position); //método para cuando se presione la imagen

        void OnItemLongClick(View view, int position);//método para cuando se mantiene presionado la imagen cierto tiempo
    }

    public ViewHolderPelicula(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.OnItemClick(view, getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.OnItemLongClick(view, getBindingAdapterPosition());
                return true;
            }
        });


    }

    //Método para presionar o mantener presionado un item
    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;

    }

    //Método que sirve para poder pasar por parámetro el nombre,imágen y el número de vistas.
    public void establecerPelicula(Context context, String nombre, String img, int vistas) {
        ImageView iVPelicula;
        TextView tVNombreImg;
        TextView tVVistasPelicula;

        //Conexion con el item
        iVPelicula = mView.findViewById(R.id.iV_item_pelicula);
        tVNombreImg = mView.findViewById(R.id.tV_item_img_peliculas);
        tVVistasPelicula = mView.findViewById(R.id.tV_item_vistas_peliculas);

        tVNombreImg.setText(nombre);

        //Convertimos o casteamos a String el parámetro vistas
        String sVistas = String.valueOf(vistas);
        tVVistasPelicula.setText(sVistas);

        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(img).placeholder(R.drawable.icon_categoria).into(iVPelicula);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(R.drawable.icon_categoria).into(iVPelicula);
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}
