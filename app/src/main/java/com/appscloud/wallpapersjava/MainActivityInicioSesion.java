package com.appscloud.wallpapersjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityInicioSesion extends AppCompatActivity {

    EditText eTemailLogin, eTpassLogin;
    Button btnLogin;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_inicio_sesion);

        eTemailLogin = findViewById(R.id.eT_mail_login);
        eTpassLogin = findViewById(R.id.eT_pass_login);
        btnLogin = findViewById(R.id.btn_login);

        firebaseAuth = FirebaseAuth.getInstance();

        //creamos botón de retroceso con estas lineas
        ActionBar actionBar = getSupportActionBar(); //Creamos nuestro actionbar
        assert actionBar != null; //Afirmammos que el actionbar no es nulo
        actionBar.setTitle("Inicio de sesión"); //Asignamos un titulo
        actionBar.setDisplayHomeAsUpEnabled(true); //Habilitamos el botón de retroceso
        actionBar.setDisplayShowHomeEnabled(true);
        /////////////////////

        progressDialog = new ProgressDialog(MainActivityInicioSesion.this);
        progressDialog.setMessage("Ingresando espere por favor");
        progressDialog.setCancelable(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emal = eTemailLogin.getText().toString();
                String pass = eTpassLogin.getText().toString();

                //Validación de correo
                if (!Patterns.EMAIL_ADDRESS.matcher(emal).matches()) {
                    eTemailLogin.setError("Correo o contraseña incorrectos");
                    eTemailLogin.setFocusable(true);

                } else if (pass.length() < 8) {
                    eTpassLogin.setError("Correo o contraseña incorrectos");
                    eTpassLogin.setFocusable(true);

                } else {
                    iniciarSesionAdministradores(emal, pass);
                }
            }
        });


    }

    private void iniciarSesionAdministradores(String emal, String pass) {
        progressDialog.show();
        progressDialog.setCancelable(false);
        firebaseAuth.signInWithEmailAndPassword(emal, pass)
                .addOnCompleteListener(MainActivityInicioSesion.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Si el inicio de sesión fue exitoso
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(MainActivityInicioSesion.this,
                                    MainActivityAdmin.class));
                            assert firebaseUser != null; //afrimamos que no es nuelo
                            Toast.makeText(MainActivityInicioSesion.this, "Bienvenida(o) "
                                    + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            progressDialog.dismiss();
                            usuarioIncorrecto();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        usuarioIncorrecto();


                    }
                });


    }

    private void usuarioIncorrecto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("¡HA OCURRIDO UN ERROR!");
        builder.setMessage("Verifique su correo o contraseña")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();


                    }
                }).show();

    }

    //Método para que cuando el usuario presione el botón de retrocesos este lo mande a la Activity anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}