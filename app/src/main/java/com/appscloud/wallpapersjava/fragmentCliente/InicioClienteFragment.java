package com.appscloud.wallpapersjava.fragmentCliente;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appscloud.wallpapersjava.R;
import com.appscloud.wallpapersjava.apartadoInformativo.ApartadoInformativo;
import com.appscloud.wallpapersjava.apartadoInformativo.ViewHolderApartadoInformativo;
import com.appscloud.wallpapersjava.categoriaClienteFirebase.ListaClienteFirebaseActivity;
import com.appscloud.wallpapersjava.categorias.ControladorDispositivoActivity;
import com.appscloud.wallpapersjava.categorias.categoriafirebase.CategoriaFirebase;
import com.appscloud.wallpapersjava.categorias.categoriafirebase.FirebaseViewHolder;
import com.appscloud.wallpapersjava.categorias.dispositivo.CategoriaDispositivo;
import com.appscloud.wallpapersjava.categorias.dispositivo.DispositivoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


public class InicioClienteFragment extends Fragment {

    RecyclerView recyclerViewCategoriaDispositivo, recyclerViewCategoriaFirebase, recyclerViewInformacion;
    FirebaseDatabase firebaseDatabase, firebaseDatabaseCategorias, firebaseDatabaseInformacion;
    private DatabaseReference databaseReference, databaseReferenceFirebase, databaseReferenceInformacion;
    LinearLayoutManager linearLayoutManager, linearLayoutManagerFirebase, linearLayoutManagerInformacion;

    private FirebaseRecyclerAdapter<CategoriaDispositivo, DispositivoViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerAdapter<CategoriaFirebase, FirebaseViewHolder> firebaseRecyclerAdapterCategorias;
    private FirebaseRecyclerAdapter<ApartadoInformativo, ViewHolderApartadoInformativo> firebaseRecyclerAdapterInformacion;

    private FirebaseRecyclerOptions<CategoriaDispositivo> firebaseRecyclerOptions;
    private FirebaseRecyclerOptions<CategoriaFirebase> firebaseRecyclerOptionsCategorias;
    private FirebaseRecyclerOptions<ApartadoInformativo> firebaseRecyclerOptionsInformacion;

    TextView fecha;


    LinearLayoutCompat conConexion, sinConexion;

    AdView mAdView;


    ConnectivityManager connectivityManager;
    ConnectivityManager.NetworkCallback networkCallback;
    ;

