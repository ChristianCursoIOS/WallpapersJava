package com.appscloud.wallpapersjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.appscloud.wallpapersjava.fragmentAdmin.InicioAdminFragment;
import com.appscloud.wallpapersjava.fragmentAdmin.ListaFragment;
import com.appscloud.wallpapersjava.fragmentAdmin.PerfilAdminFragment;
import com.appscloud.wallpapersjava.fragmentAdmin.RegistroAdminFragment;
import com.appscloud.wallpapersjava.fragmentCliente.AcercaDeClienteFragment;
import com.appscloud.wallpapersjava.fragmentCliente.CompartirClienteFragment;
import com.appscloud.wallpapersjava.fragmentCliente.InicioClienteFragment;
import com.appscloud.wallpapersjava.fragmentCliente.LoginAdminFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivityCliente extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar_cliente);
        setSupportActionBar(toolbar);

        //Inicializamos nuestro drawerLayout
        drawerLayout = findViewById(R.id.drawer_cliente);

        NavigationView navigationView = findViewById(R.id.navigation_view_cliente);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Fragment por defectof
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_cliente,
                    new InicioClienteFragment()).commit();

            navigationView.setCheckedItem(R.id.inicio_cliente);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Con este switch dependiendo la opción seleccionada se mostrara el fragmento correspondiente
        switch (item.getItemId()) {

            case R.id.inicio_cliente:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_cliente,
                        new InicioClienteFragment()).commit();
                break;

            case R.id.acerca_de:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_cliente,
                        new AcercaDeClienteFragment()).commit();
                break;

            case R.id.compartir:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_cliente,
                        new CompartirClienteFragment()).commit();
                break;

            case R.id.administrador:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_cliente,
                        new LoginAdminFragment()).commit();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}