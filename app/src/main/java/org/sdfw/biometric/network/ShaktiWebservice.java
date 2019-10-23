package org.sdfw.biometric.network;

import org.sdfw.biometric.network.response.FetchCentersResponse;
import org.sdfw.biometric.network.response.FetchMemberMatrixResponse;
import org.sdfw.biometric.network.response.FetchUserRoleResponse;
import org.sdfw.biometric.network.response.FingerprintAuthResponse;
import org.sdfw.biometric.network.response.GetEarlySettlementListResponse;
import org.sdfw.biometric.network.response.GetLoanDisbursementListResponse;
import org.sdfw.biometric.network.response.GetSavingWithdrawnListResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.SigninResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ShaktiWebservice {

    @FormUrlEncoded
    @POST("user/fingerprint-auth")
    Observable<FingerprintAuthResponse> fingerprintAuth(
            @Field("user_id") String ein,
            @Field("finger_print") String template
    );

    @FormUrlEncoded
    @POST("user/signin")
    Observable<SigninResponse> signin(@Field("user_id") String ein, @Field("password") String password);

    @FormUrlEncoded
    @POST("signout")
    Observable<MessageResponse> signout(@Field("user_id") String ein, @Field("access_token") String token);

    @FormUrlEncoded
    @POST("user/fingerprint-signup")
    Observable<MessageResponse> registerFingerprints(
            @Field("user_id") String ein,
            @Field("template1") String template1,
            @Field("template2") String template2,
            @Field("template3") String template3,
            @Field("template4") String template4,
            @Field("device_id") String deviceId,
            @Field("device_token") String deviceToken,
            @Field("access_token") String token
    );

    @GET("fetch_user_role")
    Observable<FetchUserRoleResponse> fetchUserRole(
            @Field("user_id") String ein,
            @Field("access_token") String token
    );

    @GET("center")
    Observable<FetchCentersResponse> fetchCenters(
            @Query("user_id") String ein,
            @Query("branch_id") String branch_id,
            @Query("access_token") String token
    );

    @GET("member-matrix")
    Observable<FetchMemberMatrixResponse> fetchMemberMatrix(
            @Query("user_id") String ein,
            @Query("branch_id") String branch_id,
            @Query("center_id") String center_id,
            @Query("access_token") String token
    );

    @FormUrlEncoded
    @POST("member")
    Observable<MessageResponse> createMember(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("member")
    Call<MessageResponse> createMemberSync(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("member/fingerprint-verification")
    Observable<MessageResponse> verifyFingerprint(
            @Field("user_id") String userId,
            @Field("branch_id") String branchId,
            @Field("center_id") String centerId,
            @Field("member_id") String memberId,
            @Field("finger_print") String fingerPrint,
            @Field("access_token") String token
    );

    @GET("loan-disbursement")
    Observable<GetLoanDisbursementListResponse> getLoanDisbursementList(
            @Query("user_id") String user_id,
            @Query("branch_id") String branch_id,
            @Query("access_token") String token
    );

    @FormUrlEncoded
    @POST("loan-disbursement")
    Observable<MessageResponse> postLoanDisbursement(@FieldMap Map<String, String> params);

    @GET("saving-withdrawn")
    Observable<GetSavingWithdrawnListResponse> getSavingWithdrawnList(
            @Query("user_id") String user_id,
            @Query("branch_id") String branch_id,
            @Query("access_token") String token
    );

    @FormUrlEncoded
    @POST("saving-withdrawn")
    Observable<MessageResponse> postSavingWithdrawn(@FieldMap Map<String, String> params);

    @GET("early-settlement")
    Observable<GetEarlySettlementListResponse> getEarlySettlementList(
            @Query("user_id") String user_id,
            @Query("branch_id") String branch_id,
            @Query("access_token") String token
    );

    @FormUrlEncoded
    @POST("early-settlement")
    Observable<MessageResponse> postEarlySettlement(@FieldMap Map<String, String> params);
}
