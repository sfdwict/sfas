package org.sdfw.biometric.di.module;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.sdfw.biometric.R;
import org.sdfw.biometric.di.scope.PerActivity;

import dagger.Module;
import dagger.Provides;
import dmax.dialog.SpotsDialog;

@Module(includes = ActivityContextModule.class)
public class DialogModule {

    // --- ALERT DIALOG

    @Provides
    @PerActivity
    public AlertDialog provideSpotDialog(AppCompatActivity activity){
        return new SpotsDialog.Builder()
                .setContext(activity)
                .setCancelable(false)
                .setTheme(R.style.Dialog_Loading)
                .build();
    }

}
