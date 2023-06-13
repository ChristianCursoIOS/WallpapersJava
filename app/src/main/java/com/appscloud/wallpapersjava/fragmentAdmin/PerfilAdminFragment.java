package com.appscloud.wallpapersjava.fragmentAdmin;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appscloud.wallpapersjava.MainActivityAdmin;
import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.fragmentAdmin.activity.CambioPasswordActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class PerfilAdminFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference adminDatabase;
    private StorageReference storageReference;
    private TextView tVUidPerfil;
    private TextView tVNombrePerfil;
    private TextView tVApellidosPerfil;
    private TextView tVCorreoPerfil;
    private TextView tVPassPerfil;
    private TextView tVEdadPerfil;
    private ShapeableImageView iVFotoPerfil;
    private Button btnActualizarPass, btnActualizarDatos;
    private final String RUTA_ALMACENAMIENTO_FOTO_PERFIL = "fotos_perfil_administradores/*";
    // private String[] permisosDeCamaraArreglo;//Es el arreglo donde se van almacenar los dos códigos de solicitud
    // private String[] permisosDeAlmacenamientoArreglo;//Es el arreglo donde se van almacenar los dos códigos de solicitud
    private Uri imageUri;//esta variable es para la imágen que nos va devolver tanto si hemos elegido de la cámara como de la galeria
    private String imagenPerfil;//esta variable para poder hacer la actualización en la base de datos en tiempo real.
    private ProgressDialog progressDialog;

    public PerfilAdminFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        tVUidPerfil = view.findViewById(R.id.tV_uid_perfil1);
        tVNombrePerfil = view.findViewById(R.id.tV_nombre_perfil1);
        tVApellidosPerfil = view.findViewById(R.id.tV_apellidos_perfil1);
        tVCorreoPerfil = view.findViewById(R.id.tV_correo_perfil1);
        tVPassPerfil = view.findViewById(R.id.tV_pass_perfil1);
        tVEdadPerfil = view.findViewById(R.id.tV_edad_perfil1);
        iVFotoPerfil = view.findViewById(R.id.iV_foto_perfil);
        btnActualizarPass = view.findViewById(R.id.btn_actualizar_pass);
        btnActualizarDatos = view.findViewById(R.id.btn_actualizar_datos);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = getInstance().getReference();


        //inicializar los permisos
        // permisosDeCamaraArreglo = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // permisosDeAlmacenamientoArreglo = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //inicikalizamos progressDialog
        progressDialog = new ProgressDialog(getContext());


        adminDatabase = FirebaseDatabase.getInstance().getReference("Admin Database");// establecemos el nombre de la DB
        //Hacemos el llamado de la información del admin actual, para ello lo ubicaremos a través de su UID.
        adminDatabase.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Declaramos una condición para verificar si existe el usuario en la base de datos, en caso de que exista hacemos lectura de sus datos
                if (snapshot.exists()) {
                    //Obtener datos de la bd
                    String uid = "" + snapshot.child("UID").getValue();
                    String nombre = "" + snapshot.child("NOMBRES").getValue();
                    String apellidos = "" + snapshot.child("APELLIDOS").getValue();
                    String correo = "" + snapshot.child("EMAIL").getValue();
                    String pass = "" + snapshot.child("PASSWORD").getValue();
                    String edad = "" + snapshot.child("EDAD").getValue();
                    String imagen = "" + snapshot.child("IMAGEN").getValue();

                    tVUidPerfil.setText(uid);
                    tVNombrePerfil.setText(nombre);
                    tVApellidosPerfil.setText(apellidos);
                    tVCorreoPerfil.setText(correo);
                    tVPassPerfil.setText(pass);
                    tVEdadPerfil.setText(edad);


                    try {
                        //Si existe la imagen carga el string que continene le paramero IMAGEN
                        Picasso.get().load(imagen).placeholder(R.drawable.perfil).into(iVFotoPerfil);


                    } catch (Exception e) {
                        //No existe la imágen en la base de datos
                        Picasso.get().load(R.drawable.perfil).into(iVFotoPerfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Nos dirige a CambioPasswordActivity
        btnActualizarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CambioPasswordActivity.class));
                getActivity().finish();
            }
        });

        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarDatosDelAdministrador();
            }
        });

        iVFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagenPerfil = "IMAGEN";
                elegirFoto();
                // actualizarImagenDelPerfilAdministrador();
            }
        });


        return view;
    }

    private void editarDatosDelAdministrador() {
        //Mostrar diálogo que contiene 3 opciones
        String[] opcionesMenu = {"Editar nombre", "Editar apellidos", "Editar edad"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elige una opción");
        builder.setItems(opcionesMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //edit nombres
                    editarNombre();

                } else if (i == 1) {
                    //edit apellidos
                    editarApellidos();

                } else if (i == 2) {
                    //edit edad
                    editarEdad();


                }
            }
        });
        builder.create().show();

    }

    private void editarEdad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizar información: ");
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getActivity());
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Ingrese nuevo dato");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayoutCompat.addView(editText);// añadimos el edittext dentro del linearlayout
        builder.setView(linearLayoutCompat); //seteamos el linearlayout dentor del alertDialog
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nuevoDato = editText.getText().toString().trim();
                if (!nuevoDato.equals("")) {
                    int nuevoDatoInt = Integer.parseInt(nuevoDato);//Casteamos nuestro String nuevoDato a int
                    HashMap<String, Object> resultado = new HashMap<>();
                    resultado.put("EDAD", nuevoDatoInt);// el nuevodato que escribamos atravez del edittext
                    //va reemplazar al dato que contiene el atributo nombres
                    adminDatabase.child(firebaseUser.getUid()).updateChildren(resultado)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Dato actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                } else {
                    Toast.makeText(getActivity(), "Campo vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Cuando el usuario desee cancelar y no actualizar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    private void editarApellidos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizar información: ");
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getActivity());
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Ingrese nuevo dato");
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        linearLayoutCompat.addView(editText);// añadimos el edittext dentro del linearlayout
        builder.setView(linearLayoutCompat); //seteamos el linearlayout dentor del alertDialog
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nuevoDato = editText.getText().toString().trim();
                if (!nuevoDato.equals("")) {
                    HashMap<String, Object> resultado = new HashMap<>();
                    resultado.put("APELLIDOS", nuevoDato);// el nuevodato que escribamos atravez del edittext
                    //va reemplazar al dato que contiene el atributo nombres
                    adminDatabase.child(firebaseUser.getUid()).updateChildren(resultado)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Dato actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                } else {
                    Toast.makeText(getActivity(), "Campo vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Cuando el usuario desee cancelar y no actualizar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();

    }

    private void editarNombre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizar información: ");
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getActivity());
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Ingrese nuevo dato");
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        linearLayoutCompat.addView(editText);// añadimos el edittext dentro del linearlayout
        builder.setView(linearLayoutCompat); //seteamos el linearlayout dentor del alertDialog
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nuevoDato = editText.getText().toString().trim();
                if (!nuevoDato.equals("")) {
                    HashMap<String, Object> resultado = new HashMap<>();
                    resultado.put("NOMBRES", nuevoDato);// el nuevodato que escribamos atravez del edittext
                    //va reemplazar al dato que contiene el atributo nombres
                    adminDatabase.child(firebaseUser.getUid()).updateChildren(resultado)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Dato actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                } else {
                    Toast.makeText(getActivity(), "No puede dejar Campo vacío", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Cuando el usuario desee cancelar y no actualizar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();


    }

    //Método para editar imagen
    private void actualizarImagenDelPerfilAdministrador() {
        String[] opcion = {"Cambiar foto de perfil"};
        //Creamos alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elige una opción");
        builder.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    imagenPerfil = "IMAGEN";
                    elegirFoto();
                }

            }
        });
        builder.create().show();


    }

    //Elegir de donde procede la imágen
    private void elegirFoto() {
        String[] opciones = {"PincheCámara", "Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccionar una imagen de: ");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Seleccionar imágen de camara
                if (i == 0) {
                    //si el permiso fue concedido ejecutamos el método elegirdecamara
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        elegirImagenDeCamara();

                    } else {
                        solicitarPermisosParaCamara.launch(Manifest.permission.CAMERA);
                    }

                } else if (i == 1) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        elegirImagenDeGaleria();

                    } else {
                        solicitarPermisosParaGaleria.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
            }
        });
        builder.create().show();


    }

    private void elegirImagenDeGaleria() {
        Intent imagenGaleriaIntent = new Intent(Intent.ACTION_PICK);
        imagenGaleriaIntent.setType("image/*");
        obtenerImagenDeGaleria.launch(imagenGaleriaIntent);

    }

    //Método para seleccionar una foto de la cámara y nos va devolver atraves del uri la imagen que hayamos seleccionado
    private void elegirImagenDeCamara() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Foto temporal");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripción temporal");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI, contentValues);

        //Para abrir la cámara
        Intent tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // startActivityForResult(camaraIntent, CODIGO_DE_CAMARA_DE_SELECCION_DE_IMAGENES);
        obtenerImagenDeCamara.launch(tomarFotoIntent);
        Log.e("elegir de cámera", "elegirdecamara" + tomarFotoIntent);


    }

    //Variable para manejar el resultado y obtener la uri que ha sido tomada atraves de la cámara del dispositivo
    private final ActivityResultLauncher<Intent> obtenerImagenDeCamara = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        actualizarImagenEnBaseDeDatos(imageUri);
                        progressDialog.setTitle("Procesando...");
                        progressDialog.setMessage("La imagen se esta actualizando");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    } else {
                        Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> obtenerImagenDeGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        actualizarImagenEnBaseDeDatos(imageUri);
                        progressDialog.setTitle("Procesando...");
                        progressDialog.setMessage("La imagen se esta actualizando");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    } else {
                        Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> solicitarPermisosParaCamara = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        elegirImagenDeCamara();

                    } else {
                        Toast.makeText(getActivity(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> solicitarPermisosParaGaleria = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        elegirImagenDeGaleria();

                    } else {
                        Toast.makeText(getActivity(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    private void actualizarImagenEnBaseDeDatos(final Uri uri) {
        String rutaDeArchivoYNombre = RUTA_ALMACENAMIENTO_FOTO_PERFIL + "" + imagenPerfil + "_"
                + firebaseUser.getUid();
        StorageReference storageReference1 = storageReference.child(rutaDeArchivoYNombre);
        storageReference1.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Obtenemos la imagen
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();

//                        try {
                        if (uriTask.isSuccessful()) {
                            //Hashmap para poder enviar datos que van a ser recuperados en otro lado
                            // este caso en el servidor de Firebase
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(imagenPerfil, downloadUri.toString());
                            adminDatabase.child(firebaseUser.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            startActivity(new Intent(getActivity(), MainActivityAdmin.class));
                                            getActivity().finish();

                                            Toast.makeText(getActivity(), "Imagen actualizada con éxito",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        } else {
                            Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }


                        /*} catch (Exception e) {
                            Toast.makeText(getActivity(), "A ocurrido un error, Catch", Toast.LENGTH_SHORT).show();
                            Log.e("try", "Error al cambiar imagen" + imagenPerfil);

                        }*/
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}