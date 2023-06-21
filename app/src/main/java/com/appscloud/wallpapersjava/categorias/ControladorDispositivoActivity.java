package com.appscloud.wallpapersjava.categorias;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.categoriascliente.MusicaClienteActivity;
import com.appscloud.wallpapersjava.categoriascliente.PeliculasClienteActivity;
import com.appscloud.wallpapersjava.categoriascliente.SeriesClienteActivity;
import com.appscloud.wallpapersjava.categoriascliente.VideojuegosClienteActivity;

public class ControladorDispositivoActivity extends AppCompatActivity {
    //Esta clase nos permite gestionar que categoría a elejido el cliente

    //Esta vez lo vamos hacer d euna manera diferente para que nos de incluso la disponibilidad d
    // de añadir nuevas catewgorias sin tener que estar programando nuevas clases.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlador_dispositivo);

        String recibeCategoriaIntent = getIntent().getStringExtra("categoria");

        if (recibeCategoriaIntent.equals("Pelicula")) {
            startActivity(new Intent(this, PeliculasClienteActivity.class));
            finish();

        }
        if (recibeCategoriaIntent.equals("Series")) {
            startActivity(new Intent(this, SeriesClienteActivity.class));
            finish();

        }
        if (recibeCategoriaIntent.equals("Musica")) {
            startActivity(new Intent(this, MusicaClienteActivity.class));
            finish();
        }
        if (recibeCategoriaIntent.equals("Videojuegos")) {
            startActivity(new Intent(this, VideojuegosClienteActivity.class));
            finish();

        }
    }
}