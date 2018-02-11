package br.org.meiaentrada.validadorcie.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseManager extends SQLiteOpenHelper {

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

    public DatabaseManager(Context context, SQLiteDatabase.CursorFactory factory) {

        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_CAPTURAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CERTIFICADO + " TEXT,"
                + COLUMN_RESULTADO + " TEXT,"
                + COLUMN_HORARIO + " TEXT,"
                + COLUMN_EVENTO + " TEXT,"
                + COLUMN_LATITUDE + " TEXT,"
                + COLUMN_LONGITUDE + " TEXT,"
                + COLUMN_ID_DISPOSITIVO + " TEXT" +
                ", CONSTRAINT restricao UNIQUE (certificado, evento)" +
                ")";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAPTURAS);
        onCreate(db);

    }

}
