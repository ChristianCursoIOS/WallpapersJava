package com.appscloud.wallpapersjava.fragmentAdmin.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.modelo.Administrador;
import com.squareup.picasso.Picasso;

public class DetalleAdminActivity extends AppCompatActivity {

    final String ADMIN_DETAIL_KEY = "administrador";
    ImageView sIVDetalle;
    TextView tVUidDetalle;
    TextView tVNombresDetalle;
    TextView tvAppellidosDetalle;
    TextView tVEmailDetalle;
    TextView tVEdadDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_admin);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //Afirmamos que actionBNar no es nulo
        actionBar.setTitle("Detalle ");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        sIVDetalle = findViewById(R.id.sIV_detalle_admin);
        tVUidDetalle = findViewById(R.id.tV_uid_detalle_admin);
        tVNombresDetalle = findViewById(R.id.tv_nombres_detalle_admin);
        tvAppellidosDetalle = findViewById(R.id.tV_apellidos_detalle_admin);
        tVEmailDetalle = findViewById(R.id.tV_email_detalle_admin);
        tVEdadDetalle = findViewById(R.id.tV_edad_detalle_admin);


        //Recibimos el objeto administrador que viene del intent
        Bundle bundle = getIntent().getExtras();
        Administrador adminDetalle = bundle.getParcelable(ADMIN_DETAIL_KEY);


        //colocamos nuestros datos
        tVUidDetalle.setText("UID: " + adminDetalle.getUID());
        tVNombresDetalle.setText("Nombre: " + adminDetalle.getNOMBRES());
        tvAppellidosDetalle.setText("Apellidos: " + adminDetalle.getAPELLIDOS());
        tVEmailDetalle.setText("Email: " + adminDetalle.getEMAIL());
        String edadS = String.valueOf(adminDetalle.getEDAD()); //casting (convertimos) la edad de tipo int a tipo String
        tVEdadDetalle.setText("Edad: " + edadS);

        try {
            Picasso.get().load(adminDetalle.getIMAGEN()).placeholder(R.drawable.admin_perfil).into(sIVDetalle);

        } catch (Exception e) {
            Picasso.get().load(R.drawable.admin_perfil).into(sIVDetalle);

        }

        //Recibimos el objeto administrador que viene del intent
       /* String imagenPerfil = getIntent().getStringExtra("IMAGEN");
        String UID = getIntent().getStringExtra("UID");
        String nombres = getIntent().getStringExtra("NOMBRES");
        String apellidos = getIntent().getStringExtra("APELLIDOS");
        String email = getIntent().getStringExtra("EMAIL");
        String edad = getIntent().getStringExtra("EDAD");*/
        //colocamos nuestros datos
        /*tVUidDetalle.setText("UID: " + UID);
        tVNombresDetalle.setText("Nombre: " + nombres);
        tvAppellidosDetalle.setText("Apellido: " + apellidos);
        tVEmailDetalle.setText("Email: " + email);
        tVEdadDetalle.setText("Edad: " + edad);*/

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}