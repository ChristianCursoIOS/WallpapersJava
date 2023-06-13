package com.appscloud.wallpapersjava.fragmentAdmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appscloud.wallpapersjava.MainActivityAdmin;
import com.appscloud.wallpapersjava.MainActivityInicioSesion;
import com.appscloud.wallpapersjava.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CambioPasswordActivity extends AppCompatActivity {


    private TextView tVPassActual;
    private EditText eTPassActual, eTPassNuevo;
    private Button btnCambioPass, btnIrInicio;
    private DatabaseReference adminDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_password);

        tVPassActual = findViewById(R.id.tV_password_actual);
        eTPassActual = findViewById(R.id.eT_password_actual);
        eTPassNuevo = findViewById(R.id.eT_password_nuevo);
        btnCambioPass = findViewById(R.id.btn_cambiar_password);
        btnIrInicio = findViewById(R.id.btn_ir_a_inicio);

        adminDatabase = FirebaseDatabase.getInstance().getReference("Admin Database");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);

        //Consulta  en la database para traer a la app la contraeña
        Query query = adminDatabase.orderByChild("EMAIL").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //For que recorre todos los usuarios registrados
                for (DataSnapshot dS : snapshot.getChildren()) {
                    //Obtenemo el password actual desde firebase
                    String password = "" + dS.child("PASSWORD").getValue();
                    tVPassActual.setText(password);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnCambioPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passActual = eTPassActual.getText().toString();
                String passNuevo = eTPassNuevo.getText().toString();

                if (TextUtils.isEmpty(passActual)) {
                    Toast.makeText(CambioPasswordActivity.this,
                            "El campo de contraseña actual esta vacío", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(passNuevo)) {
                    Toast.makeText(CambioPasswordActivity.this,
                            "El campo de contraseña nueva esta vacío", Toast.LENGTH_SHORT).show();

                    //con esta condición decimos si la contraseña actual y si la nueva contraseña y
                    // si la contraseña nueva es mayor o igual a 8 caracteres se debera ejecutar el
                    //método cambiarPassword(passActual, passNuevo);
                }
                if (!passActual.equals("") && !passNuevo.equals("") && passNuevo.length() >= 8) {
                    cambiarPassword(passActual, passNuevo);

                } else {
                    eTPassNuevo.setError("La contraseña debe ser mayor o igual a 8 carateres");
                    eTPassNuevo.setFocusable(true);
                }
            }
        });

        btnIrInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CambioPasswordActivity.this, MainActivityAdmin.class));
            }
        });


    }

    private void cambiarPassword(String passActual, String passNuevo) {
        progressDialog.show();
        progressDialog.setTitle("Actualiando...");
        progressDialog.setMessage("Espere por favor");

        //Para hacer posible el cambio de contraseña en la base de datos, es necesario utilizar
        //la propiedad de FireBaseAuthentication la cual se llama auto credencial y también usaremos
        // la reautenticación le pasamos el mail y pass del usuairo actual.
        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),
                passActual);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseUser.updatePassword(passNuevo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        String nuevoPass = eTPassNuevo.getText().toString().trim();
                                        //el hashmap lo utilizamos para evniar datos que van a ser
                                        //recuperados en otro lado.
                                        //En este caso vamos a enviar el dato nuevopass desde la aplicación el cual
                                        //va ser recuperado en el sevidor de Firebase.
                                        HashMap<String, Object> reultadoHashMap = new HashMap<>();
                                        //Establecemos el parametro que queremos actualizar en este caso password
                                        reultadoHashMap.put("PASSWORD", nuevoPass);
                                        //Actualizar contraseña en la base de datos y le pasamos el reultadoHashMap
                                        //qeu es el que contiene el dato password
                                        adminDatabase.child(firebaseUser.getUid()).updateChildren(reultadoHashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(CambioPasswordActivity.this,
                                                                "La contraseña se actualizó correctamente",
                                                                Toast.LENGTH_SHORT).show();
                                                        //Cerramos sesión
                                                        firebaseAuth.signOut();
                                                        startActivity(new Intent(CambioPasswordActivity.this,
                                                                MainActivityInicioSesion.class));
                                                        finish();
                                                    }
                                                    //Si ocurre un error mostrara un mensaje
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(CambioPasswordActivity.this,
                                                                e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                });

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CambioPasswordActivity.this,
                                                e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CambioPasswordActivity.this,
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onBackPressed() {

    }
}