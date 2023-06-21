package com.appscloud.wallpapersjava.categoriaClienteFirebase;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class ViewHolderImgCatFirebaseElegida extends RecyclerView.ViewHolder {

    private final View mView;

    private ViewHolderImgCatFirebaseElegida.ClickListener mCLickListener;


    //creamos nuestra interface
    public interface ClickListener {
        void onItemClick(View view, int position);

    }

    public ViewHolderImgCatFirebaseElegida(@NonNull View itemView) {
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
    public void setOnClickListener(ViewHolderImgCatFirebaseElegida.ClickListener clickListener) {
        mCLickListener = clickListener;

    }

    public void establecerClienteCatFirebase(Context context, String imagen, String nombre, int vistas) {

        ImageView iVClienteCategoriaFirebase;
        TextView tVNombreClienteCategoriaFirebase;
        TextView tVVistasCategoriaElegida;

        //Conexion con el item
        iVClienteCategoriaFirebase = mView.findViewById(R.id.iV_item_cat_elegida_firebase);
        tVNombreClienteCategoriaFirebase = mView.findViewById(R.id.tV_item_img_cat_elegida_firebase);
        tVVistasCategoriaElegida = mView.findViewById(R.id.tV_item_vistas_cat_elegida);

        tVNombreClienteCategoriaFirebase.setText(nombre);
        String numeroVistas = String.valueOf(vistas);//Casteamos a string nuestra variable tipo int vistas
        tVVistasCategoriaElegida.setText(numeroVistas);


        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(imagen).placeholder(R.drawable.icon_categoria).into(iVClienteCategoriaFirebase);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(imagen).into(iVClienteCategoriaFirebase);

        }
    }
}
