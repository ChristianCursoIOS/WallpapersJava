package com.appscloud.wallpapersjava.fragmentAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.categorias.musicaadmin.CategoriaMusicAdmin;
import com.appscloud.wallpapersjava.categorias.peliculasadmin.MainActivityCategoriaPeliculas;
import com.appscloud.wallpapersjava.categorias.seriesadmin.CategoriaSeriesAdmin;
import com.appscloud.wallpapersjava.categorias.videojuegosadmin.CategoriaVidegamesAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class InicioAdminFragment extends Fragment {

    Button btnPeliculas, btnSeries, btnMusica, btnVGames;
    TextView tVfechaAdmin, tVNombreBienvenida;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReferenceAdmin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio_admin, container, false);

        btnPeliculas = view.findViewById(R.id.btn_peliculas);
        btnSeries = view.findViewById(R.id.btn_series);
        btnMusica = view.findViewById(R.id.btn_musica);
        btnVGames = view.findViewById(R.id.btn_vJuegos);

        tVfechaAdmin = view.findViewById(R.id.tV_fecha_admin);
        tVNombreBienvenida = view.findViewById(R.id.tV_nombre_bienvenida);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReferenceAdmin = FirebaseDatabase.getInstance().getReference("Admin Database");

        //obtenemos la Fecha actual del sistema
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy");
        String formatoFecha = simpleDateFormat.format(date);
        tVfechaAdmin.setText(formatoFecha);


        btnPeliculas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivityCategoriaPeliculas.class));

            }
        });

        btnSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CategoriaSeriesAdmin.class));

            }
        });

        btnMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CategoriaMusicAdmin.class));
            }
        });

        btnVGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CategoriaVidegamesAdmin.class));

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        comprobarUsuarioActivo();
    }

    private void comprobarUsuarioActivo() {
        if (firebaseUser != null) {
            cargarDatos();
        }
    }

    private void cargarDatos() {
        databaseReferenceAdmin.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //si el usuario administrador existe
                if (snapshot.exists()) {
                    //Obetenemos el dato nombre
                    String nombre = "" + snapshot.child("NOMBRES").getValue();
                    tVNombreBienvenida.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}