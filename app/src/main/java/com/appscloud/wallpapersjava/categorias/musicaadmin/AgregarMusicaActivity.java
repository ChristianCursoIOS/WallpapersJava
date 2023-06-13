package com.appscloud.wallpapersjava.categorias.musicaadmin;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AgregarMusicaActivity extends AppCompatActivity {
    private TextView tVVistasMusica, idMusica;
    private EditText eTNombreMusica;
    private ImageView iVAgregarMusica;
    private Uri rutaArchivoUri = null;
    private final String rutaDeAlmacenamiento = "imgMusica_subida/";
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    String recibirId, recibirIntentNombre, recibirIntentImagen, recibirIntentVistas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_musica);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //Afirmamos que actionBNar no es nulo
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Inicializamos nuestras variables del xml
        idMusica = findViewById(R.id.id_musica);
        tVVistasMusica = findViewById(R.id.tV_vistas_musica);
        eTNombreMusica = findViewById(R.id.eT_escribe_nombre_musica);
        iVAgregarMusica = findViewById(R.id.iV_agregar_musica);
        Button btnPublicarMusica = findViewById(R.id.btn_publicar_musica);

        //Inicializamos nuestras variables de Firebase
        storageReference = getInstance().getReference();
        final String rutaDeBaseDeDatos = "imgMusica_db";
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
            idMusica.setText(recibirId);
            eTNombreMusica.setText(recibirIntentNombre);
            tVVistasMusica.setText(recibirIntentVistas);
            Picasso.get().load(recibirIntentImagen).into(iVAgregarMusica);

            //Cambiar nombre en actionbar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";

            //Cambiar nombre del borón
            btnPublicarMusica.setText(actualizar);

        }


        iVAgregarMusica.setOnClickListener(new View.OnClickListener() {
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

        btnPublicarMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnPublicarMusica.getText().equals("Publicar")) {
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
                Toast.makeText(AgregarMusicaActivity.this, "La imágen anterior a sido eliminada",
                        Toast.LENGTH_SHORT).show();
                subirNuevaImagen();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarMusicaActivity.this, e.getMessage(),
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
        Bitmap bitmap = ((BitmapDrawable) iVAgregarMusica.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = storageReferenceSubNueImg.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarMusicaActivity.this, "Nueva imágen cargada",
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
                Toast.makeText(AgregarMusicaActivity.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void actualizarImagenEnLaBaseDeDatos(String nuevaImagen) {
        //Por si el admin decide actualizar el nombre
        final String nombreActualizqr = eTNombreMusica.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); //declaramos la base de datos
        //y la base de datos dónde se va hacer la actualización
        DatabaseReference databaseReferenceActualizar = firebaseDatabase.getReference("imgMusica_db");

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
                Toast.makeText(AgregarMusicaActivity.this, "Actualizado correctamente",
                        Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void subirImagen() {
        String mNombre = eTNombreMusica.getText().toString();
        if (mNombre.equals("") || rutaArchivoUri == null) {
            Toast.makeText(this, "Asigne un nombre o una imágen", Toast.LENGTH_SHORT).show();

        } else {
            progressDialog.setTitle("Espere por favor");
            progressDialog.setMessage("Subiendo imágen en música");
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
                            idMusica.setText(ID);

                            //obtenemos el texto qud tecle el usuario y lo casteamos a String
                            String mVistas = tVVistasMusica.getText().toString();
                            String mID = idMusica.getText().toString();
                            int numVistas = Integer.parseInt(mVistas); //casteamos nuestro string a int


                            //cremos nuestro objeto pelicula y le pasamos los parámetros correspondientes
                            Musica musica = new Musica(mNombre + "/" + mID, mNombre,
                                    descargauri.toString(), numVistas);
                            String ID_IMAGEN = databaseReference.push().getKey();//esta línea es el hijo de nuestra base de datos general

                            databaseReference.child(ID_IMAGEN).setValue(musica);

                            progressDialog.dismiss();
                            Toast.makeText(AgregarMusicaActivity.this, "Subido exitosamente",
                                    Toast.LENGTH_SHORT).show();

                            /*startActivity(new Intent(AgregarMusicaActivity.this,
                                    CategoriaMusicAdmin.class));*/
                            finish();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AgregarMusicaActivity.this, e.getMessage(),
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
                    iVAgregarMusica.setImageURI(rutaArchivoUri);

                } else {
                    Toast.makeText(AgregarMusicaActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(AgregarMusicaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    });

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);

    }

}
