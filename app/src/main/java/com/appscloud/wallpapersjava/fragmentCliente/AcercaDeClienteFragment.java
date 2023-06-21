package com.appscloud.wallpapersjava.fragmentCliente;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.appscloud.wallpapersjava.R;

public class AcercaDeClienteFragment extends Fragment {

    TextView infoWebsite, infoFacebook, infoInstagram, infoYoutube;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_acerca_de_cliente, container,
                false);

        infoWebsite = view.findViewById(R.id.info_website);
        infoFacebook = view.findViewById(R.id.info_facebook);
        infoInstagram = view.findViewById(R.id.info_instagram);
        infoYoutube = view.findViewById(R.id.info_youtube);

        infoWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Para poder dirigirnos al navegador
                Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=6847347025918528804");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);


            }
        });

        infoFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=6847347025918528804");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        infoInstagram.setOnClickListener(view1 -> {
            Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=6847347025918528804");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        infoYoutube.setOnClickListener(view12 -> {
            Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=6847347025918528804");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });


        return view;
    }
}