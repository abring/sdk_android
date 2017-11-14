package ir.abring.abringlibrary.services;

import android.content.Context;
import android.util.Log;

import java.io.File;

import ir.abring.abringlibrary.Abring;
import ir.abring.abringlibrary.R;
import ir.abring.abringlibrary.interfaces.AbringCallBack;
import ir.abring.abringlibrary.models.abringregister.AbringRegister;
import ir.abring.abringlibrary.network.AbringAppAPI;
import ir.abring.abringlibrary.network.AbringAppService;
import ir.abring.abringlibrary.network.AbringRetrofitErrorResponce;
import ir.abring.abringlibrary.utils.Check;
import ir.abring.abringlibrary.utils.NetworkUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbringUserServices {

    public static void register(String username,
                                String password,
                                String name,
                                File avatar,
                                String email,
                                String phone,
                                String reg_idgcm,
                                final AbringCallBack<Object, Object> abringCallBack) {

        if (NetworkUtil.isNetworkConnected(Abring.getContext())) {
            AbringAppAPI mApiService = AbringAppService.getInstance().getService(AbringAppAPI.class);

            // Parsing any Media type file
            MultipartBody.Part avatarToUpload = null;
            if(avatar != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), avatar);
                avatarToUpload = MultipartBody.Part.createFormData("avatar", avatar.getName(), requestBody);
            }

            RequestBody usernameRequest = Check.isEmpty(username) ?
                    null : RequestBody.create(MediaType.parse("text/plain"), username);
            RequestBody passwordRequest = Check.isEmpty(password) ?
                    null : RequestBody.create(MediaType.parse("text/plain"), password);
            RequestBody nameRequest = Check.isEmpty(name) ?
                    null : RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody emailRequest = Check.isEmpty(email) ?
                    null : RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody phoneRequest = Check.isEmpty(phone) ?
                    null : RequestBody.create(MediaType.parse("text/plain"), phone);
            RequestBody gcmRequest = Check.isEmpty(reg_idgcm) ?
                    null : RequestBody.create(MediaType.parse("text/plain"), reg_idgcm);
            RequestBody appIdRequest = Check.isEmpty(Abring.getPackageName()) ?
                    null : RequestBody.create(MediaType.parse("text/plain"), Abring.getPackageName());

            Call<AbringRegister> mEntityCall = mApiService.RegisterAPI(
                    usernameRequest,
                    passwordRequest,
                    nameRequest,
                    avatarToUpload,
                    emailRequest,
                    phoneRequest,
                    gcmRequest,
                    appIdRequest);

            Log.i("RetrofitURL", "Request URL: " + mEntityCall.request().url().toString());

            mEntityCall.enqueue(new Callback<AbringRegister>() {
                @Override
                public void onResponse(Call<AbringRegister> call, final Response<AbringRegister> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        abringCallBack.onSuccessful(response.body());
                    } else {
                        handleError(response, Abring.getContext(), abringCallBack);
                    }
                }

                @Override
                public void onFailure(Call<AbringRegister> call, Throwable t) {
                    handleError(t, Abring.getContext(), abringCallBack);
                }
            });

        } else {
            abringCallBack.onFailure(Abring.getContext().getString(R.string.no_connect_to_internet));
        }
    }

    private static void handleError(Object response, Context context, AbringCallBack<Object, Object> abringCallBack) {
        Object msg = new AbringRetrofitErrorResponce().getMessage(response, context);
        abringCallBack.onFailure(msg);
    }

}