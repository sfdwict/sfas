package org.sdfw.biometric.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.sdfw.biometric.BuildConfig;
import org.sdfw.biometric.R;
import org.sdfw.biometric.database.ShaktiDatabase;
import org.sdfw.biometric.model.Member;
import org.sdfw.biometric.network.ErrorUtils;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.repository.MemberRepository;
import org.sdfw.biometric.ui.SplashActivity;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.sdfw.biometric.util.Constant.BASE_URL;
import static org.sdfw.biometric.util.Constant.CHANNEL_ID_OFFLINE_SYNC;
import static org.sdfw.biometric.util.Constant.TAG;

public class CreateMemberWorker extends Worker {

    private ShaktiWebservice webservice;
    private Converter<ResponseBody, MessageResponse> converter;
    private MemberRepository memberRepository;

    public CreateMemberWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        setupComponents();
        createNotificationChannel();
    }

    private void setupComponents() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .client(client)
                .build();
        webservice = retrofit.create(ShaktiWebservice.class);
        converter = retrofit.responseBodyConverter(MessageResponse.class, new Annotation[0]);
        ShaktiDatabase database = Room.databaseBuilder(getApplicationContext(),
                ShaktiDatabase.class, "shakti.db")
                .build();
        memberRepository = new MemberRepository(database.memberDao());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Offline Sync";
            String description = "Sync created members with server when device is connected to internet";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_OFFLINE_SYNC, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        Member member = memberRepository.find(data.getString("branch_id"), data.getString("center_id"),
                data.getString("member_id"));
        Map<String, String> params = new HashMap<>();
        params.put("user_id", data.getString("user_id"));
        params.put("branch_id", member.getBranchId());
        params.put("center_id", member.getCenterId());
        params.put("member_id", member.getMemberId());
        params.put("group_id", member.getMemberId().substring(0, 1));
        params.put("member_name", member.getMemberName());
        params.put("savers_only_flag", member.getSaversOnlyFlag());
        params.put("father_name", member.getFatherName());
        params.put("mother_name", member.getMotherName());
        params.put("spouse_name", member.getSpouseName());
        params.put("sex", member.getSex());
        params.put("marital_status", member.getMaritalStatus());
        params.put("address", member.getAddress());
        params.put("successor", member.getSuccessor());
        params.put("dob", member.getDateOfBirth());
        params.put("voter_id", member.getVoterId());
        params.put("mobile", member.getMobileNo());
        params.put("template1", member.getTemplate1());
        params.put("template2", member.getTemplate2());
        params.put("template3", member.getTemplate3());
        params.put("template4", member.getTemplate4());
        params.put("member_image", member.getImage());
        params.put("varification_status", member.getVerificationStatus());
        params.put("create_uid", data.getString("create_uid"));
        params.put("access_token", data.getString("access_token"));
        Log.d(TAG, "doWork: " + params);

        Call<MessageResponse> call = webservice.createMemberSync(params);
        try {
            Response<MessageResponse> response = call.execute();
            StringBuilder builder = new StringBuilder()
                    .append("ব্রাঞ্চ: ")
                    .append(member.getBranchId())
                    .append(" কেন্দ্র: ")
                    .append(member.getCenterId())
                    .append(" সদস্য নং: ")
                    .append(member.getMemberId())
                    .append(" নাম: ")
                    .append(member.getMemberName())
                    .append("\n");
            if (response.isSuccessful()) {
                memberRepository.updateVerificationStatus(member.getBranchId(), member.getCenterId(),
                        member.getMemberId(), "V");
                showNotification(data.hashCode(), "কার্যক্রম সম্পন্ন",
                        builder.append(response.body().getMessage()).toString());
                return Worker.Result.success();
            } else {
                MessageResponse errorResponse = ErrorUtils.parseError(converter, response);
                if (response.code() == 409) {
                    memberRepository.updateVerificationStatus(member.getBranchId(), member.getCenterId(),
                            member.getMemberId(), errorResponse.getStatus());
                    showNotification(data.hashCode(), "সমস্যা শনাক্ত",
                            builder.append(errorResponse.getMessage()).toString());
                    return Worker.Result.success();
                }
                showNotification(data.hashCode(), "কার্যক্রমটি সফল হয়নি",
                        builder.append(errorResponse.getMessage()).toString());
                return Worker.Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (getRunAttemptCount() < 3) {
                return Worker.Result.retry();
            } else {
                return Worker.Result.failure();
            }
        }
    }

    private void showNotification(int id, String title, String message) {
        Log.d(TAG, "showNotification: " + message);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_OFFLINE_SYNC)
                .setSmallIcon(R.drawable.ic_shakti_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_shakti_notification_large))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, builder.build());
    }
}
