package org.sdfw.biometric.repository;

import org.sdfw.biometric.database.dao.CenterDao;
import org.sdfw.biometric.model.Center;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;


public class CenterRepository {

    private CenterDao centerDao;

    @Inject
    public CenterRepository(CenterDao centerDao) {
        this.centerDao = centerDao;
    }

    public Flowable<List<Center>> getAll(String branchId) {
        return centerDao.getAll(branchId);
    }

    public Maybe<Center> getCenter(Long id) {
        return centerDao.get(id);
    }

    public Maybe<Center> findCenter(String centerId) {
        return centerDao.get(centerId);
    }

    public long save(String centerId, String centerName, String branchId) {
        Center center = new Center();
        center.setCenterId(centerId);
        center.setCenterName(centerName);
        center.setBranchId(branchId);
        return centerDao.save(center);
    }

    public void saveAll(List<Center> centers) {
        centerDao.saveAll(centers.toArray(new Center[centers.size()]));
    }

    public int deleteAll(String branchId) {
        return centerDao.deleteAll(branchId);
    }
}
