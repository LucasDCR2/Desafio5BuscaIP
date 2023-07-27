package com.example.desafioteste5;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // Inicialização do mapa
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        EditText editTextIp = findViewById(R.id.editTextIp);
        ImageView imageViewBuscar = findViewById(R.id.imageViewBuscar);

        // Set click listener for the ImageView
        imageViewBuscar.setOnClickListener(v -> {
            String ipAddress = editTextIp.getText().toString().trim();
            if (!ipAddress.isEmpty()) {
                buscarIp(ipAddress);
            }
        });
    }

    private void buscarIp(String ipAddress) {
        // Configuração do Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ip-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IpApiService apiService = retrofit.create(IpApiService.class);

        // Chamada API
        Call<IpApiResult> call = apiService.getIpInfo(ipAddress);
        call.enqueue(new Callback<IpApiResult>() {
            @Override
            public void onResponse(Call<IpApiResult> call, Response<IpApiResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    IpApiResult ipApiResult = response.body();
                    exibirInformacoesNoMapa(ipApiResult);
                    exibirInformacoesDoIp(ipApiResult);
                } else {
                    Log.e("IpLocatorActivity", "Erro na resposta da API");
                }
            }

            @Override
            public void onFailure(Call<IpApiResult> call, Throwable t) {
                Log.e("IpLocatorActivity", "Erro na requisição à API", t);
            }
        });
    }

    private void exibirInformacoesNoMapa(IpApiResult ipApiResult) {
        if (googleMap != null) {
            LatLng ipLocation = new LatLng(ipApiResult.getLat(), ipApiResult.getLon());
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(ipLocation).title("Localização do IP"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ipLocation, 12));
        }
    }

    private void exibirInformacoesDoIp(IpApiResult ipApiResult) {
        String types = "Continente:\n" +
                "Country Code:\n" +
                "País:\n" +
                "Região:\n" +
                "Cidade:";

        String results = ipApiResult.getCountry() + "\n" +
                ipApiResult.getCountryCode() + "\n" +
                ipApiResult.getRegionName() + "\n" +
                ipApiResult.getRegion() + "\n" +
                ipApiResult.getCity();

        TextView textViewIpInfoTypes = findViewById(R.id.textViewIpInfoTipos);
        TextView textViewIpInfoResults = findViewById(R.id.textViewIpInfoResults);

        textViewIpInfoTypes.setText(types);
        textViewIpInfoResults.setText(results);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
