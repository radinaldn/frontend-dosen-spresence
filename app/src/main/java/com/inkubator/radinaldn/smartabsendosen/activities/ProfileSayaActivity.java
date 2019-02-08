package com.inkubator.radinaldn.smartabsendosen.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsendosen.models.Dosen;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseUpdatePassword;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSayaActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ImageView iv_foto;
    public static final String TAG_NIP = "nip";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";
    public static final String TAG_JK = "jk";
    private String nip, imei, nama, foto, jk;
    private TextView tv_nama, tv_nip, tv_imei, tv_jk;
    AlertDialog.Builder alertDialogBuilder;
    LayoutInflater inflater;
    View dialogView;
    ApiInterface apiService;
    private Button btChangeLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_saya);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        sessionManager = new SessionManager(this);

        nip = sessionManager.getDosenDetail().get(TAG_NIP);
        imei = sessionManager.getDosenDetail().get(TAG_IMEI);
        nama = sessionManager.getDosenDetail().get(TAG_NAMA);
        foto = sessionManager.getDosenDetail().get(TAG_FOTO);
        jk = sessionManager.getDosenDetail().get(TAG_JK);

        iv_foto = findViewById(R.id.iv_foto);
        tv_nama = findViewById(R.id.tv_nama);
        tv_nip = findViewById(R.id.tv_nip);
        tv_imei = findViewById(R.id.tv_imei);
        tv_jk = findViewById(R.id.tv_jk);
        btChangeLanguage = findViewById(R.id.btChangeLanguage);

        btChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(mIntent);
            }
        });

        Picasso.with(getApplicationContext()).load(ServerConfig.IMAGE_PATH + "/dosen/" + foto).into(iv_foto);
        tv_nip.setText(nip);
        tv_nama.setText(nama);
        tv_imei.setText(imei);
        tv_jk.setText(jk);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);

        collapsingToolbar.setTitle(nama);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ProfileSayaActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profil_saya, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.ubah_password) {
            actionUbahPassword();
        }

        return super.onOptionsItemSelected(item);
    }

    private void actionUbahPassword() {
        alertDialogBuilder = new AlertDialog.Builder(ProfileSayaActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_ubah_password, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(false);
        //alertDialogBuilder.setIcon(R.drawable.logo_performance);
        alertDialogBuilder.setTitle(R.string.ubah_password);

        final TextInputEditText et_old_password = dialogView.findViewById(R.id.etpassword_old);
        final TextInputEditText et_new_password = dialogView.findViewById(R.id.etpassword_new);
        final TextInputEditText et_new_password2 = dialogView.findViewById(R.id.etpassword_new2);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String old_password = et_old_password.getText().toString();
                        String new_password = et_new_password.getText().toString();
                        String new_password2 = et_new_password2.getText().toString();

                        if (new_password.equals(new_password2)) {
                            apiService.dosenUpdatePassword(sessionManager.getDosenDetail().get(SessionManager.NIP), old_password, new_password).enqueue(new Callback<ResponseUpdatePassword>() {
                                @Override
                                public void onResponse(Call<ResponseUpdatePassword> call, Response<ResponseUpdatePassword> response) {
                                    if (response.isSuccessful()) {

                                        if (response.body().getCode().equalsIgnoreCase("200")) {
                                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                            goToMainActivity();
                                        } else {
                                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseUpdatePassword> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), R.string.gagal_terhubung_ke_server, Toast.LENGTH_LONG).show();
                                    t.getLocalizedMessage();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.password_baru_yang_anda_masukkan_tidak_cocok, Toast.LENGTH_LONG).show();
                        }
                    }
                })

                .setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
