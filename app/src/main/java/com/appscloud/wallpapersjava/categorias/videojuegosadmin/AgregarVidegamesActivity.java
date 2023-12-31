package com.appscloud.wallpapersjava.categorias.videojuegosadmin;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.categorias.seriesadmin.Serie;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AgregarVidegamesActivity extends AppCompatActivity {

    private TextView tVVistasVideojuego, idVGames;
    private EditText eTNombreVideojuego;
    private ImageView iVAgregarVideojuego;

    private Uri rutaArchivoUri = null;
    private final String rutaDeAlmacenamiento = "imgVideojuegos_subida/";


    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String recibirId, recibirIntentNombre, recibirIntentImagen, recibirIntentVistas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_videgames);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //Afirmamos que actionBNar no es nulo
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Inicializamos nuestras variables del xml
        idVGames = findViewById(R.id.id_vgames);
        tVVistasVideojuego = findViewById(R.id.tV_vistas_videojuegos);
        eTNombreVideojuego = findViewById(R.id.eT_escribe_nombre_vGames);
        iVAgregarVideojuego = findViewById(R.id.iV_agregar_videojuego);
        Button btnPublicarVideojuego = findViewById(R.id.btn_publicar_videojuego);

        //Inikcializamos nuestras variables de Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        final String rutaDeBaseDeDatos = "imgVideojuegos_db";
        databaseReference = FirebaseDatabase.getInstance().getReference(rutaDeBaseDeDatos); //referencia a nuestra database
        progressDialog = new ProgressDialog(this);

        //Recibir intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //Recibir los datos de la MainActivityCategoriaPeliculas
            recibirId = bundle.getString("idEnviado");
            recibirIntentNombre = bundle.getString("nombreImg");
            recibirIntentImagen = bundle.getString("imagen");
            recibirIntentVistas = bundle.getString("numVistas");

            //Colocar los datos en sus respectivos campos
            idVGames.setText(recibirId);
            eTNombreVideojuego.setText(recibirIntentNombre);
            tVVistasVideojuego.setText(recibirIntentVistas);
            Picasso.get().load(recibirIntentImagen).into(iVAgregarVideojuego);

            //Cambiar nombre en actionbar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";

            //Cambiar nombre del borón
            btnPublicarVideojuego.setText(actualizar);

        }

        //


        iVAgregarVideojuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria();
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar imágen"), REQUEST_CODE_GALERIA);
                //Cuando el admin abra la galeria y selecciones la imágen la*/
                //que va subir en este caso pelicula por eso mandamos como parámetro el request code
                // para posteriormente identificar si la imágen fue seleccionada correctamente o no.

            }
        });

        btnPublicarVideojuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnPublicarVideojuego.getText().equals("Publicar")) {
                    subirImagen();


                } else {
                    actualizarImagen();
                }
            }
        });
    }

    private void actualizarImagen() {
        //Crreemoa nuestro progressdialog
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        eliminarImagenAnterior();

    }

    private void eliminarImagenAnterior() {
        //Declaramos storageReference para pasarle como parametro la imagen que deseamos eliminar
        StorageReference storageReferenceImage = getInstance().getReferenceFromUrl(recibirIntentImagen);
        storageReferenceImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            //si se elimina del almacenamiento de firebase se ejecuta el método subirnuevaimagen
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AgregarVidegamesActivity.this, "La imágen anterior a sido eliminada",
                        Toast.LENGTH_SHORT).show();
                subirNuevaImagen();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarVidegamesActivity.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

    }

    private void subirNuevaImagen() {
        //Declaramos como STring la nueva imágen que se va a cargar
        String nuemvaImagen = System.currentTimeMillis() + ".png"; //currentTimeMillis nos va devolver la hora actual del sistema en milisegundos
        //Declaramos storageReference para poder almacenar esta imagen en la carpeta de storage
        StorageReference storageReferenceSubNueImg = storageReference.child(
                rutaDeAlmacenamiento + nuemvaImagen);
        //Creamos nuestro bitmap de la nueva imágen que seleccionamos para posteriormente comprimirla
        Bitmap bitmap = ((BitmapDrawable) iVAgregarVideojuego.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = storageReferenceSubNueImg.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarVidegamesActivity.this, "Nueva imágen cargada",
                        Toast.LENGTH_SHORT).show();
                //obtenemos la URL de la imágen cecién cargada
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downLoadUri = uriTask.getResult();
                //Actualizamos la base de datos con los nuevo datos, la cúal manda como parámetro
                //la imágen ya convertida a String.
                actualizarImagenEnLaBaseDeDatos(downLoadUri.toString());


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarVidegamesActivity.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void actualizarImagenEnLaBaseDeDatos(String nuevaImagen) {
        //Por si el admin decide actualizar el nombre
        final String nombreActualizqr = eTNombreVideojuego.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); //declaramos la base de datos
        //y la base de datos dónde se va hacer la actualización
        DatabaseReference databaseReferenceActualizar = firebaseDatabase.getReference("imgVideojuegos_db");

        //Consulta
        //de la base de datos pelicula según el nombre si es equivalente al nombre que deseamos cambiar se hagan los respectivos cambios.
        Query query = databaseReferenceActualizar.orderByChild("id").equalTo(recibirId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //datos a actualizar, hace el recorrido de las imágenes que tenemos subidas
                for (DataSnapshot dataSnapShot : snapshot.getChildren()) {
                    dataSnapShot.getRef().child("nombre").setValue(nombreActualizqr);
                    dataSnapShot.getRef().child("imagen").setValue(nuevaImagen);
                }
                progressDialog.dismiss();
                Toast.makeText(AgregarVidegamesActivity.this, "Actualizado correctamente",
                        Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void subirImagen() {
        String mNombre = eTNombreVideojuego.getText().toString();
        if (mNombre.equals("") || rutaArchivoUri == null) {
            Toast.makeText(this, "Asigne un nombre o una imágen", Toast.LENGTH_SHORT).show();

        } else {
            progressDialog.setTitle("Espere por favor");
            progressDialog.setMessage("Subiendo imágen en videojuegos");
            progressDialog.show();
            progressDialog.setCancelable(false);
            //inicializamos que se cree dentro la ruta que se llama pelicula_subida la cual se va
            //crear en el apartado de storage de firebase.
            //Con el cual dentro de esa carpeta se va crear el siguiente formato de nombre .jpg o .png
            StorageReference mStorageReference = storageReference.child(rutaDeAlmacenamiento
                    + System.currentTimeMillis() + "." + obtenerExtensionDelArchivo(rutaArchivoUri));
            mStorageReference.putFile(rutaArchivoUri)// sirve para enviar la img dentor del apartado del storage
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //obtenemos el Uri de la imágen que acaba de subir el admin que se subio a firebase storage
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;

                            /* con Task obtenemos la imágen para poder subirla a firebase database al momento que el admin
                            presione el botón de publicar, ya que posteriormente las imágenes subidas a la base de datos
                                    serán mostradas tanto al admin como al usuario.*/

                            Uri descargauri = uriTask.getResult();

                            //fecha y hora actual del sistema de nuestro dispositivo
                            String ID = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss", Locale
                                    .getDefault()).format(System.currentTimeMillis());
                            //EJEMPLO: 2023-06-30/05:10:05
                            idVGames.setText(ID);


                            //obtenemos el texto qud tecle el usuario y lo casteamos a String
                            String mVistas = tVVistasVideojuego.getText().toString();
                            String mID = idVGames.getText().toString();
                            int numVistas = Integer.parseInt(mVistas); //casteamos nuestro string a int


                            //cremos nuestro objeto pelicula y le pasamos los parámetros correspondientes
                            Serie serie = new Serie(mNombre + "/" + mID, mNombre, descargauri.toString(), numVistas);
                            String ID_IMAGEN = databaseReference.push().getKey();//esta línea es el hijo de nuestra base de datos general

                            databaseReference.child(ID_IMAGEN).setValue(serie);

                            progressDialog.dismiss();
                            Toast.makeText(AgregarVidegamesActivity.this, "Subido exitosamente",
                                    Toast.LENGTH_SHORT).show();

                            /*startActivity(new Intent(AgregarVidegamesActivity.this,
                                    CategoriaVidegamesAdmin.class));*/
                            finish();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AgregarVidegamesActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressDialog.setTitle("Publicando");
                            progressDialog.setCancelable(false);
                        }
                    });

        }

    }

    //método para obtener la extension de la imágen .jpg o .png
    private String obtenerExtensionDelArchivo(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    private final ActivityResultLauncher<Intent> galeriaLauncher = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //Validámos si se selecciono correctamente la imágen
            try {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    rutaArchivoUri = data.getData();
                    iVAgregarVideojuego.setImageURI(rutaArchivoUri);

                } else {
                    Toast.makeText(AgregarVidegamesActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(AgregarVidegamesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    });

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);

    }
}