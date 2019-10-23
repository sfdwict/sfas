package org.sdfw.biometric.repository;

import org.sdfw.biometric.database.dao.EarlySettlementDao;
import org.sdfw.biometric.model.EarlySettlement;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;


public class EarlySettlementRepository {

    private EarlySettlementDao earlySettlementDao;

    @Inject
    public EarlySettlementRepository(EarlySettlementDao earlySettlementDao) {
        this.earlySettlementDao = earlySettlementDao;
    }

    public Flowable<List<EarlySettlement>> getAll(String branchId) {
        return earlySettlementDao.getAll(branchId);
    }

    public Maybe<EarlySettlement> getEarlySettlement(Long id) {
        return earlySettlementDao.get(id);
    }

    public Maybe<EarlySettlement> findEarlySettlement(String memberId) {
        return earlySettlementDao.get(memberId);
    }

    public Maybe<EarlySettlement> findEarlySettlement(String openingDate, String branchId, String centerId, String memberId) {
        return earlySettlementDao.get(openingDate, branchId, centerId, memberId);
    }

    public long save(String memberId) {
        EarlySettlement earlySettlement = new EarlySettlement();
        earlySettlement.setMemberId(memberId);
        return earlySettlementDao.save(earlySettlement);
    }

    public void saveAll(List<EarlySettlement> earlySettlements) {
        earlySettlementDao.saveAll(earlySettlements.toArray(new EarlySettlement[earlySettlements.size()]));
    }

    public int deleteAll(String branchId) {
        return earlySettlementDao.deleteAll(branchId);
    }

    public int updateSyncStatus(String openingDate, String branchId, String centerId, String memberId, String isSynced) {
        return earlySettlementDao.updateSyncStatus(openingDate, branchId, centerId, memberId, isSynced);
    }
}
