package com.appscloud.wallpapersjava.fragmentAdmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.adapter.Adaptador;
import com.appscloud.wallpapersjava.modelo.Administrador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListaFragment extends Fragment {

    private RecyclerView recyclerViewAdmin;
    private Adaptador adaptador;
    private ArrayList<Administrador> administradorList;
    FirebaseAuth firebaseAuth;


    public ListaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        recyclerViewAdmin = view.findViewById(R.id.recyclerView_administradores);
        recyclerViewAdmin.setHasFixedSize(true);
        recyclerViewAdmin.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        administradorList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        obtenerListaDeUsuariosRegistrados();

        return view;
    }

    private void obtenerListaDeUsuariosRegistrados() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin Database");
        //ordenamos por apellidos
        databaseReference.orderByChild("APELLIDOS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                administradorList.clear();//
                //
                for (DataSnapshot dS : snapshot.getChildren()) {
                    Administrador administrador = dS.getValue(Administrador.class);
                    //Condición para que se visualicen todos los usuasios excepto el que ha iniciado sesión
                    assert administrador != null;
                    assert firebaseUser != null;
                    /*if (!administrador.getUID().equals(firebaseUser.getUid())) {
                        administradorList.add(administrador);

                    }*/
                    administradorList.add(administrador);

                    adaptador = new Adaptador(getActivity(), administradorList);
                    recyclerViewAdmin.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void buscarAdministrador(String consulta) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin Database");
        //ordenamos por apellidos
        databaseReference.orderByChild("APELLIDOS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                administradorList.clear();//
                //
                for (DataSnapshot dS : snapshot.getChildren()) {
                    Administrador administrador = dS.getValue(Administrador.class);
                    //Condición para que se visualicen todos los usuasios excepto el que ha iniciado sesión
                    assert administrador != null;
                    assert firebaseUser != null;
                    if (!administrador.getUID().equals(firebaseUser.getUid())) {
                        //Buscar por nombre y correo
                        //buscamos por nombre y correo, los convertimos esas letras a minsculas y solamente nos agregue en
                        // la lista aquellos admin que sean equivalentes a la consulta en este caso tanto el nombre como el email
                        if (administrador.getNOMBRES().toLowerCase().contains(consulta.toLowerCase())
                                || administrador.getEMAIL().toLowerCase().contains(consulta.toLowerCase())) {
                            administradorList.add(administrador);

                        }

                    }
                    adaptador = new Adaptador(getActivity(), administradorList);
                    recyclerViewAdmin.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //creando menú
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_buscar, menu);
        MenuItem menuItem = menu.findItem(R.id.buscar_admin);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //El método onQueryTextSubmit se llama cuando el usuario presiona el botón de búsqueda desde el teclado
            @Override
            public boolean onQueryTextSubmit(String consulta) {
                //Si la consulta de búsqueda no esta vacío,entonces que se ejecute el método buscarAdministrador
                if (!TextUtils.isEmpty(consulta.trim())) {
                    buscarAdministrador(consulta);
                    //Se la búsqueda es vacía que muestr toda la lista
                } else {
                    obtenerListaDeUsuariosRegistrados();
                }

                return false;
            }

            //onQueryTextChange sirve para que la búsqueda se va actualizando conforme yamos escribiendo
            @Override
            public boolean onQueryTextChange(String consulta) {
                //Si el campo búsqueda no es vacío se va ejecutar el método buscarAdministrador(consulta
                // pasandole como parámetro la consulta que nuevamente hacemos referencia a lo que ayamos
                // escribiendo desde el teclado)
                if (!TextUtils.isEmpty(consulta.trim())) {
                    buscarAdministrador(consulta);

                } else {
                    //si el campo búsqueda esta vacío que nos muestre toda la lista
                    obtenerListaDeUsuariosRegistrados();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }

    //Visualizr el menú
    //esto lo hacemos ya que estamos trabajando desde un Fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}