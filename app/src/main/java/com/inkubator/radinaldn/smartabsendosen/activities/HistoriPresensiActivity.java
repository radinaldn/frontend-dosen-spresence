package com.inkubator.radinaldn.smartabsendosen.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.adapters.HistoriPresensiViewPagerAdapter;
import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsendosen.fragments.HistoriPresensiFragment;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensiDetail;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseStatusPresensi;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 17/07/18.
 */

public class HistoriPresensiActivity extends AppCompatActivity {

    public String ID_PRESENSI;
    public String STATUS_PRESENSI;
    Button bt_konfirmasi, bt_qrcode;
    private LinearLayout layout_btqrcode_btkonfirmasi;
    Dialog  myDialog;
    Button bt_close;
    ImageView iv_qrcode;
    String QRCODE_FORMAT = ".png";

    ViewPager viewPager;
    TabLayout tabLayout;

    private static final String TAG_ID_PRESENSI = "id_presensi";
    private static final String TAG_STATUS_PRESENSI = "status_presensi";
    private static final String CLOSE = "close";

    private static final String TAG = HistoriPresensiActivity.class.getSimpleName();
    ApiInterface apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_histori_presensi);
        super.onCreate(savedInstanceState);

        //get Extra from previous Activity
        ID_PRESENSI = getIntent().getStringExtra(TAG_ID_PRESENSI);
        STATUS_PRESENSI = getIntent().getStringExtra(TAG_STATUS_PRESENSI);

        bt_qrcode = findViewById(R.id.bt_qrcode);
        bt_konfirmasi = findViewById(R.id.bt_konfirmasi);
        layout_btqrcode_btkonfirmasi = findViewById(R.id.layout_btqrcode_btkonfirmasi);

        // jika presensi sudah ditutup, hide button konfrimasi dan button qr_code

        apiService = ApiClient.getClient().create(ApiInterface.class);

        // munculkan/hilangkan action presensi
        apiService.isCloseByIdPresensi(ID_PRESENSI).enqueue(new Callback<ResponseStatusPresensi>() {
            @Override
            public void onResponse(Call<ResponseStatusPresensi> call, Response<ResponseStatusPresensi> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().equalsIgnoreCase("200")){
                        String status = response.body().getData();
                        System.out.println("status = "+status);
                        if (status.equalsIgnoreCase("open")){
                            layout_btqrcode_btkonfirmasi.setVisibility(View.VISIBLE);
                            System.out.println("layout ditampilkan");
                        } else {
                            layout_btqrcode_btkonfirmasi.setVisibility(View.GONE);
                            System.out.println("layout dihilangkan");
                        }
                    } else {
                        Log.e(TAG, "onResponse is not success: "+response.body().toString());
                    }
                } else {
                    Log.e(TAG, "onResponse: Error "+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseStatusPresensi> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure : "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_histori_presensi);
        setSupportActionBar(toolbar);

        // action click tombol back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bt_konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: bt_konfirmasi diklik");
                AlertDialog.Builder confirmBox = new AlertDialog.Builder(HistoriPresensiActivity.this);
                confirmBox.setTitle("Konfirmasi");
                confirmBox.setIcon(R.drawable.ic_live_help_black_24dp);
                confirmBox.setMessage("Anda yakin ingin mengkonfirmasi presensi "+ID_PRESENSI+" ?");
                confirmBox.setCancelable(false);

                confirmBox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // lakukan konfirmasi presensi here
                        konfirmasiPresensi(ID_PRESENSI);
                    }
                });
                confirmBox.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Anda menekan tidak" ,Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialogKonfirmasi = confirmBox.create();
                alertDialogKonfirmasi.show();

            }
        });

        bt_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog = new Dialog(HistoriPresensiActivity.this);
                myDialog.setContentView(R.layout.dialog_popup_image);
                myDialog.setTitle("QR CODE");
                bt_close = myDialog.findViewById(R.id.bt_close);
                iv_qrcode = myDialog.findViewById(R.id.iv_qrcode);
                Picasso.with(getApplicationContext()).load(ServerConfig.QRCODE_PATH+ID_PRESENSI+QRCODE_FORMAT).resize(1000, 1000).into(iv_qrcode);

                bt_close.setEnabled(true);

                bt_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.cancel();
                    }
                });

                myDialog.show();

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewpager_histori_presensi);

        tabLayout = findViewById(R.id.tabs_histori_presensi);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }



    private void konfirmasiPresensi(String id_presensi) {
        System.out.println("masuk ke konfirmasiPresensi( "+id_presensi+")");
        apiService.presensiDetailKonfirmasiAll(id_presensi).enqueue(new Callback<ResponsePresensiDetail>() {
            @Override
            public void onResponse(retrofit2.Call<ResponsePresensiDetail> call, Response<ResponsePresensiDetail> response) {
                Log.i(TAG, "onResponse: response = "+response);
                Log.i(TAG, "onResponse: response.body() = "+response.body());
                if(response.isSuccessful()){
                    Log.i(TAG, "onResponse: response.body() = "+response.body());
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "Error : "+response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponsePresensiDetail> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure : "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        HistoriPresensiViewPagerAdapter adapter = new HistoriPresensiViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HistoriPresensiFragment.newInstance(ID_PRESENSI, "Hadir"), "Hadir");
        adapter.addFragment(HistoriPresensiFragment.newInstance(ID_PRESENSI, "Tidak Hadir"), "Tidak Hadir");
        viewPager.setAdapter(adapter);
    }
}
