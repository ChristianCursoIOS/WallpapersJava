package com.appscloud.wallpapersjava.categoriaClienteFirebase;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.detalleCliente.DetalleImgActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ListaClienteFirebaseActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCatElegida;

    private LinearLayoutManager linearLayoutManagerCatElegida;
    FirebaseDatabase firebaseDatabaseCatElegida;
    private DatabaseReference databaseReferenceCatElegida;
    private FirebaseRecyclerAdapter<ImagenCategoriaElegida, ViewHolderImgCatFirebaseElegida> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<ImagenCategoriaElegida> firebaseRecyclerOptions;
    SharedPreferences sharedPreferences;
    Dialog dialog;
    ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cliente_firebase);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Lista imágenes");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // obtenemos el nombre de la categoria
        String categoriaFriebase_db = getIntent().getStringExtra("nombreCategoria");
        recyclerViewCatElegida = findViewById(R.id.recyclerView_cat_elegida);
        recyclerViewCatElegida.setHasFixedSize(true);

        firebaseDatabaseCatElegida = FirebaseDatabase.getInstance();
        databaseReferenceCatElegida = firebaseDatabaseCatElegida.getReference("categorias_subidas_firebase_db")
                .child(categoriaFriebase_db); // con esta linea de acuerdo al nombre de la categoria
        // que seleccionamos va hacer lectura y muestra las imágenes disponibles

        dialog = new Dialog(ListaClienteFirebaseActivity.this);


        listarCategoriaSeleccionada();


    }

    private void listarCategoriaSeleccionada() {
        //esta linea es para que pueda leer nuestra base de datos
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<ImagenCategoriaElegida>().setQuery(databaseReferenceCatElegida,
                ImagenCategoriaElegida.class).build();

        //inicializamos nuestro objeto adapter
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ImagenCategoriaElegida, ViewHolderImgCatFirebaseElegida>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderImgCatFirebaseElegida holder, int position,
                                            @NonNull ImagenCategoriaElegida model) {
                holder.establecerClienteCatFirebase(getApplicationContext(), model.getImagen(), model.getNombre(),
                        model.getVistas());

            }

            @NonNull
            @Override
            public ViewHolderImgCatFirebaseElegida onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflar en el item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_cat_firebase_elegida,
                        parent, false);
                //inicializamos nuestro ViewHolder
                ViewHolderImgCatFirebaseElegida viewHolderImgCatFirebaseElegida = new ViewHolderImgCatFirebaseElegida(itemView);
                viewHolderImgCatFirebaseElegida.setOnClickListener(new ViewHolderImgCatFirebaseElegida.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //Obtener los datos de la imagen
                        final String id = getItem(position).getId();
                        String imagen = getItem(position).getImagen();
                        String nombreImg = getItem(position).getNombre();
                        int vistas = getItem(position).getVistas();
                        String vistasImg = String.valueOf(vistas);//casteamos nuestra variable vists int a tipo String


                        valueEventListener = databaseReferenceCatElegida.addValueEventListener(new ValueEventListener() {
                            //Este método se llama cada vez que los datos cambien dentor de una ubicacion
                            //pero debemos especificar que dato deseamos cambiar dentro de su ubicacion

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    //creamos un objeto de la clase musics
                                    //realizamos esta linea para darle a entender a la base de datos
                                    //que vamos hacer uso de los atributos del obtejo musica ya que
                                    // como sabemos en el objeto musica vamos a encontrar el dato
                                    // vistas el cual es de tipo int y es por es tambén que hemos
                                    // establecido un constructor vacío ya que necesitamos un
                                    // constructor vacío para realizar la operación de la
                                    // actualización de datos
                                    ImagenCategoriaElegida elegida = dataSnapshot.getValue(ImagenCategoriaElegida.class);
                                    if (elegida.getId().equals(id)) {
                                        int i = 1;
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        // el valor que vamos actualizar
                                        //establecemos que cada vezx que el usuario cliente sleccione
                                        //una imagen su atributo "vistas" incrementará en 1
                                        hashMap.put("vistas", vistas + i);
                                        dataSnapshot.getRef().updateChildren(hashMap);
                                    }
                                }
                            }

                            //se va llamar cuando se cancela la lectura de datos ejemplo se va cancelar
                            // si el cliente no tiene el permiso para leer los datos de una
                            // ubicación en la base de datos de firebase
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Intent intentInfoDetalle = new Intent(ListaClienteFirebaseActivity.this,
                                DetalleImgActivity.class);
                        intentInfoDetalle.putExtra("nombre", nombreImg);
                        intentInfoDetalle.putExtra("vistas", vistasImg);
                        intentInfoDetalle.putExtra("imagen", imagen);
                        startActivity(intentInfoDetalle);

                    }

                    public void onItemLongClick(View view, int position) {

                    }
                });

                return viewHolderImgCatFirebaseElegida;
            }
        };
        //con esta línea listamos nuestras imágenes en dos columnas
        recyclerViewCatElegida.setLayoutManager(new GridLayoutManager(this, 2));
        firebaseRecyclerAdapter.startListening();//para que el recyclerView este escuchando al momento de agregar o eleiminar imágenes
        recyclerViewCatElegida.setAdapter(firebaseRecyclerAdapter);

        //Al iniciar la Activity se va listar en dos columnas
        //gracias sharedpreferences sirve para guardar el estado que elegimos ya sea ordenar
        // en dos o tres columnas
        sharedPreferences = this.getSharedPreferences("Caricaturas", MODE_PRIVATE);
        String ordenarEn = sharedPreferences.getString("Ordenar", "Dos");

        //Elegir tipo de vista
        if (ordenarEn.equals("Dos")) {
            //con esta línea listamos nuestras imágenes en dos columnas
            recyclerViewCatElegida.setLayoutManager(new GridLayoutManager
                    (ListaClienteFirebaseActivity.this, 2));
            firebaseRecyclerAdapter.startListening();//para que el recyclerView este escuchando al momento de agregar o eleiminar imágenes
            recyclerViewCatElegida.setAdapter(firebaseRecyclerAdapter);

        } else if (ordenarEn.equals("Tres")) {
            //con esta línea listamos nuestras imágenes en tress columnas
            recyclerViewCatElegida.setLayoutManager(new GridLayoutManager
                    (ListaClienteFirebaseActivity.this, 3));
            firebaseRecyclerAdapter.startListening();//para que el recyclerView este escuchando al momento de agregar o eleiminar imágenes
            recyclerViewCatElegida.setAdapter(firebaseRecyclerAdapter);
        }
    }

    @Override
    protected void onStart() {
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();

        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // establecemos que el eventlisener se elimine ya que si no establecemos nada dentro de
        // onStop va estar en constantr axtualización
        if (databaseReferenceCatElegida != null && valueEventListener != null) {
            databaseReferenceCatElegida.removeEventListener(valueEventListener);
        }
    }

    //Agregamos el menu en la parte superior derecha
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_vista, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.it_vista) {
            ordenarImagenes();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ordenarImagenes() {
        //Cambio de letra
        String rutaFuente = "fuentes/roboto-bold.ttf";
        Typeface typeface = Typeface.createFromAsset(ListaClienteFirebaseActivity.this.getAssets(), rutaFuente);

        //declaramos nuestras variables
        TextView tVTituloOrdenar;
        Button btnDosColumnas;
        Button btnTresColumnas;

        //Conexión con el cuadro de diálogo
        dialog.setContentView(R.layout.dialog_ordenar);

        //inicializamos nuestras variables
        tVTituloOrdenar = dialog.findViewById(R.id.titulo_ordenar_en);
        btnDosColumnas = dialog.findViewById(R.id.btn_ordenar_dos_columnas);
        btnTresColumnas = dialog.findViewById(R.id.btn_ordenar_tres_columnas);

        //colocamos la fuente roboto
        tVTituloOrdenar.setTypeface(typeface);
        btnDosColumnas.setTypeface(typeface);
        btnTresColumnas.setTypeface(typeface);

        //damos accion a los botónes
        btnDosColumnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creamos nuestro objeto editor
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Ordenar", "Dos");
                editor.apply();
                recreate();
                dialog.dismiss();

            }
        });

        btnTresColumnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creamos nuestro objeto editor
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Ordenar", "Tres");
                editor.apply();
                recreate();
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}