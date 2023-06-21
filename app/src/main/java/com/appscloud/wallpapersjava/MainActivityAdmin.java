package com.appscloud.wallpapersjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.appscloud.wallpapersjava.fragmentAdmin.InicioAdminFragment;
import com.appscloud.wallpapersjava.fragmentAdmin.ListaFragment;
import com.appscloud.wallpapersjava.fragmentAdmin.PerfilAdminFragment;
import com.appscloud.wallpapersjava.fragmentAdmin.RegistroAdminFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityAdmin extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        //Inicializamos nuestro drawerLayout
        drawerLayout = findViewById(R.id.drawer_admin);

        NavigationView navigationView = findViewById(R.id.navigation_View_admin);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Inicializamos nuestro firebaseauth y user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //Esta linea obtiene al admin actual

        //Fragment por defecto o predeterminado
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new InicioAdminFragment()).commit();

            navigationView.setCheckedItem(R.id.inicio_admin);

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Con este switch dependiendo la opción seleccionada se mostrara el fragmento correspondiente
        switch (item.getItemId()) {

            case R.id.inicio_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new InicioAdminFragment()).commit();
                break;

            case R.id.perfil_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PerfilAdminFragment()).commit();
                break;

            case R.id.registro_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RegistroAdminFragment()).commit();
                break;

            case R.id.listar_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ListaFragment()).commit();
                break;

            case R.id.cerrar_sesion:
                cerrarSesion();
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void comprobarInicioDeSesion() {

        if (firebaseUser != null) {
            //Si el usuario es un admin inicia sesión
            Toast.makeText(MainActivityAdmin.this, "Se ha iniciado sesión",
                    Toast.LENGTH_SHORT).show();

        } else {
            //Si no se inicia sesión es por que el usuario es un cliente
            startActivity(new Intent(MainActivityAdmin.this, MainActivityCliente.class));
            finish();

        }
    }

    private void cerrarSesion() {
        firebaseAuth.signOut();
        //Al cerrar nos dirijira a la avtivity cliente
        startActivity(new Intent(MainActivityAdmin.this, MainActivityCliente.class));
        Toast.makeText(this, "Cerro sesión exitosamente", Toast.LENGTH_LONG).show();

    }


    //Con este método al iniciarse el MainActivityAdmin hacemos que sea lo primero que se ejecute
    @Override
    protected void onStart() {
        comprobarInicioDeSesion();
        super.onStart();
    }

    /*Con el metodo comprobarInicioDeSesion hacemos lo siguuiente; pasando la activity de carga
    nos dirigimos a la MainActivityAdmin luego se ferifica si el user no es nulo y si no es nulo el user
    es admin de lo contrario el usuario es cliente y nos dirigira al MainActivityCliente*/

}