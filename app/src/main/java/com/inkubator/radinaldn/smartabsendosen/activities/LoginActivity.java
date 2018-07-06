package com.inkubator.radinaldn.smartabsendosen.activities;

/**
 * Created by radinaldn on 17/03/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.models.Dosen;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseLogin;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init
        ButterKnife.bind(this);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        nip = etnip.getText().toString();
        password = etpassword.getText().toString();
        imei = "356876057383575";

        Log.d(TAG, "loginUser: " + nip +" "+password+" "+imei);

        apiService.login(nip, password, imei).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "onResponse: Dapat terhubung ke server");
                    Log.d(TAG, "onResponse: " +response.body().getStatus());

                    List<Dosen> dosen = response.body().getData();

                    if(response.body().getStatus().equalsIgnoreCase("success")){
                        sessionManager.createLoginSession(dosen.get(0).getNip(),
                                dosen.get(0).getPassword(),
                                dosen.get(0).getImei(),
                                dosen.get(0).getNama(),
                                dosen.get(0).getJk(),
                                dosen.get(0).getFoto());

                        Log.d(TAG, "onResponse: Dapat data dosen");

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Toast.makeText(LoginActivity.this, "Berhasil login", Toast.LENGTH_LONG).show();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Gagal login dosen :"+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Gagal login dosen :"+response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Gagal konek ke server", Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: "+ t.getLocalizedMessage());
            }
        });
    }
}
