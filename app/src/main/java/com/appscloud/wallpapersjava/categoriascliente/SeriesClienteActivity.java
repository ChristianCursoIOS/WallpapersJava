package com.appscloud.wallpapersjava.categoriascliente;

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
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.categorias.seriesadmin.Serie;
import com.appscloud.wallpapersjava.categorias.seriesadmin.ViewHolderSeries;
import com.appscloud.wallpapersjava.detalleCliente.DetalleImgActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SeriesClienteActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSeriesCliente;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mReference;

    /*La principal funciona de recycler adapter es que va usar un detector de eventos para poder monitorear los cambios en la consulta de firebase
    esto quiere decir que al momento de agregar o eliminar una imágen esta va estar escuchando en todomomento
    cabe mencionar que para que funcione correctamente debemos declarar un onStart ya que el onStart se ejecuta al abrir
    la aplicación o en este caso al ejecutar la activity*/
    private FirebaseRecyclerAdapter<Serie, ViewHolderSeries> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Serie> firebaseRecyclerOptions;
    private SharedPreferences sharedPreferences;
    private Dialog dialog;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_cliente);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Series");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Inicializamos el recyclerView
        recyclerViewSeriesCliente = findViewById(R.id.recyclerView_series_cliente);
        recyclerViewSeriesCliente.setHasFixedSize(true);//establecemos que el recyclerView se adapte al momento que nosotros eliminemos o agreguemos un nuejvo elemento.

        //inicializamos el fribaseDatabase
        firebaseDatabase = FirebaseDatabase.getInstance();
        mReference = firebaseDatabase.getReference("imgSeries_db");

        dialog = new Dialog(SeriesClienteActivity.this);

        listarImagenesDeSeries();
    }

    private void listarImagenesDeSeries() {
        //esta linea es para que pueda leer nuestra base de datos
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Serie>().setQuery(mReference,
                Serie.class).build();

        //inicializamos nuestro objeto adapter
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Serie, ViewHolderSeries>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderSeries holder, int position, @NonNull Serie serie) {
                holder.establecerSerie(getApplicationContext(), serie.getNombre(), serie.getImagen(),
                        serie.getVistas());

            }

            @NonNull
            @Override
            public ViewHolderSeries onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflar en el item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie,
                        parent, false);
                //inicializamos nuestro ViewHolder
                ViewHolderSeries viewHolderSeries = new ViewHolderSeries(itemView);
                viewHolderSeries.setOnClickListener(new ViewHolderSeries.CLickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //Obtener los datos de la imagen
                        String imagen = getItem(position).getImagen();
                        String nomnbreImg = getItem(position).getNombre();
                        final int vistas = getItem(position).getVistas();
                        String vistasImg = String.valueOf(vistas);

                        final String id = getItem(position).getId();

                        valueEventListener = mReference.addValueEventListener(new ValueEventListener() {
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
                                    Serie serie = dataSnapshot.getValue(Serie.class);
                                    if (serie.getId().equals(id)) {
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


                        Intent intentInfoDetalle = new Intent(SeriesClienteActivity.this,
                                DetalleImgActivity.class);

                        intentInfoDetalle.putExtra("nombre", nomnbreImg);
                        intentInfoDetalle.putExtra("vistas", vistasImg);
                        intentInfoDetalle.putExtra("imagen", imagen);
                        startActivity(intentInfoDetalle);


                    }

                    @Override
                    public void onItemLongClick(View view, int position) {


                    }
                });

                return viewHolderSeries;
            }
        };

        //Al iniciar la Activity se va listar en dos columnas
        sharedPreferences = this.getSharedPreferences("Series", MODE_PRIVATE);
        String ordenarEn = sharedPreferences.getString("Ordenar", "Dos");

        //Elegir tipo de vista
        if (ordenarEn.equals("Dos")) {
            //con esta línea listamos nuestras imágenes en dos columnas
            recyclerViewSeriesCliente.setLayoutManager(new GridLayoutManager(this, 2));
            firebaseRecyclerAdapter.startListening();//para que el recyclerView este escuchando al momento de agregar o eleiminar imágenes
            recyclerViewSeriesCliente.setAdapter(firebaseRecyclerAdapter);


        } else if (ordenarEn.equals("Tres")) {
            //con esta línea listamos nuestras imágenes en tress columnas
            recyclerViewSeriesCliente.setLayoutManager(new GridLayoutManager(this, 3));
            firebaseRecyclerAdapter.startListening();//para que el recyclerView este escuchando al momento de agregar o eleiminar imágenes
            recyclerViewSeriesCliente.setAdapter(firebaseRecyclerAdapter);

        }

    }

    /*Con este método al abrir  nuestra activity seriesclientrActivity firebaserecycleradapter
    va estas escuchando si las imágenes fueron traidas exitosamente*/
    @Override
    protected void onStart() {
        super.onStart();
        //si no es nulo comience a escuchar si las imágenes fueron traidas exitosamente.
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // establecemos que el eventlisener se elimine ya que si no establecemos nada dentro de
        // onStop va estar en constantr axtualización
        if (mReference != null && valueEventListener != null) {
            mReference.removeEventListener(valueEventListener);
        }
    }

    //creamos nuestro menú agregar
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
        Typeface typeface = Typeface.createFromAsset(SeriesClienteActivity.this.getAssets(), rutaFuente);

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