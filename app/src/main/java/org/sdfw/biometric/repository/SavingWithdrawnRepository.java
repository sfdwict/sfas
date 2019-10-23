package org.sdfw.biometric.repository;

import org.sdfw.biometric.database.dao.SavingWithdrawnDao;
import org.sdfw.biometric.model.SavingWithdrawn;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;


public class SavingWithdrawnRepository {

    private SavingWithdrawnDao savingWithdrawnDao;

    @Inject
    public SavingWithdrawnRepository(SavingWithdrawnDao savingWithdrawnDao) {
        this.savingWithdrawnDao = savingWithdrawnDao;
    }

    public Flowable<List<SavingWithdrawn>> getAll(String branchId) {
        return savingWithdrawnDao.getAll(branchId);
    }

    public Maybe<SavingWithdrawn> getSavingWithdrawn(Long id) {
        return savingWithdrawnDao.get(id);
    }

    public Maybe<SavingWithdrawn> findSavingWithdrawn(String memberId) {
        return savingWithdrawnDao.get(memberId);
    }

    public Maybe<SavingWithdrawn> findSavingWithdrawn(String openingDate, String branchId, String centerId, String memberId) {
        return savingWithdrawnDao.get(openingDate, branchId, centerId, memberId);
    }

    public long save(String memberId) {
        SavingWithdrawn savingWithdrawn = new SavingWithdrawn();
        savingWithdrawn.setMemberId(memberId);
        return savingWithdrawnDao.save(savingWithdrawn);
    }

    public void saveAll(List<SavingWithdrawn> savingWithdrawns) {
        savingWithdrawnDao.saveAll(savingWithdrawns.toArray(new SavingWithdrawn[savingWithdrawns.size()]));
    }

    public int deleteAll(String branchId) {
        return savingWithdrawnDao.deleteAll(branchId);
    }

    public int updateSyncStatus(String openingDate, String branchId, String centerId, String memberId, String isSynced) {
        return savingWithdrawnDao.updateSyncStatus(openingDate, branchId, centerId, memberId, isSynced);
    }
}
