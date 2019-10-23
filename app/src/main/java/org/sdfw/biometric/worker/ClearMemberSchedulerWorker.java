package org.sdfw.biometric.worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static org.sdfw.biometric.util.Constant.TAG;

public class ClearMemberSchedulerWorker extends Worker {

    public ClearMemberSchedulerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: Scheduling clean member job");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresDeviceIdle(false)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(false)
                .build();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(ClearMemberWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("clear_members", ExistingPeriodicWorkPolicy.REPLACE, request);
        return Worker.Result.success();
    }
}
