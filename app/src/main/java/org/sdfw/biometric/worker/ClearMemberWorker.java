package org.sdfw.biometric.worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static org.sdfw.biometric.util.Constant.TAG;

public class ClearMemberWorker extends Worker {

    public ClearMemberWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: logged");
        return Worker.Result.success();
    }
}
