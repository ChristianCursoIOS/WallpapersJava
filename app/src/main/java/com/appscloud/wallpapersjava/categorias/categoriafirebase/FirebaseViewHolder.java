package com.appscloud.wallpapersjava.categorias.categoriafirebase;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.squareup.picasso.Picasso;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {
    private final View mView;

    private FirebaseViewHolder.ClickListener mCLickListener;


    //creamos nuestra interface
    public interface ClickListener {
        void onItemClick(View view, int position);

    }

    public FirebaseViewHolder(@NonNull View itemView) {
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
    public void setOnClickListener(FirebaseViewHolder.ClickListener clickListener) {
        mCLickListener = clickListener;

    }

    public void establecerCategoriaFirebase(Context context, String categoria, String img) {
        ImageView iVCategoriaFirebase;
        TextView tVNombreCategoriaFirebase;

        //Conexion con el item
        iVCategoriaFirebase = mView.findViewById(R.id.iV_categoria_firebase);
        tVNombreCategoriaFirebase = mView.findViewById(R.id.tV_nombre_categoria_firebase);

        tVNombreCategoriaFirebase.setText(categoria);


        //Controlamos posibles errors
        try {
            //Si la imagen se descargo exitosamente de firebase
            Picasso.get().load(img).placeholder(R.drawable.icon_categoria).into(iVCategoriaFirebase);

        } catch (Exception e) {
            //Si ocurrio algun error en la descarga de la imágen
            Picasso.get().load(img).into(iVCategoriaFirebase);

        }
    }
}
