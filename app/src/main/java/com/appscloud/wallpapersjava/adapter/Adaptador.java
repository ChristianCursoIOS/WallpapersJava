package com.appscloud.wallpapersjava.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.fragmentAdmin.activity.DetalleAdminActivity;
import com.appscloud.wallpapersjava.modelo.Administrador;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyViewHolder> {

    Context context;
    ArrayList<Administrador> administradores;
    private final String ADMIN_DETAIL_KEY = "administrador";


    //Inicializamos nuestras dos variables contex y administradores
    public Adaptador(Context context, ArrayList<Administrador> administradores) {
        this.context = context;
        this.administradores = administradores;
    }

    //onCreateViewHolder Sirve para inflar el layout en este caso es el admin_item
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item, parent, false);

        return new MyViewHolder(view);
    }


    //onBindViewHolder Obtiene los datos del model en este caso el modelo Administrador
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Obtenemos los dados del modelo
        Administrador administrador = new Administrador(administradores.get(position).getUID(),
                administradores.get(position).getNOMBRES(), administradores.get(position).getAPELLIDOS(),
                administradores.get(position).getEMAIL(), administradores.get(position).getIMAGEN(),
                administradores.get(position).getEDAD());


        /*String UID = administradores.get(position).getUID();
        String NOMBRES = administradores.get(position).getNOMBRES();
        String APELLIDOS = administradores.get(position).getAPELLIDOS();
        String EMAIL = administradores.get(position).getEMAIL();
        String IMAGEN = administradores.get(position).getIMAGEN();
        int EDAD = administradores.get(position).getEDAD();
        String edadString = String.valueOf(EDAD);//casteamos la edad de int a String
        Administrador admin2 = new Administrador(UID, NOMBRES, APELLIDOS, EMAIL, IMAGEN, EDAD);*/

        //colocamos los datos en el item del recycler
        holder.tVNombresAdminItem.setText(administrador.getNOMBRES());
        holder.tVEmailAdminItem.setText(administrador.getEMAIL());

        //try para imagen
        try {
            //Si la imagen existe de la base de datos, entonces la imagen se va cargar en nuesro iVFotoPerfilItem
            //NOTA: Cuando establecemos el placeholder estamos declarando que el icono se va visualizar temporalmente
            //hasta que se cargue la imagen de la base de datos, simplemente el icono se oculta y se mostraria la imagen traida de la base de datos
            Picasso.get().load(administrador.getIMAGEN()).placeholder(R.drawable.admin_perfil).into(holder.iVFotoPerfilItem);

        } catch (Exception e) {
            //Si la imagen del admin no existe se coloca la imagen perfil_item
            Picasso.get().load(R.drawable.admin_perfil).into(holder.iVFotoPerfilItem);
        }

        //interface que sirve para hacer algo cuando toquemos un elemento de la lista en pantalla.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoAdminIntent = new Intent(context, DetalleAdminActivity.class);
                //Pasamos datos por separado

               /* infoAdminIntent.putExtra("UID", UID);
                infoAdminIntent.putExtra("NOMBRES", NOMBRES);
                infoAdminIntent.putExtra("APELLIDOS", APELLIDOS);
                infoAdminIntent.putExtra("EMAIL", EMAIL);
                infoAdminIntent.putExtra("EDAD", edadString);
                infoAdminIntent.putExtra("IMAGEN", IMAGEN);*/

                infoAdminIntent.putExtra("administrador", administrador);
                context.startActivity(infoAdminIntent);
                Log.d("IntentDetalle", "EnviandoAdmin " + administrador);

            }
        });

    }
    // PRUEBA ENVIANDO COMMIT

    //getItemCount Sirve para recorrer la lista de los administradores registrados en la base de datos y asi
    // obtenemos el tamaño de la lista
    @Override
    public int getItemCount() {
        return administradores.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //Declaramos vistas
        ImageView iVFotoPerfilItem;
        TextView tVNombresAdminItem;
        TextView tVEmailAdminItem;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //Inicializamos nuestras vistas

            iVFotoPerfilItem = itemView.findViewById(R.id.iV_foto_perfil_item);
            tVNombresAdminItem = itemView.findViewById(R.id.tV_nombres_admin_item);
            tVEmailAdminItem = itemView.findViewById(R.id.tV_email_admin_item);
        }
    }
}
