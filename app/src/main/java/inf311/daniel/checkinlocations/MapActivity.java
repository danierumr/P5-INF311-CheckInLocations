package inf311.daniel.checkinlocations;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", -20.7578);
        longitude = intent.getDoubleExtra("longitude", -42.8754);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng localAtual = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localAtual, 15f));

        BancoDados banco = new BancoDados(this);
        Cursor c = banco.buscar("Checkin", null, "", "");

        while (c.moveToNext()) {
            String nome = c.getString(c.getColumnIndexOrThrow("Local"));
            int visitas = c.getInt(c.getColumnIndexOrThrow("qtdVisitas"));
            int idCategoria = c.getInt(c.getColumnIndexOrThrow("cat"));
            double lat = Double.parseDouble(c.getString(c.getColumnIndexOrThrow("latitude")));
            double lng = Double.parseDouble(c.getString(c.getColumnIndexOrThrow("longitude")));

            Cursor cat = banco.buscar("Categoria", new String[]{"nome"}, "idCategoria = " + idCategoria, "");
            String nomeCategoria = (cat.moveToFirst()) ? cat.getString(0) : "Desconhecida";
            cat.close();

            LatLng pos = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(nome)
                    .snippet(nomeCategoria + " - " + visitas + " visita(s)"));
        }

        c.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_voltar) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.menu_gestao) {
            startActivity(new Intent(this, GestaoActivity.class));
            return true;
        } else if (id == R.id.menu_relatorio) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        } else if (id == R.id.menu_normal) {
            if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        } else if (id == R.id.menu_hibrido) {
            if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
