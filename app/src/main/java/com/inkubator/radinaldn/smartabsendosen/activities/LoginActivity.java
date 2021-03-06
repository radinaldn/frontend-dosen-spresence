package com.inkubator.radinaldn.smartabsendosen.activities;

/**
 * Created by radinaldn on 17/03/18.
 */

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.models.Dosen;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseLogin;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.utils.AbsRuntimePermission;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;
import com.onurkaganaldemir.ktoastlib.KToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AbsRuntimePermission {

    @BindView(R.id.etnip)
    EditText etnip;

    @BindView(R.id.etpassword)
    EditText etpassword;

    @BindView(R.id.btlogin)
    Button btlogin;

    SessionManager sessionManager;
    ApiInterface apiService;

    String nip, password, imei;
    public final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // do runtime permission
        //request permission here
        requestAppPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.RECORD_AUDIO},
                R.string.msg, REQUEST_PERMISSION);

        //init
        ButterKnife.bind(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);

//        if(sessionManager.isLoggedIn()){
//            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//            // agar tidak balik ke activity ini lagi
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
//            startActivity(i);
//            finish();
//        }

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    @Override
    public void onPermissionGranted(int requestcode) {
        Toast.makeText(getApplicationContext(), R.string.izin_diberikan, Toast.LENGTH_LONG).show();
    }

    private void loginUser() {
        nip = etnip.getText().toString();
        password = etpassword.getText().toString();
        imei = "356876057383575";

        Log.d(TAG, "loginUser: " + nip + " " + password + " " + imei);

        apiService.login(nip, password, imei).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Dapat terhubung ke server");
                    Log.d(TAG, "onResponse: " + response.body().getStatus());

                    Dosen dosen = response.body().getData();

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        sessionManager.createLoginSession(dosen.getNip(),
                                dosen.getPassword(),
                                dosen.getImei(),
                                dosen.getNama(),
                                dosen.getJk(),
                                dosen.getFoto());

                        Log.d(TAG, "onResponse: Dapat data dosen");

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        KToast.successToast(LoginActivity.this, getString(R.string.berhasil_login), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.gagal_login) + response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }
}
