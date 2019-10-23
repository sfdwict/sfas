package org.sdfw.biometric.repository;

import org.sdfw.biometric.database.dao.LoanDisbursementDao;
import org.sdfw.biometric.model.LoanDisbursement;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

public class LoanDisbursementRepository {

    private LoanDisbursementDao loanDisbursementDao;

    @Inject
    public LoanDisbursementRepository(LoanDisbursementDao loanDisbursementDao) {
        this.loanDisbursementDao = loanDisbursementDao;
    }

    public Flowable<List<LoanDisbursement>> getAll(String branchId) {
        return loanDisbursementDao.getAll(branchId);
    }

    public Maybe<LoanDisbursement> getLoanDisbursement(Long id) {
        return loanDisbursementDao.get(id);
    }

    public Maybe<LoanDisbursement> findLoanDisbursement(String openingDate, String branchId, String centerId, String memberId) {
        return loanDisbursementDao.get(openingDate, branchId, centerId, memberId);
    }

    public long save(String memberId) {
        LoanDisbursement loanDisbursement = new LoanDisbursement();
        loanDisbursement.setMemberId(memberId);
        return loanDisbursementDao.save(loanDisbursement);
    }

    public void saveAll(List<LoanDisbursement> loanDisbursements) {
        loanDisbursementDao.saveAll(loanDisbursements.toArray(new LoanDisbursement[loanDisbursements.size()]));
    }

    public int deleteAll(String branchId) {
        return loanDisbursementDao.deleteAll(branchId);
    }

    public int updateSyncStatus(String openingDate, String branchId, String centerId, String memberId, String isSynced) {
        return loanDisbursementDao.updateSyncStatus(openingDate, branchId, centerId, memberId, isSynced);
    }
}
