package com.appscloud.wallpapersjava.fragmentAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.appscloud.wallpapersjava.MainActivityAdmin;
import com.appscloud.wallpapersjava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RegistroAdminFragment extends Fragment {

    //Declaramos nuestras variables globales
    TextView tVFechaDeRegistro;
    EditText eTmail, eTcontrasena, eTnombres, eTApellidos, eTEdad;
    Button btnRegistrar;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro_admin, container, false);
        firebaseAuth = FirebaseAuth.getInstance(); //inicializando fribase

        tVFechaDeRegistro = view.findViewById(R.id.fecha_de_registro);
        eTnombres = view.findViewById(R.id.eT_nombres);
        eTApellidos = view.findViewById(R.id.eT_apellido);
        eTmail = view.findViewById(R.id.eT_mail);
        eTcontrasena = view.findViewById(R.id.eT_contraseña);
        eTEdad = view.findViewById(R.id.eT_edad);
        btnRegistrar = view.findViewById(R.id.btn_registro_admins);

        //En estas lineas creamos la fecha para poder convertirla a un strimg y poder visualizarla en nuestra pantalla
        //de registro
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy");
        String fecha = simpleDateFormat.format(date); //convertimos la fecha a string
        tVFechaDeRegistro.setText(fecha);

        //Método que se va ejecutar al hacer clic en el botón
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Convertimos a string correo y contraseña para poder hacer las validaciopones
                // atravez de condiciónes
                String correo = eTmail.getText().toString();
                String contrasena = eTcontrasena.getText().toString();
                String nombres = eTnombres.getText().toString();
                String apellidos = eTApellidos.getText().toString();
                String edad = eTEdad.getText().toString();

                //Validación para que todos los campos sean obligatorios
                if (correo.equals("") || contrasena.equals("") || nombres.equals("") || apellidos.
                        equals("") || edad.equals("")) {
                    Toast.makeText(getActivity(), "Por favor ingrresa los campos faltantes",
                            Toast.LENGTH_SHORT).show();

                } else {
                    //Validacion de correo
                    if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                        eTmail.setError("Email inválido");
                        eTmail.setFocusable(true);

                    } else if (contrasena.length() < 8) {
                        eTcontrasena.setError("La contraseña debe ser mayor o igual a 8 caracteres");
                        eTcontrasena.setFocusable(true);

                    } else {
                        registrarAdministrador(correo, contrasena);
                    }
                }


            }
        });
        //Esta lineas sirven para poder tener un mensaje de que se esta procesando algún método
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Registrando, espere porfavor");
        progressDialog.setCancelable(false); //Esta línea sirve para que si damos click afura de la alerta
        //de que se esta registrando no se cierre y se mantenga abierta.

        return view;
    }

    //Método para registrar administradores
    private void registrarAdministrador(String correo, String contrasena) {
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Si el admin fue creado correctamente
                        if (task.isSuccessful()) {
                            progressDialog.dismiss(); //Si fue correcto que se cierre con dismiss
                            //esta linea quiere decir que va tomar en cuenta al usuario actual en el cual
                            //esta siendo uso la app.
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null; //afirmamos que el usuario admin no es nulo

                            //Convertir a string los datos de los adminstradores
                            String uId = firebaseUser.getUid();

                            String nombres = eTnombres.getText().toString();
                            String apellidos = eTApellidos.getText().toString();
                            String email = eTmail.getText().toString();
                            String password = eTcontrasena.getText().toString();
                            String edad = eTEdad.getText().toString();
                            int edadInt = Integer.parseInt(edad);

                            //Con esta linea enviamos los datos desde nuestra app hacia el servidor de Firebase
                            HashMap<Object, Object> administradores = new HashMap<>();

                            administradores.put("UID", uId);
                            administradores.put("NOMBRES", nombres);
                            administradores.put("APELLIDOS", apellidos);
                            administradores.put("EMAIL", email);
                            administradores.put("PASSWORD", password);
                            administradores.put("EDAD", edadInt);
                            administradores.put("IMAGEN", "");

                            //Inicializar y cremos nuestra firebaseDataBase
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase
                                    .getReference("Admin Database");
                            //la database de admin va contener varios UID y cada UID que le pertenese
                            //a un admin va contener sus propios datos nombre, edad etc..
                            databaseReference.child(uId).setValue(administradores);

                            //despues del registro nos va enviar al MainACtivityAdmin
                            startActivity(new Intent(getActivity(), MainActivityAdmin.class));
                            Toast.makeText(getActivity(), "Registro exitoso",
                                    Toast.LENGTH_SHORT).show();
                            getActivity().finish();


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                //gestión de posbiles errores
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}