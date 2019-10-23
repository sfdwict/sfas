package org.sdfw.biometric.repository;

import org.sdfw.biometric.database.dao.MemberDao;
import org.sdfw.biometric.model.Member;
import org.sdfw.biometric.model.MemberLite;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class MemberRepository {

    private MemberDao memberDao;

    @Inject
    public MemberRepository(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public Flowable<List<MemberLite>> getAll(String branchId, String centerId) {
        return memberDao.getAll(branchId, centerId);
    }

    public Single<List<Member>> getMemberByStatus(String branchId, String centerId) {
        return memberDao.getMembersByStatus(branchId, centerId);
    }

    public Maybe<Member> getMember(Long id) {
        return memberDao.get(id);
    }

    public Maybe<Member> findMember(String branchId, String centerId, String memberId) {
        return memberDao.get(branchId, centerId, memberId);
    }

    public Member find(String branchId, String centerId, String memberId) {
        return memberDao.find(branchId, centerId, memberId);
    }

    public long save(String memberId) {
        Member member = new Member();
        member.setMemberId(memberId);
        return memberDao.save(member);
    }

    public void saveAll(List<Member> members) {
        memberDao.saveAll(members.toArray(new Member[members.size()]));
    }

    public Flowable<Integer> getCount(String branchId, String centerId) {
        return memberDao.getCount(branchId, centerId);
    }

    public int update(Member member) {
        return memberDao.update(member);
    }

    public int updateVerificationStatus(String branchId, String centerId, String memberId, String status) {
        return memberDao.updateStatus(branchId, centerId, memberId, status);
    }

    public int deleteAllByBranch(String branchId) {
        return memberDao.deleteAllByBranch(branchId);
    }

    public int deleteAll() {
        return memberDao.deleteAll();
    }
}
