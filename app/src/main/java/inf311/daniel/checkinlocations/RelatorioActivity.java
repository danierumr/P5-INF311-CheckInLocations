package inf311.daniel.checkinlocations;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RelatorioActivity extends AppCompatActivity {

    LinearLayout layoutRelatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        layoutRelatorio = (LinearLayout) findViewById(R.id.layoutRelatorio);
        carregarRelatorio();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        menu.add(0, 1, 0, "Voltar");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == 1) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void carregarRelatorio() {
        BancoDados banco = new BancoDados(this);
        Cursor c = banco.buscar("Checkin", null, "", "qtdVisitas DESC");

        while (c.moveToNext()) {
            String nome = c.getString(c.getColumnIndexOrThrow("Local"));
            int qtd = c.getInt(c.getColumnIndexOrThrow("qtdVisitas"));

            // Linha horizontal com dois TextViews
            LinearLayout linha = new LinearLayout(this);
            linha.setOrientation(LinearLayout.HORIZONTAL);

            TextView txtNome = new TextView(this);
            txtNome.setText(nome);
            txtNome.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));

            TextView txtQtd = new TextView(this);
            txtQtd.setText("Visitas: " + qtd);
            txtQtd.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            linha.addView(txtNome);
            linha.addView(txtQtd);

            layoutRelatorio.addView(linha);
        }

        c.close();
    }

}
