package br.org.meiaentrada.validadorcie.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {Captura2.class}, version = 1)
public abstract class CapturaDatabase extends RoomDatabase {

    private static volatile CapturaDatabase INSTANCE;

    public abstract CapturaRepository capturaRepository();

    public static CapturaDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CapturaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CapturaDatabase.class, "Sample.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