    public InicioClienteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio_cliente, container, false);

        //inicializamos nuestors lineraLayoutCompat

        conConexion = view.findViewById(R.id.con_conexion);
        sinConexion = view.findViewById(R.id.sin_conexion);

        //  connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);


        //inicializamos las bases de datos
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabaseCategorias = FirebaseDatabase.getInstance();
        firebaseDatabaseInformacion = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("categorias_dispositivo_db");
        databaseReferenceFirebase = firebaseDatabaseCategorias.getReference("categorias_firebase_db");
        databaseReferenceInformacion = firebaseDatabaseInformacion.getReference("informacion_db");

        //RecyclerView de categorias subidas desde dispositivo por un admin
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoriaDispositivo = view.findViewById(R.id.recyclerView_categoria_dispositivo); //inicializamos el recyclerView categoria dispositivo
        recyclerViewCategoriaDispositivo.setHasFixedSize(true);
        recyclerViewCategoriaDispositivo.setLayoutManager(linearLayoutManager);

        //RecyclerView de categorias subidas desde consola firebase
        linearLayoutManagerFirebase = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoriaFirebase = view.findViewById(R.id.recyclerView_categoria_firebase);//inicializamos el recyclerView de categoria firebase
        recyclerViewCategoriaFirebase.setHasFixedSize(true);
        recyclerViewCategoriaFirebase.setLayoutManager(linearLayoutManagerFirebase);

        //RecyclerView del apartado informacion
        linearLayoutManagerInformacion = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewInformacion = view.findViewById(R.id.recyclerView_informacion_app);//inicializamos el recyclerView
        recyclerViewInformacion.setHasFixedSize(true);
        recyclerViewInformacion.setLayoutManager(linearLayoutManagerInformacion);

        //inicializamos nuestra variable fecha
        fecha = view.findViewById(R.id.tV_fecha);

        //Configuramos la fecha actual
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy");
        String formatoFecha = simpleDateFormat.format(date);
        fecha.setText(formatoFecha);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //invocamos nuestro método para que al inicializar el fragmento y se pueda visualizar nuestras categorias
        verCategoriasDispositivo();
        verCategoriasDeFirebase();
        verApartadoInformativo();

       /* final String DEBUG_TAG = "NetworkStatusExample";

        boolean isWifiConn = false;
        boolean isMobileConn = false;
        for (Network network : connectivityManager.getAllNetworks()) {
            networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
            }
        }
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);

        if (isWifiConn || isMobileConn) {
            conConexion.setVisibility(View.VISIBLE);
        } else {
            sinConexion.setVisibility(View.VISIBLE);
        }*/

        // Temporalmente comentado para gestionar conexcion a internet
       /* if (conectarAInternet()) {
            conConexion.setVisibility(View.VISIBLE);
        } else {
            sinConexion.setVisibility(View.VISIBLE);
        }*/
        return view;

    }

    // Gestiona si hay o no internet
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        connectivityManager = (ConnectivityManager) requireActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // con conexión a internet
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conConexion.setVisibility(View.VISIBLE);
                        sinConexion.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                // si se perdio la conexión ainternet
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sinConexion.setVisibility(View.VISIBLE);
                        conConexion.setVisibility(View.GONE);
                    }
                });
            }
        };


        NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    /*private boolean conectarAInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }*/


    //Este método muestra las categorias que se suben desde el dispositivo son las imagenes que
    // sube el admin hace posible que nosotros como clientes visualicemos las 4 categorias
    // peliculas, series, musica, videojuegos
    private void verCategoriasDispositivo() {
        //en el firebaseOptions le pasamos el objetoCategoriaDispositivo y la referencia de la base de datos
        //es decir le indicamos que la base de dstos va a contar con objetos que tienen caracgeristicas como el nolmbre e imagen
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<CategoriaDispositivo>()
                .setQuery(databaseReference, CategoriaDispositivo.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CategoriaDispositivo,
                DispositivoViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull DispositivoViewHolder holder, int position,
                                            @NonNull CategoriaDispositivo model) {
                holder.establecerCategoriaDispositivo(getActivity(), model.getCategoria(),
                        model.getImagen());
            }

            @NonNull
            @Override
            public DispositivoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflamos el layout
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorias_dispositivo,
                        parent, false);
                DispositivoViewHolder dispositivoViewHolder = new DispositivoViewHolder(view);
                dispositivoViewHolder.setOnClickListener(new DispositivoViewHolder.CLickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //obtenemos el nombre de la categoría.
                        String categoria = getItem(position).getCategoria();
                        Intent intent = new Intent(view.getContext(), ControladorDispositivoActivity.class);
                        intent.putExtra("categoria", categoria);
                        startActivity(intent);
                        //Toast.makeText(getActivity(), categoria, Toast.LENGTH_SHORT).show();
                    }
                });


                return dispositivoViewHolder;
            }
        };
        recyclerViewCategoriaDispositivo.setAdapter(firebaseRecyclerAdapter);//para que se visualice cada item en el recyclerView categorias del dispositivo

    }

    //Este método muestra las imágenes que el admin sube desde la consola de firebase son las imagenes que
    // hace posible que nosotros como clientes visualicemos las 4 categorias
    // caricaturas, animales, deportes, naturaleza
    private void verCategoriasDeFirebase() {
        //en el firebaseOptions le pasamos el objetoCategoriaDispositivo y la referencia de la base de datos
        //es decir le indicamos que la base de dstos va a contar con objetos que tienen caracgeristicas como el nolmbre e imagen
        firebaseRecyclerOptionsCategorias = new FirebaseRecyclerOptions.Builder<CategoriaFirebase>().setQuery(databaseReferenceFirebase, CategoriaFirebase.class).build();
        firebaseRecyclerAdapterCategorias = new FirebaseRecyclerAdapter<CategoriaFirebase, FirebaseViewHolder>(firebaseRecyclerOptionsCategorias) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder holder, int position, @NonNull CategoriaFirebase model) {
                holder.establecerCategoriaFirebase(getActivity(), model.getCategoria(),
                        model.getImagen());
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflamos el layout
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorias_firebase,
                        parent, false);
                FirebaseViewHolder firebaseViewHolder = new FirebaseViewHolder(view);

                firebaseViewHolder.setOnClickListener(new FirebaseViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //obtenemos el nombre de la categoria
                        String nombreCategoria = getItem(position).getCategoria();

                        //pasamos el nombre de la categoria a la siguiente activity
                        //nombreCategoria continene el nombre de la categoria elegida por el cliente
                        // ,la cual es enviada para ser recuperada en la clase, ListaCategoriaFirebase
                        // para que pposteriormente xe haga la lectura.,
                        Intent intentNombreCategoria = new Intent(view.getContext(), ListaClienteFirebaseActivity.class);
                        // Toast.makeText(view.getContext(), "Categoria seleccionada = " + nombreCategoria, Toast.LENGTH_SHORT).show();
                        intentNombreCategoria.putExtra("nombreCategoria", nombreCategoria);
                        startActivity(intentNombreCategoria);

                    }
                });

                return firebaseViewHolder;
            }
        };
        //para que se visualice cada item en el recyclerView categorias del dispositivo
        recyclerViewCategoriaFirebase.setAdapter(firebaseRecyclerAdapterCategorias);
    }

    private void verApartadoInformativo() {
        //en el firebaseOptions le pasamos el objetoCategoriaDispositivo y la referencia de la base de datos
        //es decir le indicamos que la base de dstos va a contar con objetos que tienen caracgeristicas como el nolmbre e imagen
        firebaseRecyclerOptionsInformacion = new FirebaseRecyclerOptions.Builder<ApartadoInformativo>()
                .setQuery(databaseReferenceInformacion, ApartadoInformativo.class).build();
        firebaseRecyclerAdapterInformacion = new FirebaseRecyclerAdapter<ApartadoInformativo,
                ViewHolderApartadoInformativo>(firebaseRecyclerOptionsInformacion) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderApartadoInformativo holder, int position,
                                            @NonNull ApartadoInformativo model) {
                holder.establecerApartadoInformativo(getActivity(), model.getNombre(),
                        model.getImagen());
            }

            @NonNull
            @Override
            public ViewHolderApartadoInformativo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflamos el layout del item donde se van a listar las imaáenes informativas
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apartado_informativo,
                        parent, false);

                ViewHolderApartadoInformativo viewHolderApartadoInformativo = new ViewHolderApartadoInformativo(view);
                viewHolderApartadoInformativo.setOnClickListener(new ViewHolderApartadoInformativo.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //las imágenes listadas no tendran nikngún evento

                    }
                });

                return viewHolderApartadoInformativo;
            }
        };
        //para que se visualice cada item en el recyclerView categorias del dispositivo
        recyclerViewInformacion.setAdapter(firebaseRecyclerAdapterInformacion);
    }


    @Override
    public void onStart() {
        super.onStart();
        //si el adaptador no es nuelo entonces que el adaptador este escuchando
        // se refiere a que el adaptador esta al pendiente  si surge algun cambio en el recyclreView
        if (firebaseRecyclerAdapter != null && firebaseRecyclerAdapterCategorias != null
                && firebaseRecyclerAdapterInformacion != null) {
            firebaseRecyclerAdapter.startListening();
            firebaseRecyclerAdapterCategorias.startListening();
            firebaseRecyclerAdapterInformacion.startListening();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}