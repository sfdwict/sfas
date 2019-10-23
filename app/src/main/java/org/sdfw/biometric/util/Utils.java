package org.sdfw.biometric.util;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class Utils {

    public static double getDoubleFromString(String numericString) {
        if (numericString == null || numericString.isEmpty()) {
            return 0;
        }
        return Double.valueOf(numericString);
    }

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE members ADD COLUMN savers_only_flag TEXT");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE loan_disbursements ADD COLUMN passbook_n TEXT");
        }
    };

    public static final Migration MIGRATION_2_4 = new Migration(2, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE members ADD COLUMN savers_only_flag TEXT");
            database.execSQL("ALTER TABLE loan_disbursements ADD COLUMN passbook_n TEXT");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE saving_withdrawns ADD COLUMN lakhpati_amount REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE saving_withdrawns ADD COLUMN double_savings_amount REAL NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_3_5 = new Migration(3, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE loan_disbursements ADD COLUMN passbook_n TEXT");
            database.execSQL("ALTER TABLE saving_withdrawns ADD COLUMN lakhpati_amount REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE saving_withdrawns ADD COLUMN double_savings_amount REAL NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_2_5 = new Migration(2, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE members ADD COLUMN savers_only_flag TEXT");
            database.execSQL("ALTER TABLE loan_disbursements ADD COLUMN passbook_n TEXT");
            database.execSQL("ALTER TABLE saving_withdrawns ADD COLUMN lakhpati_amount REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE saving_withdrawns ADD COLUMN double_savings_amount REAL NOT NULL DEFAULT 0");
        }
    };
}
