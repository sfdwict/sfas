package org.sdfw.biometric.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.sdfw.biometric.model.LoanDisbursement;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;


@Dao
public interface LoanDisbursementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(LoanDisbursement loanDisbursement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(LoanDisbursement... loanDisbursements);

    @Query("SELECT * FROM loan_disbursements WHERE id = :id LIMIT 1")
    Maybe<LoanDisbursement> get(long id);

    @Query("SELECT * FROM loan_disbursements WHERE day_opening = :openingDate AND branch_id = :branchId AND center_id = :centerId AND member_id = :memberId LIMIT 1")
    Maybe<LoanDisbursement> get(String openingDate, String branchId, String centerId, String memberId);

    @Query("SELECT * FROM loan_disbursements WHERE branch_id = :branchId")
    Flowable<List<LoanDisbursement>> getAll(String branchId);

    @Query("DELETE FROM loan_disbursements WHERE branch_id = :branchId")
    int deleteAll(String branchId);

    @Query("UPDATE loan_disbursements SET is_synced = :isSynced" +
            " WHERE day_opening = :openingDate AND branch_id = :branchId AND center_id = :centerId AND member_id = :memberId")
    int updateSyncStatus(String openingDate, String branchId, String centerId, String memberId, String isSynced);
}
