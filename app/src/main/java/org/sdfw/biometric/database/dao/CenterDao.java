package org.sdfw.biometric.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.sdfw.biometric.model.Center;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;


@Dao
public interface CenterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Center center);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(Center... centers);

    @Query("DELETE FROM centers WHERE branch_id = :branchId")
    int deleteAll(String branchId);

    @Query("SELECT * FROM centers WHERE id = :id LIMIT 1")
    Maybe<Center> get(long id);

    @Query("SELECT * FROM centers WHERE center_id = :centerId LIMIT 1")
    Maybe<Center> get(String centerId);

    @Query("SELECT * FROM centers WHERE branch_id = :branchId")
    Flowable<List<Center>> getAll(String branchId);
}
