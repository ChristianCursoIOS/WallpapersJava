package com.appscloud.wallpapersjava.fragmentCliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.appscloud.wallpapersjava.R;


public class CompartirClienteFragment extends Fragment {

    Button btnCompartir;


    public CompartirClienteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compartir_cliente, container, false);

        btnCompartir = view.findViewById(R.id.btn_compartir_app);
        btnCompartir.setOnClickListener(compartir -> {
            compartirAplicacion();

        });

        return view;
    }

    private void compartirAplicacion() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String mensaje = "Te invito a descargar ésta app, te encantará\n";
            mensaje = mensaje + "https://play.google.com/store/apps/details?id=com.appscloud.tropicalisima";
            intent.putExtra(Intent.EXTRA_TEXT, mensaje);
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}