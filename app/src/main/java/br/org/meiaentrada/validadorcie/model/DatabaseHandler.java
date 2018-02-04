package br.org.meiaentrada.validadorcie.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "validador_cie.db";

    // Table: capturas
    public static final String TABLE_CAPTURAS = "capturas";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CERTIFICADO = "certificado";
    public static final String COLUMN_RESULTADO = "resultado";
    public static final String COLUMN_HORARIO = "horario";
    public static final String COLUMN_EVENTO = "evento";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ID_DISPOSITIVO = "idDispositivo";

    public DatabaseHandler(Context context, SQLiteDatabase.CursorFactory factory) {

        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_CAPTURAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_CERTIFICADO + " TEXT,"
                + COLUMN_RESULTADO + " TEXT,"
                + COLUMN_HORARIO + " TEXT,"
                + COLUMN_EVENTO + " TEXT,"
                + COLUMN_LATITUDE + " TEXT,"
                + COLUMN_LONGITUDE + " TEXT,"
                + COLUMN_ID_DISPOSITIVO + " TEXT,"
                + "CONSTRAINT restricao UNIQUE (certificado, horario));";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAPTURAS);
        onCreate(db);

    }

    public void addCaptura(Captura captura) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_CERTIFICADO, captura.getHashCertificado());
        values.put(COLUMN_RESULTADO, captura.getResultado());
        values.put(COLUMN_HORARIO, captura.getHorario());
        values.put(COLUMN_EVENTO, captura.getEvento());
        values.put(COLUMN_LATITUDE, captura.getLatitude());
        values.put(COLUMN_LONGITUDE, captura.getLongitude());
        values.put(COLUMN_ID_DISPOSITIVO, captura.getIdDispositivo());

        SQLiteDatabase database = getWritableDatabase();
        database.insert(TABLE_CAPTURAS, null, values);
        database.close();

    }

    public void deleteCaptura(String id) {

        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_CAPTURAS, "id = ?", new String[]{id});
        database.close();

    }

    public Captura nextCaptura() {

        String selectQuery = "SELECT * FROM " + TABLE_CAPTURAS + " LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
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

    public int totalOfCapturas() {

        SQLiteDatabase database = this.getReadableDatabase();
        Integer total = (int) DatabaseUtils.queryNumEntries(database, TABLE_CAPTURAS);
        database.close();
        return total;

    }

}
