package org.sdfw.biometric.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.sdfw.biometric.model.User;

import io.reactivex.Maybe;


@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(User user);

    @Query("UPDATE users SET template1 = :template1, template2 = :template2, template3 = :template3, " +
            "template4 = :template4, device_id = :deviceId, device_token = :deviceToken WHERE user_id = :ein")
    int update(String ein, String template1, String template2, String template3, String template4,
               String deviceId, String deviceToken);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    Maybe<User> get(long id);

    @Query("SELECT * FROM users WHERE user_id = :ein LIMIT 1")
    Maybe<User> get(String ein);
}
