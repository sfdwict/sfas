package org.sdfw.biometric.repository;

import org.sdfw.biometric.database.dao.UserDao;
import org.sdfw.biometric.model.User;

import javax.inject.Inject;

import io.reactivex.Maybe;


public class UserRepository {

    private UserDao userDao;

    @Inject
    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public Maybe<User> getUser(Long id) {
        return userDao.get(id);
    }

    public Maybe<User> findUser(String ein) {
        return userDao.get(ein);
    }

    public long saveUser(User user) {
        return userDao.save(user);
    }

    public int saveFingerprints(String ein, String template1, String template2, String template3, String template4,
                       String deviceId, String deviceToken) {
        return userDao.update(ein, template1, template2, template3, template4, deviceId, deviceToken);
    }
}
