package org.sdfw.biometric.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.sdfw.biometric.model.Member;
import org.sdfw.biometric.model.MemberLite;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;


@Dao
public interface MemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Member member);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(Member... members);

    @Query("SELECT COUNT(*) from members WHERE branch_id = :branchId AND center_id = :centerId")
    Flowable<Integer> getCount(String branchId, String centerId);

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    Maybe<Member> get(long id);

    @Query("SELECT * FROM members WHERE branch_id = :branchId AND center_id = :centerId AND member_id = :memberId LIMIT 1")
    Maybe<Member> get(String branchId, String centerId, String memberId);

    @Query("SELECT * FROM members WHERE branch_id = :branchId AND center_id = :centerId AND member_id = :memberId LIMIT 1")
    Member find(String branchId, String centerId, String memberId);

    @Query("SELECT branch_id, center_id, member_id, member_name, is_new_member, has_fingerprint, has_image, has_voter_id FROM members WHERE branch_id = :branchId AND center_id = :centerId")
    Flowable<List<MemberLite>> getAll(String branchId, String centerId);

    @Query("SELECT * FROM members WHERE branch_id = :branchId AND center_id = :centerId " +
            "AND is_new_member = 0 AND verification_status = 'V'")
    Single<List<Member>> getMembersByStatus(String branchId, String centerId);

    @Update
    int update(Member member);

    @Query("UPDATE members SET verification_status = :verificationStatus" +
            " WHERE branch_id = :branchId AND center_id = :centerId AND member_id = :memberId")
    int updateStatus(String branchId, String centerId, String memberId, String verificationStatus);

    @Query("DELETE FROM members WHERE branch_id = :branchId")
    int deleteAllByBranch(String branchId);

    @Query("DELETE FROM members")
    int deleteAll();
}
