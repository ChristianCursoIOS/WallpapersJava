package com.appscloud.wallpapersjava.fragmentCliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appscloud.wallpapersjava.MainActivityInicioSesion;
import com.appscloud.wallpapersjava.R;


public class LoginAdminFragment extends Fragment {

    Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_admin, container, false);

        btnLogin = view.findViewById(R.id.btn_login_admin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivityInicioSesion.class));
            }
        });

        return view;
    }
}