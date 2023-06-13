package com.appscloud.wallpapersjava.categorias.dispositivo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class DispositivoViewHolder extends RecyclerView.ViewHolder {
    private final View mView;

    private DispositivoViewHolder.CLickListener mCLickListener;


    //creamos nuestra interface
    public interface CLickListener {
        void onItemClick(View view, int position);

    }

    public DispositivoViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCLickListener.onItemClick(view, getBindingAdapterPosition());
            }
        });


    }

    //Método para presionar o mantener presionado un item
    public void setOnClickListener(DispositivoViewHolder.CLickListener clickListener) {
        mCLickListener = clickListener;

    }

    public void establecerCategoriaDispositivo(Context context, String categoria, String img) {
        ImageView iVCategoriaDispositivo;
        TextView tVNombreCategoriaDispositivo;

        //Conexion con el item
        iVCategoriaDispositivo = mView.findViewById(R.id.iV_categoria_dispositivo);
        tVNombreCategoriaDispositivo = mView.findViewById(R.id.tV_nombre_categoria_dispositivo);

        tVNombreCategoriaDispositivo.setText(categoria);


        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(img).placeholder(R.drawable.icon_categoria).into(iVCategoriaDispositivo);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(img).into(iVCategoriaDispositivo);

        }
    }

}
