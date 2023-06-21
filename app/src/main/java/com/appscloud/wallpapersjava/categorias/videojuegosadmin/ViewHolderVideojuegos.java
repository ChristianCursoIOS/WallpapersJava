package com.appscloud.wallpapersjava.categorias.videojuegosadmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class ViewHolderVideojuegos extends RecyclerView.ViewHolder {

    private final View mView;

    private ViewHolderVideojuegos.CLickListener mCLickListener;

    public interface CLickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public ViewHolderVideojuegos(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCLickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mCLickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });
    }

    //Método para presionar o mantener presionado un item
    public void setOnClickListener(ViewHolderVideojuegos.CLickListener clickListener) {
        mCLickListener = clickListener;

    }

    public void establecerVideojuego(Context context, String nombre, String img, int vistas) {
        ImageView iVVideojuegos;
        TextView tVNombreImgVideojuegos;
        TextView tVVistasVideojuegos;

        //Conexion con el item
        iVVideojuegos = mView.findViewById(R.id.iV_item_videojuego);
        tVNombreImgVideojuegos = mView.findViewById(R.id.tV_item_img_videojuego);
        tVVistasVideojuegos = mView.findViewById(R.id.tV_item_vistas_videojuego);

        tVNombreImgVideojuegos.setText(nombre);

        //Convertimos o casteamos a String el parámetro vistas
        String sVistas = String.valueOf(vistas);
        tVVistasVideojuegos.setText(sVistas);

        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(img).placeholder(R.drawable.icon_categoria).into(iVVideojuegos);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(R.drawable.icon_categoria).into(iVVideojuegos);
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}
