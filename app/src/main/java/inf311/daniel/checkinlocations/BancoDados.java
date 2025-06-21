package inf311.daniel.checkinlocations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BancoDados {

    protected SQLiteDatabase db;

    private final String NOME_BANCO = "logs_location";

    private final String [] SCRIPT_DATABASE_CREATE = new String[] {
            "CREATE TABLE IF NOT EXISTS Checkin (Local TEXT PRIMARY KEY, qtdVisitas INTEGER NOT NULL, cat INTEGER NOT NULL, " +
                    "latitude TEXT NOT NULL, longitude TEXT NOT NULL, CONSTRAINT fkey0 FOREIGN KEY (cat) REFERENCES Categoria (idCategoria));",
            "CREATE TABLE Categoria (idCategoria INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL);",
            "INSERT INTO Categoria (nome) VALUES ('Restaurante');",
            "INSERT INTO Categoria (nome) VALUES ('Bar');",
            "INSERT INTO Categoria (nome) VALUES ('Cinema');",
            "INSERT INTO Categoria (nome) VALUES ('Universidade');",
            "INSERT INTO Categoria (nome) VALUES ('Estádio');",
            "INSERT INTO Categoria (nome) VALUES ('Parque');",
            "INSERT INTO Categoria (nome) VALUES ('Outros');"

};

    public BancoDados(Context ctx) {

        db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);

        Cursor c = buscar("sqlite_master", null, "type= 'table'", "");

        if(c.getCount() == 1) {
            for (String s : SCRIPT_DATABASE_CREATE) {
                db.execSQL(s);
            }
            Log.i("BANCO_DADOS", "Criou tabelas do banco e as populou");
        }

    }

    public long inserir(String tabela, ContentValues valores) {

        long id = db.insert(tabela, null, valores);

        Log.i("BANCO_DADOS", "Cadastrou registro com o id [" + id + "]");
        return id;
    }

    public int atualizar(String tabela, ContentValues valores, String where) {
        int count = db.update(tabela, valores, where, null);

        Log.i("BANCO_DADOS", "Atualizou [" + count + "] registros");
        return count;
    }

    public int deletar(String tabela, String where) {
        int count = db.delete(tabela, where, null);

        Log.i("BANCO_DADOS", "Deletou [" + count + "] registros");
        return count;
    }

    public Cursor buscar(String tabela, String colunas[], String where, String orderBy) {

        Cursor c;
        if(!where.isEmpty())
            c = db.query(tabela, colunas, where, null, null, null, orderBy);
        else
            c = db.query(tabela, colunas, null, null, null, null, orderBy);
        Log.i("BANCO_DADOS", "Realizou uma busca e retornou [" + c.getCount() + "] registros.");

        return c;
    }

    public void abrir(Context ctx) {
        db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        Log.i("BANCO_DADOS", "Abriu conexao com o banco");
    }

    public void fechar() {
        if (db != null) {
            db.close();
            Log.i("BANCO_DADOS", "Fechou conexao com o banco");
        }
    }

    public List<String> getNomesLocais() {
        List<String> nomes = new ArrayList<>();
        Cursor c = buscar("Checkin", new String[]{"Local"}, "", "Local ASC");
        while (c.moveToNext()) {
            nomes.add(c.getString(0));
        }
        c.close();
        return nomes;
    }

    public List<String> getNomesCategorias() {
        List<String> nomes = new ArrayList<>();
        Cursor c = buscar("Categoria", new String[]{"nome"}, "", "idCategoria ASC");
        while (c.moveToNext()) {
            nomes.add(c.getString(0));
        }
        c.close();
        return nomes;
    }

}
