package org.sdfw.biometric.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.sdfw.biometric.model.EarlySettlement;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;


@Dao
public interface EarlySettlementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(EarlySettlement earlySettlement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(EarlySettlement... earlySettlements);

    @Query("SELECT * FROM early_settlements WHERE id = :id LIMIT 1")
    Maybe<EarlySettlement> get(long id);

    @Query("SELECT * FROM early_settlements WHERE member_id = :memberId LIMIT 1")
    Maybe<EarlySettlement> get(String memberId);

    @Query("SELECT * FROM early_settlements WHERE day_opening = :openingDate AND branch_id = :branchId AND center_id = :centerId AND member_id = :memberId LIMIT 1")
    Maybe<EarlySettlement> get(String openingDate, String branchId, String centerId, String memberId);

    @Query("SELECT * FROM early_settlements WHERE branch_id = :branchId")
    Flowable<List<EarlySettlement>> getAll(String branchId);

    @Query("DELETE FROM early_settlements WHERE branch_id = :branchId")
    int deleteAll(String branchId);

    @Query("UPDATE early_settlements SET is_synced = :isSynced" +
            " WHERE day_opening = :openingDate AND branch_id = :branchId AND center_id = :centerId AND member_id = :memberId")
    int updateSyncStatus(String openingDate, String branchId, String centerId, String memberId, String isSynced);
}
