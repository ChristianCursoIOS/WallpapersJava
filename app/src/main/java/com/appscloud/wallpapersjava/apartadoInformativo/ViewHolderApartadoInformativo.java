package com.appscloud.wallpapersjava.apartadoInformativo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class ViewHolderApartadoInformativo extends RecyclerView.ViewHolder {
    private final View mView;

    private ViewHolderApartadoInformativo.ClickListener mCLickListener;


    //creamos nuestra interface
    public interface ClickListener {
        void onItemClick(View view, int position);

    }

    public ViewHolderApartadoInformativo(@NonNull View itemView) {
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
    public void setOnClickListener(ViewHolderApartadoInformativo.ClickListener clickListener) {
        mCLickListener = clickListener;

    }

    public void establecerApartadoInformativo(Context context, String nombre, String img) {
        ImageView iVImagenInformativo;
        TextView tVNombreTituloInformativo;

        //Conexion con el item
        iVImagenInformativo = mView.findViewById(R.id.iV_imagen_informativo);
        tVNombreTituloInformativo = mView.findViewById(R.id.tV_titulo_informativo);

        tVNombreTituloInformativo.setText(nombre);

        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(img).placeholder(R.drawable.icon_categoria).into(iVImagenInformativo);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(img).into(iVImagenInformativo);

        }
    }
}
