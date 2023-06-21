package com.appscloud.wallpapersjava.categorias.seriesadmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class ViewHolderSeries extends RecyclerView.ViewHolder {

    private final View mView;

    private ViewHolderSeries.CLickListener mCLickListener;

    public interface CLickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public ViewHolderSeries(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCLickListener.onItemClick(view, getBindingAdapterPosition());
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
    public void setOnClickListener(ViewHolderSeries.CLickListener clickListener) {
        mCLickListener = clickListener;

    }

    public void establecerSerie(Context context, String nombre, String img, int vistas) {
        ImageView iVSeries;
        TextView tVNombreImgSerie;
        TextView tVVistasSeries;

        //Conexion con el item
        iVSeries = mView.findViewById(R.id.iV_item_serie);
        tVNombreImgSerie = mView.findViewById(R.id.tV_item_img_serie);
        tVVistasSeries = mView.findViewById(R.id.tV_item_vistas_serie);

        tVNombreImgSerie.setText(nombre);
        //Convertimos o casteamos a String el parámetro vistas
        String sVistas = String.valueOf(vistas);
        tVVistasSeries.setText(sVistas);

        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(img).placeholder(R.drawable.icon_categoria).into(iVSeries);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(R.drawable.icon_categoria).into(iVSeries);
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}
