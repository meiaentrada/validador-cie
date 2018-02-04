package br.org.meiaentrada.validadorcie.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import io.reactivex.Flowable;


@Dao
public interface CapturaRepository {

    @Query("SELECT * FROM capturas LIMIT 1")
    Flowable<Captura2> getCaptura();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Captura2 captura);

    @Query("DELETE FROM capturas")
    void deleteAllCapturas();

}
