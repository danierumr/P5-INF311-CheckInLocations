package inf311.daniel.checkinlocations;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GestaoActivity  extends AppCompatActivity {

    LinearLayout layoutConteudo;

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_gestao);

        layoutConteudo = (LinearLayout) findViewById(R.id.layoutConteudo);

        carregarLista();
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

    private void carregarLista() {
        BancoDados banco = new BancoDados(this);
        Cursor c = banco.buscar("Checkin", null, "", "");

        while (c.moveToNext()) {
            String nome = c.getString(c.getColumnIndexOrThrow("Local"));
            int cat = c.getInt(c.getColumnIndexOrThrow("cat"));
            String latitude = c.getString(c.getColumnIndexOrThrow("latitude"));
            String longitude = c.getString(c.getColumnIndexOrThrow("longitude"));

            // Layout horizontal com TextView e botão
            LinearLayout linha = new LinearLayout(this);
            linha.setOrientation(LinearLayout.HORIZONTAL);

            TextView txt = new TextView(this);
            txt.setText(nome);
            txt.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            ImageButton btnDelete = new ImageButton(this);
            btnDelete.setImageResource(android.R.drawable.ic_delete);
            btnDelete.setTag(nome);
            btnDelete.setOnClickListener(this::cliqueDeletar);
            btnDelete.setBackground(null);

            linha.addView(txt);
            linha.addView(btnDelete);

            layoutConteudo.addView(linha);
        }

        c.close();
    }

    public void cliqueDeletar(View v) {
        String local = v.getTag().toString();

        new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Deseja deletar o local \"" + local + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    BancoDados banco = new BancoDados(this);
                    banco.deletar("Checkin", "Local = '" + local + "'");

                    // Recarregar tela
                    finish();
                    startActivity(getIntent());
                })
                .setNegativeButton("Não", null)
                .show();
    }

}
