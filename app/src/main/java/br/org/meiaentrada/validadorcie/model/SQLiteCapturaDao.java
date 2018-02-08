package br.org.meiaentrada.validadorcie.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.Optional;


public class SQLiteCapturaDao implements CapturaDao {

    private DatabaseManager databaseHandler;

    public SQLiteCapturaDao(DatabaseManager database) {

        this.databaseHandler = database;

    }

    @Override
    public Captura next() {

        String selectQuery = "SELECT * FROM " + DatabaseManager.TABLE_CAPTURAS + " LIMIT 1";
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Captura captura = new Captura();
        captura.setId("");

        if (cursor.moveToFirst()) {

            Integer idint = cursor.getInt(0);
            captura.setId(idint.toString());
            captura.setCertificado(cursor.getString(1));
            captura.setResultado(cursor.getString(2));
            captura.setHorario(cursor.getString(3));
            captura.setEvento(cursor.getString(4));
            captura.setLatitude(cursor.getString(5));
            captura.setLongitude(cursor.getString(6));
            captura.setIdDispositivo(cursor.getString(7));

        }

        cursor.close();
        db.close();

        return captura;

    }

    @Override
    public <S extends Captura> S save(Captura captura) {

        ContentValues values = new ContentValues();
        values.put(DatabaseManager.COLUMN_CERTIFICADO, captura.getHashCertificado());
        values.put(DatabaseManager.COLUMN_RESULTADO, captura.getResultado());
        values.put(DatabaseManager.COLUMN_HORARIO, captura.getHorario());
        values.put(DatabaseManager.COLUMN_EVENTO, captura.getEvento());
        values.put(DatabaseManager.COLUMN_LATITUDE, captura.getLatitude());
        values.put(DatabaseManager.COLUMN_LONGITUDE, captura.getLongitude());
        values.put(DatabaseManager.COLUMN_ID_DISPOSITIVO, captura.getIdDispositivo());

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.insert(DatabaseManager.TABLE_CAPTURAS, null, values);
        db.close();

        return null;

    }

    @Override
    public Optional<Captura> findById(String id) {
        return null;
    }

    @Override
    public Iterable<Captura> findAll() {
        return null;
    }

    @Override
    public int count() {

        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Integer total = (int) DatabaseUtils.queryNumEntries(db, DatabaseManager.TABLE_CAPTURAS);
        db.close();
        return total;

    }

    @Override
    public Captura update(Captura entity) {
        return null;
    }

    @Override
    public void delete(String id) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(DatabaseManager.TABLE_CAPTURAS, "id = ?", new String[]{id});
        db.close();

    }

    @Override
    public boolean existsById(String id) {
        return false;
    }

}
