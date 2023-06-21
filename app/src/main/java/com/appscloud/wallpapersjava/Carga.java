package com.appscloud.wallpapersjava;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Carga extends AppCompatActivity {

    private TextView tvAppName, devName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carga);

        //Cambio de fuente
        String rutaFuente = "fuentes/roboto-bold.ttf";
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), rutaFuente);
        tvAppName = findViewById(R.id.app_name);
        devName = findViewById(R.id.dev_name);


        //Tiempo que va tardar la app en lanzarce
        final int DURACION = 1500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent que nos va enviar a MainActivity
                Intent intent = new Intent(Carga.this, MainActivityAdmin.class);
                Log.e("INTENT", "INTENT CLIENTE " + intent);

                startActivity(intent);
                finish();

            }
        }, DURACION);

        tvAppName.setTypeface(typeface);
        devName.setTypeface(typeface);


    }
}