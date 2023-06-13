package com.appscloud.wallpapersjava.categorias.peliculasadmin;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


public class MainActivityCategoriaPeliculas extends AppCompatActivity {

    private RecyclerView recyclerViewPeliculas;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mReference;

    /*La principal funciona de recycler adapter es que va usar un detector de eventos para poder monitorear los cambios en la consulta de firebase
    esto quiere decir que al momento de agregar o eliminar una imágen esta va estar escuchando en todomomento
    cabe mencionar que para que funcione correctamente debemos declarar un onStart ya que el onStart se ejecuta al abrir
    la aplicación o en este caso al ejecutar la activity*/
    private FirebaseRecyclerAdapter<Pelicula, ViewHolderPelicula> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Pelicula> firebaseRecyclerOptions;

    private SharedPreferences sharedPreferences;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_categoria_peliculas);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Películas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Inicializamos el recyclerView
        recyclerViewPeliculas = findViewById(R.id.recyclerView_peliculas);
        recyclerViewPeliculas.setHasFixedSize(true);//establecemos que el recyclerView se adapte al momento que nosotros eliminemos o agreguemos un nuejvo elemento.

        //inicializamos el fribaseDatabase
        firebaseDatabase = FirebaseDatabase.getInstance();
        mReference = firebaseDatabase.getReference("peliculas_db");

        dialog = new Dialog(this);

        listarImagenesDePeliculas();


    }

    private void eliminarImagen(final String idActual, final String imagenActual) {
        //Creamos nuestro dialogo para elegir si queremos o no eliminar una imágen
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Quiere eliminar la imágen?");
        //
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ELIMINA LA IMAGEN DE LA DB ATRAVES DEL NOMBBRE

                //query es para consultar en nuestra base de datos por nombre
                Query query = mReference.orderByChild("id").equalTo(idActual);

                /*utilizamos el método addListenerForSingleValueEvent para que este escuchando si ourre
                la eliminación de la imágen y usamos la interface ValueEventListener*/
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recorre la base y elimina la imágen que seleccione el admin.
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                        }
                        Toast.makeText(MainActivityCategoriaPeliculas.this, "La imágen ha sido eliminada", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivityCategoriaPeliculas.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

                //para eliminar la imágen de la carpeta de almacenamiento de firebaseStroage
                StorageReference storageReference = getInstance().getReferenceFromUrl(imagenActual);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivityCategoriaPeliculas.this, "La imágen se elimino correctamente", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivityCategoriaPeliculas.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //Si el admin elige la opción no
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivityCategoriaPeliculas.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();//para mostrar el cuadro de diálogo


    }

    //Método para listar nuestras imágenes
    private void listarImagenesDePeliculas() {
        //esta linea es para que pueda leer nuestra base de datos
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Pelicula>().setQuery(mReference, Pelicula.class).build();

        //inicializamos nuestro objeto adapter
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pelicula, ViewHolderPelicula>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderPelicula holder, int position, @NonNull Pelicula pelicula) {
                holder.establecerPelicula(getApplicationContext(), pelicula.getNombre(), pelicula.getImagen(), pelicula.getVistas());


            }

            @NonNull
            @Override
            public ViewHolderPelicula onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflar en el item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
                //inicializamos nuestro ViewHolderPelicula
                ViewHolderPelicula viewHolderPelicula = new ViewHolderPelicula(itemView);
                viewHolderPelicula.setOnClickListener(new ViewHolderPelicula.ClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        Toast.makeText(MainActivityCategoriaPeliculas.this, "Item click corto", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        final String id = getItem(position).getId();
                        final String nombre = getItem(position).getNombre();
                        final String imagen = getItem(position).getImagen();
                        int vistas = getItem(position).getVistas();
                        String sVistas = String.valueOf(vistas);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityCategoriaPeliculas.this);
                        String[] opciones = {"Actualizar", "Eliminar"};
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Menu que se muestra al hacer un click largo sobre alguna imágen
                                //ACTUALIZAR
                                if (i == 0) {
                                    //Estas lineas de código envian los datos de la imágen a la activity agregarpelicula
                                    Intent intent = new Intent(MainActivityCategoriaPeliculas.this, AgregarPeliculaActivity.class);
                                    intent.putExtra("idEnviado", id);
                                    intent.putExtra("nombreImg", nombre);
                                    intent.putExtra("imagen", imagen);
                                    intent.putExtra("numVistas", sVistas);
                                    startActivity(intent);
                                }
                                //ELIMINAR
                                if (i == 1) {
                                    eliminarImagen(id, imagen);

                                }
                            }
                        });

                        builder.create().show();
                    }
                });

                return viewHolderPelicula;
            }
        };

        //Al iniciar la Activity se va listar en dos columnas por defecto
        sharedPreferences = this.getSharedPreferences("Peliculas", MODE_PRIVATE);
        String ordenarEn = sharedPreferences.getString("Ordenar", "Dos");

        //Elegir tipo de vista depende el boton que seleccione el admin
        if (ordenarEn.equals("Dos")) {
            //con esta línea listamos nuestras imágenes en dos columnas
            recyclerViewPeliculas.setLayoutManager(new GridLayoutManager(this, 2));
            firebaseRecyclerAdapter.startListening();//para que el recyclerView este escuchando al momento de agregar o eleiminar imágenes
            recyclerViewPeliculas.setAdapter(firebaseRecyclerAdapter);


        } else if (ordenarEn.equals("Tres")) {
            //con esta línea listamos nuestras imágenes en tres columnas
            recyclerViewPeliculas.setLayoutManager(new GridLayoutManager(this, 3));
            firebaseRecyclerAdapter.startListening();//para que el recyclerView este escuchando al momento de agregar o eleiminar imágenes
            recyclerViewPeliculas.setAdapter(firebaseRecyclerAdapter);

        }

    }

    /*Con este método al abrir  nuestra activity mainActivityCategoriaPeliculas firebaserecycleradapter
    va estas escuchando si las imágenes fueron traidas exitosamente*/
    @Override
    protected void onStart() {
        super.onStart();
        //si no es nulo comience a escuchar si las imágenes fueron traidas exitosamente.
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    //creamos nuestro menú agregar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar, menu);//seleccionamos el menú que queremos que aparezca en la activity
        menuInflater.inflate(R.menu.menu_vista, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_agregar:
                startActivity(new Intent(this, AgregarPeliculaActivity.class));
                break;
            case R.id.it_vista:
                ordenarImagenes();
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    private void ordenarImagenes() {
        //Cambio de letra
        String rutaFuente = "fuentes/roboto-bold.ttf";
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), rutaFuente);

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
