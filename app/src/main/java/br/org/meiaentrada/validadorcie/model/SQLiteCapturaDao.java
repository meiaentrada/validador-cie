package br.org.meiaentrada.validadorcie.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class SQLiteCapturaDao implements CapturaDao {

    private DatabaseManager databaseHandler;

    public SQLiteCapturaDao(DatabaseManager database) {

        this.databaseHandler = database;

    }

    @Override
    public Captura next() {

        String query = "SELECT * FROM " + DatabaseManager.TABLE_CAPTURAS + " LIMIT 1";
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Captura captura = null;

        if (cursor.moveToFirst()) {

            captura = new Captura();
            captura.setId(cursor.getInt(0));
            captura.setCertificado(cursor.getString(1));
            captura.setStatus(cursor.getString(2));
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
    public Captura save(Captura captura) {

        ContentValues values = new ContentValues();
        values.put(DatabaseManager.COLUMN_CERTIFICADO, captura.getHashCertificado());
        values.put(DatabaseManager.COLUMN_RESULTADO, captura.getStatus());
        values.put(DatabaseManager.COLUMN_HORARIO, captura.getHorario());
        values.put(DatabaseManager.COLUMN_EVENTO, captura.getEvento());
        values.put(DatabaseManager.COLUMN_LATITUDE, captura.getLatitude());
        values.put(DatabaseManager.COLUMN_LONGITUDE, captura.getLongitude());
        values.put(DatabaseManager.COLUMN_ID_DISPOSITIVO, captura.getIdDispositivo());

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        int lastInsertedId = (int) db.insert(DatabaseManager.TABLE_CAPTURAS, null, values);
        db.close();

        captura.setId(lastInsertedId);
        return findById(lastInsertedId);

    }

    @Override
    public Captura findById(Integer id) {

        Captura captura = null;

        String query = "SELECT * FROM " + DatabaseManager.TABLE_CAPTURAS + " WHERE id = " + id;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            captura = new Captura();
            captura.setId(cursor.getInt(0));
            captura.setCertificado(cursor.getString(1));
            captura.setStatus(cursor.getString(2));
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
    public List<Captura> findAll() {

        String query = "SELECT * FROM " + DatabaseManager.TABLE_CAPTURAS;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        List<Captura> capturas = new ArrayList<>();

        if (cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {

                Captura captura = new Captura();
                captura.setId(cursor.getInt(0));
                captura.setCertificado(cursor.getString(1));
                captura.setStatus(cursor.getString(2));
                captura.setHorario(cursor.getString(3));
                captura.setEvento(cursor.getString(4));
                captura.setLatitude(cursor.getString(5));
                captura.setLongitude(cursor.getString(6));
                captura.setIdDispositivo(cursor.getString(7));

                capturas.add(captura);

                cursor.moveToNext();

            }
            cursor.close();
            databaseHandler.close();

        }
        return capturas;

    }

    @Override
    public int count() {

        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Integer total = (int) DatabaseUtils.queryNumEntries(db, DatabaseManager.TABLE_CAPTURAS);
        db.close();
        return total;

    }

    @Override
    public Captura update(Captura captura) {
        return null;
    }

    @Override
    public void delete(Integer id) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(DatabaseManager.TABLE_CAPTURAS, "id = ?", new String[]{id.toString()});
        db.close();

    }

    @Override
    public boolean existsById(Integer id) {
        return false;
    }

}
