package com.appscloud.wallpapersjava.fragmentCliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appscloud.wallpapersjava.R;


public class CompartirClienteFragment extends Fragment {


    public CompartirClienteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compartir_cliente, container, false);
    }
}