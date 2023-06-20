package com.appscloud.wallpapersjava.detalleCliente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appscloud.wallpapersjava.R;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class DetalleImgActivity extends AppCompatActivity {

    /*   private final int CODIGO_ALMACENAMIENTO = 1;
       private final int CODIGO_ALMACENAMIENTO_API_30 = 2;*/
    private TextView tVNombreDetalleImg;
    TextView tVVistasDetalle;
    private ImageView iVimagen;
    FloatingActionButton fabDescargar, fabCompartir, fabEstablecer;
    private Bitmap bitmap; //mapa de bits, es la estructura donde se almacenan los pixeles que conforman un gráfico
    Uri imageUri = null;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_img);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tVNombreDetalleImg = findViewById(R.id.tv_nombre_detalle_img_cliente);
        tVVistasDetalle = findViewById(R.id.tv_vista_detalle_cliente);
        iVimagen = findViewById(R.id.aCIV_detalle_cliente);

        fabCompartir = findViewById(R.id.fab_compartir);
        fabDescargar = findViewById(R.id.fab_descargar);
        fabEstablecer = findViewById(R.id.fab_establecer);

        dialog = new Dialog(this);


        //Obtenemos nuestro intent del detalle de la imagen
        String nombre = getIntent().getStringExtra("nombre");
        String numeroDeVistas = getIntent().getStringExtra("vistas");
        String imagen = getIntent().getStringExtra("imagen");

        //colocamos los datos en sus TextView correspondientes
        tVNombreDetalleImg.setText(nombre);
        tVVistasDetalle.setText(numeroDeVistas);
        try {
            Picasso.get().load(imagen).placeholder(R.drawable.icon_categoria)
                    .into(iVimagen);

        } catch (Exception e) {
            Picasso.get().load(R.drawable.icon_categoria).into(iVimagen);

        }
        //Establecemos accion al nuestro botónes
        fabDescargar.setOnClickListener(descargar -> {
            //Si la version es mayor o igual a Android 11
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //si el permiso es concedido descargamos imagen
                if (ContextCompat.checkSelfPermission(DetalleImgActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    descargarImagenAndroid11oSuperior();

                } else {
                    solicitarPermisoDescargarAndroid11oSuperior.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                //Si la verisón de Android es igual o mayor a Android 6
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //si el permiso fue concedido descargamos la imagen
                if (ContextCompat.checkSelfPermission(DetalleImgActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    descargarImagenAndroid6oMenor();

                } else {
                    solicitarPermisoDescargar.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }


            } else { //si la versión de Android es menor a 6
                descargarImagenAndroid6oMenor();


            }
        });

        fabCompartir.setOnClickListener(compartir ->
                //compartirImagen();
                compartirImagenAndroid11oSuperior()
        );

        fabEstablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                establecerImagenFondoDePantalla();
            }
        });

        //En caso  no descargue la imagen
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


    }

    //Solicitud para descargar imagen con Andoid menor a Android 6
    private final ActivityResultLauncher<String> solicitarPermisoDescargar =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                //Si el permiso fue acceptado
                if (isGranted) {
                    descargarImagenAndroid6oMenor();

                } else { //si el permiso fue denegado
                    // Toast.makeText(this, "Por favor acepte el permiso para poder " + "descargar la imagen", Toast.LENGTH_SHORT).show();
                    activarPermisosAnimacion();

                }

            });
    //Solicitud para descargar imagen con Andoid superior a Android 11
    private final ActivityResultLauncher<String> solicitarPermisoDescargarAndroid11oSuperior =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted11 -> {
                //Si el permiso fue acceptado
                if (isGranted11) {
                    descargarImagenAndroid11oSuperior();

                } else { //si el permiso fue denegado
                    // Toast.makeText(this, "Por favor acepte el permiso para poder " + "descargar la imagen", Toast.LENGTH_SHORT).show();
                    activarPermisosAnimacion();

                }

            });

    private void descargarImagenAndroid11oSuperior() {
        bitmap = ((BitmapDrawable) iVimagen.getDrawable()).getBitmap();
        //declaramos salida
        OutputStream outputStream;
        //obtenemos el nombre de la imagen de la galeria
        String nombreImagen = tVNombreDetalleImg.getText().toString();

        try {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, nombreImagen);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "Image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + "/wallpaperAppAI/");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            // Toast.makeText(this, "La imagen se descargo correctamente", Toast.LENGTH_SHORT).show();
            descargarImgExitosaAnimacion();

        } catch (Exception e) {
            Toast.makeText(this, "No se pudo descargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void descargarImagenAndroid6oMenor() {
        //obtenemos el mapa de bits de la imágen
        bitmap = ((BitmapDrawable) iVimagen.getDrawable()).getBitmap();
        //obtenemos fecha de descarga con este formato y ahora el nombre de una imagen será única, ya que la fecha y hora
        // actual del sistema varia cada segundo y es algo irrepetible. Entoncesm las imágenes n
        // no se irán reemplazando una tras otra.
        String fechaDeDescarga = new SimpleDateFormat("'Fecha descarga: ' yyyy_MM_dd 'Hora: ' HH:mm:ss",
                Locale.getDefault()).format(System.currentTimeMillis());
        //Definir ruta de almacenamiento
        File fileRuta = Environment.getExternalStorageDirectory();
        //Definir el nombre de la carpeta
        File nombreCapeta = new File(fileRuta + "/wallpaperAppAI/");
        nombreCapeta.mkdir();
        //Definir nombre de la imagen descargada
        String obtenerNombreImagen = tVNombreDetalleImg.getText().toString();
        String nombreImagen = obtenerNombreImagen + " " + fechaDeDescarga + ".JPEG";
        File file = new File(nombreCapeta, nombreImagen);
        //declaramos salida
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            //Toast.makeText(this, "La imagen se descargo correctamente", Toast.LENGTH_SHORT).show();
            descargarImgExitosaAnimacion();


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void compartirImagen() {
        try {
            //obtenemos el bitmap
            bitmap = ((BitmapDrawable) iVimagen.getDrawable()).getBitmap();
            //Obtenemos el nombre de la imagen
            String nombreImagen = tVNombreDetalleImg.getText().toString();
            File file = new File(getExternalCacheDir(), nombreImagen + ".JPEG");
            //Flujo de salida
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            file.setReadable(true);
            Intent intent = new Intent(Intent.ACTION_SEND);//indicamos que vamos a enviar una imagen
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpeg");
            startActivity(Intent.createChooser(intent, "Compartir"));


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();


        }
    }

    private void compartirImagenAndroid11oSuperior() {
        Uri contentUri = getContentUri();
        Intent sharedIntent = new Intent(Intent.ACTION_SEND);
        sharedIntent.setType("image/jpeg");
        sharedIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
        sharedIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        sharedIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(sharedIntent);


    }

    private Uri getContentUri() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) iVimagen.getDrawable();
        bitmap = bitmapDrawable.getBitmap();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);

            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }

        } catch (Exception e) {

        }
        File imageFolder = new File(getCacheDir(), "images");
        Uri contentUri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            contentUri = FileProvider.getUriForFile(this, "com.appscloud.wallpapersjava.fileprovider",
                    file);


        } catch (Exception e) {

        }
        return contentUri;
    }

    private void establecerImagenFondoDePantalla() {
        //obtenemos el bitmap
        bitmap = ((BitmapDrawable) iVimagen.getDrawable()).getBitmap();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(bitmap);
           // Toast.makeText(this, "Establecido con éxito", Toast.LENGTH_SHORT).show();
            establecerImgFondoPantalla();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void activarPermisosAnimacion() {
        Button btnPermisosOk;

        dialog.setContentView(R.layout.animacion_permiso);
        //Conexión con el cuadro de dialigo
        btnPermisosOk = dialog.findViewById(R.id.btn_permisos);

        btnPermisosOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(false); // sirve para que el dialogo no lo podamos cerrar si tocamos
        //alrededor del dialogo solo cuando presionemos el boton ok
    }

    private void descargarImgExitosaAnimacion() {
        Button btnDescargaExitosa;

        dialog.setContentView(R.layout.animacion_descarga_exitosa);
        //Conexión con el cuadro de dialigo
        btnDescargaExitosa = dialog.findViewById(R.id.btn_descarga_exitosa);

        btnDescargaExitosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(false); // sirve para que el dialogo no lo podamos cerrar si tocamos
        //alrededor del dialogo solo cuando presionemos el boton ok
    }

    private void establecerImgFondoPantalla() {
        Button btnEstablecer;

        dialog.setContentView(R.layout.animacion_establecida);
        //Conexión con el cuadro de dialigo
        btnEstablecer = dialog.findViewById(R.id.btn_establecido_fondo);

        btnEstablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(false); // sirve para que el dialogo no lo podamos cerrar si tocamos
        //alrededor del dialogo solo cuando presionemos el boton ok
    }






   /* Metodo para pedir permisos para el almacenamiento no actualizado
   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODIGO_ALMACENAMIENTO) {
            //Si el permiso fue concedido
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                descargarImagen();
            } else {//si el permino no fue concedido

                Toast.makeText(this, "Por favor acepte los permisos para poder descargar" +
                        "la imager", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CODIGO_ALMACENAMIENTO_API_30) {
            //Si el permiso fue concedido
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                descargarImagenAndroid11();
            } else {//si el permino no fue concedido

                Toast.makeText(this, "Por favor acepte los permisos para poder descargar" +
                        "la imagen", Toast.LENGTH_SHORT).show();
            }


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    //Método para regresar a la activity anterior al momento que presionemos la flecha hacia atras
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}