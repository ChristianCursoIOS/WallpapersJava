package com.appscloud.wallpapersjava.categorias.musicaadmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class ViewHolderMusica extends RecyclerView.ViewHolder {

    private final View mView;

    private ViewHolderMusica.CLickListener mCLickListener;

    //creamos nuestra interface
    public interface CLickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public ViewHolderMusica(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCLickListener.onItemClick(view,getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mCLickListener.onItemLongClick(view, getBindingAdapterPosition());
                return true;
            }
        });
    }

    //Método para presionar o mantener presionado un item
    public void setOnClickListener(ViewHolderMusica.CLickListener clickListener) {
        mCLickListener = clickListener;

    }

    public void establecerMusica(Context context, String nombre, String img, int vistas) {
        ImageView iVMusica;
        TextView tVNombreImgMusica;
        TextView tVVistasMusica;

        //Conexion con el item
        iVMusica = mView.findViewById(R.id.iV_item_musica);
        tVNombreImgMusica = mView.findViewById(R.id.tV_item_img_musica);
        tVVistasMusica = mView.findViewById(R.id.tV_item_vistas_musica);

        tVNombreImgMusica.setText(nombre);

        //Convertimos o casteamos a String el parámetro vistas
        String sVistas = String.valueOf(vistas);
        tVVistasMusica.setText(sVistas);

        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(img).placeholder(R.drawable.icon_categoria).into(iVMusica);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(R.drawable.icon_categoria).into(iVMusica);
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}
