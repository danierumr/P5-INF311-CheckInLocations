package inf311.daniel.checkinlocations;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BancoDados bd;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private double latitudeAtual = 0.0;
    private double longitudeAtual = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bd = new BancoDados(this);

        // Nome do local
        AutoCompleteTextView autoCompleteLocal = (AutoCompleteTextView) findViewById(R.id.edit_local);
        List<String> nomesLocais = bd.getNomesLocais();

        ArrayAdapter<String> adapterLocais = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nomesLocais
        );
        autoCompleteLocal.setAdapter(adapterLocais);

        // Categoria do Local
        Spinner spinnerCat = (Spinner) findViewById(R.id.spinner_cat);
        List<String> nomesCategorias = bd.getNomesCategorias();
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nomesCategorias
        );
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCat.setAdapter(adapterCategorias);

        // Localizacao
        TextView latText = (TextView) findViewById(R.id.val_lat);
        TextView longText = (TextView) findViewById(R.id.val_long);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            iniciarAtualizacaoLocalizacao(latText, longText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_mapa) {
            if (latitudeAtual != 0.0 && longitudeAtual != 0.0) {
                Intent intentMapa = new Intent(this, MapActivity.class);
                intentMapa.putExtra("latitude", latitudeAtual);
                intentMapa.putExtra("longitude", longitudeAtual);
                startActivity(intentMapa);
            } else {
                Toast.makeText(this, "Aguardando localização atual...", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.menu_gestao) {
            startActivity(new Intent(this, GestaoActivity.class));
            return true;

        } else if (id == R.id.menu_relatorio) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private void iniciarAtualizacaoLocalizacao(TextView latText, TextView longText) {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                Location location = locationResult.getLastLocation();
                latitudeAtual = location.getLatitude();
                longitudeAtual = location.getLongitude();

                latText.setText(String.valueOf(latitudeAtual));
                longText.setText(String.valueOf(longitudeAtual));
                Log.i("LOCALIZACAO", "Atualizou sera?");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("LOCALIZACAO", "Requisitou updates");
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    public void onClickCheckIn(View view) {
        String nomeLocal = ((AutoCompleteTextView) findViewById(R.id.edit_local)).getText().toString().trim();
        int posCategoria = ((Spinner) findViewById(R.id.spinner_cat)).getSelectedItemPosition();
        String latitude = String.valueOf(latitudeAtual);
        String longitude = String.valueOf(longitudeAtual);

        if (nomeLocal.isEmpty()) {
            Toast.makeText(this, "Digite o nome do local.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (posCategoria == Spinner.INVALID_POSITION) {
            Toast.makeText(this, "Selecione uma categoria.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (latitudeAtual == 0.0 && longitudeAtual == 0.0) {
            Toast.makeText(this, "Aguardando posição do GPS.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = bd.buscar("Checkin", new String[]{"qtdVisitas"}, "Local = '" + nomeLocal + "'", "");

        if (cursor.getCount() == 0) {
            // ✅ Inserir novo local
            ContentValues valores = new ContentValues();
            valores.put("Local", nomeLocal);
            valores.put("qtdVisitas", 1);
            valores.put("cat", posCategoria + 1); // IDs começam em 1
            valores.put("latitude", latitude);
            valores.put("longitude", longitude);

            bd.inserir("Checkin", valores);
            Toast.makeText(this, "Check-in realizado com sucesso!", Toast.LENGTH_SHORT).show();

        } else {
            // ✅ Atualizar qtdVisitas (somente isso)
            cursor.moveToFirst();
            int visitas = cursor.getInt(0) + 1;

            ContentValues valores = new ContentValues();
            valores.put("qtdVisitas", visitas);

            bd.atualizar("Checkin", valores, "Local = '" + nomeLocal + "'");
            Toast.makeText(this, "Check-in atualizado!", Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        finish();
        startActivity(getIntent());

    }



}