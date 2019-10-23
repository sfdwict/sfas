package org.sdfw.biometric.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.sdfw.biometric.model.SavingWithdrawn;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;


@Dao
public interface SavingWithdrawnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(SavingWithdrawn savingWithdrawn);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(SavingWithdrawn... savingWithdrawns);

    @Query("SELECT * FROM saving_withdrawns WHERE id = :id LIMIT 1")
    Maybe<SavingWithdrawn> get(long id);

    @Query("SELECT * FROM saving_withdrawns WHERE member_id = :memberId LIMIT 1")
    Maybe<SavingWithdrawn> get(String memberId);

    @Query("SELECT * FROM saving_withdrawns WHERE day_opening = :openingDate AND branch_id = :branchId AND center_id = :centerId AND member_id = :memberId LIMIT 1")
    Maybe<SavingWithdrawn> get(String openingDate, String branchId, String centerId, String memberId);

    @Query("SELECT * FROM saving_withdrawns WHERE branch_id = :branchId")
    Flowable<List<SavingWithdrawn>> getAll(String branchId);

    @Query("DELETE FROM saving_withdrawns WHERE branch_id = :branchId")
    int deleteAll(String branchId);

    @Query("UPDATE saving_withdrawns SET is_synced = :isSynced" +
            " WHERE day_opening = :openingDate AND branch_id = :branchId AND center_id = :centerId AND member_id = :memberId")
    int updateSyncStatus(String openingDate, String branchId, String centerId, String memberId, String isSynced);
}
