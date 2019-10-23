package org.sdfw.biometric.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.sdfw.biometric.BuildConfig;
import org.sdfw.biometric.database.ShaktiDatabase;
import org.sdfw.biometric.database.dao.CenterDao;
import org.sdfw.biometric.database.dao.EarlySettlementDao;
import org.sdfw.biometric.database.dao.LoanDisbursementDao;
import org.sdfw.biometric.database.dao.MemberDao;
import org.sdfw.biometric.database.dao.SavingWithdrawnDao;
import org.sdfw.biometric.database.dao.UserDao;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.repository.CenterRepository;
import org.sdfw.biometric.repository.EarlySettlementRepository;
import org.sdfw.biometric.repository.LoanDisbursementRepository;
import org.sdfw.biometric.repository.MemberRepository;
import org.sdfw.biometric.repository.SavingWithdrawnRepository;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import java.lang.annotation.Annotation;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.sdfw.biometric.util.Constant.BASE_URL;
import static org.sdfw.biometric.util.Constant.KEY_PREF_SESSION;
import static org.sdfw.biometric.util.Utils.MIGRATION_2_3;
import static org.sdfw.biometric.util.Utils.MIGRATION_2_5;
import static org.sdfw.biometric.util.Utils.MIGRATION_3_4;
import static org.sdfw.biometric.util.Utils.MIGRATION_2_4;
import static org.sdfw.biometric.util.Utils.MIGRATION_3_5;
import static org.sdfw.biometric.util.Utils.MIGRATION_4_5;

@Module(includes = ViewModelModule.class)
public class AppModule {

    // --- SHARED PREF INJECTION ---

    @Provides
    @Singleton
    SharedPreferences provideSharedPereferences(Application application) {
        return application.getSharedPreferences(KEY_PREF_SESSION, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    SessionManager provideSessionManager(SharedPreferences sharedPreferences) {
        return new SessionManager(sharedPreferences);
    }

    // --- USB MANAGER INJECTION ---

    @Provides
    @Singleton
    UsbManager provideUsbManager(Application application) {
        return (UsbManager) application.getSystemService(Context.USB_SERVICE);
    }

    // --- DATABASE INJECTION ---

    @Provides
    @Singleton
    ShaktiDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application,
                ShaktiDatabase.class, "shakti.db")
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_2_4,
                        MIGRATION_4_5, MIGRATION_3_5, MIGRATION_2_5)
                .build();
    }

    @Provides
    @Singleton
    UserDao provideUserDao(ShaktiDatabase database) {
        return database.userDao();
    }

    @Provides
    @Singleton
    CenterDao provideCenterDao(ShaktiDatabase database) {
        return database.centerDao();
    }

    @Provides
    @Singleton
    MemberDao provideMemberDao(ShaktiDatabase database) {
        return database.memberDao();
    }

    @Provides
    @Singleton
    LoanDisbursementDao provideLoanDisbursementDao(ShaktiDatabase database) {
        return database.loanDisbursementDao();
    }

    @Provides
    @Singleton
    SavingWithdrawnDao provideSavingWithdrawDao(ShaktiDatabase database) {
        return database.savingWithdrawDao();
    }

    @Provides
    @Singleton
    EarlySettlementDao provideLoanPaymentDao(ShaktiDatabase database) {
        return database.earlySettlementDao();
    }

    // --- NETWORK INJECTION ---

    @Singleton
    @Provides
    Gson provideGson() { return new GsonBuilder().create(); }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();
    }

    @Singleton
    @Provides
    public Retrofit getRemoteClient(Gson gson, OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    ShaktiWebservice provideShaktiWebservice(Retrofit retrofit) {
        return retrofit.create(ShaktiWebservice.class);
    }

    @Singleton
    @Provides
    Converter<ResponseBody, MessageResponse> getConverter(Retrofit retrofit) {
        return retrofit.responseBodyConverter(MessageResponse.class, new Annotation[0]);
    }

    // --- EXECUTOR INJECTION ---

    @Provides
    ScheduledExecutorService provideScheduledExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    // --- REPOSITORY INJECTION ---

    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDao userDao) {
        return new UserRepository(userDao);
    }

    @Provides
    @Singleton
    MemberRepository provideMemberRepository(MemberDao memberDao) {
        return new MemberRepository(memberDao);
    }

    @Provides
    @Singleton
    CenterRepository provideCenterRepository(CenterDao centerDao) {
        return new CenterRepository(centerDao);
    }

    @Provides
    @Singleton
    LoanDisbursementRepository provideLoanDisbursementRepository(LoanDisbursementDao loanDisbursementDao) {
        return new LoanDisbursementRepository(loanDisbursementDao);
    }

    @Provides
    @Singleton
    SavingWithdrawnRepository provideSavingWithdrawnRepository(SavingWithdrawnDao savingWithdrawnDao) {
        return new SavingWithdrawnRepository(savingWithdrawnDao);
    }

    @Provides
    @Singleton
    EarlySettlementRepository provideEarlySettlementRepository(EarlySettlementDao earlySettlementDao) {
        return new EarlySettlementRepository(earlySettlementDao);
    }

//    @Provides
//    public CompositeDisposable getCompositeDisposable(){
//        return new CompositeDisposable();
//    }
}
