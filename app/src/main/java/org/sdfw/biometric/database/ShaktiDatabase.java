package org.sdfw.biometric.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.sdfw.biometric.database.dao.CenterDao;
import org.sdfw.biometric.database.dao.EarlySettlementDao;
import org.sdfw.biometric.database.dao.LoanDisbursementDao;
import org.sdfw.biometric.database.dao.MemberDao;
import org.sdfw.biometric.database.dao.SavingWithdrawnDao;
import org.sdfw.biometric.database.dao.UserDao;
import org.sdfw.biometric.model.Center;
import org.sdfw.biometric.model.EarlySettlement;
import org.sdfw.biometric.model.LoanDisbursement;
import org.sdfw.biometric.model.Member;
import org.sdfw.biometric.model.SavingWithdrawn;
import org.sdfw.biometric.model.User;

@Database(entities = {User.class, Center.class, Member.class, LoanDisbursement.class, SavingWithdrawn.class,
        EarlySettlement.class}, version = 5, exportSchema = false)
public abstract class ShaktiDatabase extends RoomDatabase {

    public static volatile ShaktiDatabase INSTANCE;
    public abstract UserDao userDao();
    public abstract CenterDao centerDao();
    public abstract MemberDao memberDao();
    public abstract LoanDisbursementDao loanDisbursementDao();
    public abstract SavingWithdrawnDao savingWithdrawDao();
    public abstract EarlySettlementDao earlySettlementDao();
}
