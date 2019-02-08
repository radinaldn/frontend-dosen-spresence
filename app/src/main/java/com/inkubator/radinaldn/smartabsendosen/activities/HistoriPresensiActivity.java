package com.inkubator.radinaldn.smartabsendosen.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.inkubator.radinaldn.smartabsendosen.models.Presensi;
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
    public String STATUS_PRESENSI, MATAKULIAH, KELAS;
    Button bt_konfirmasi, bt_qrcode;
    private LinearLayout layout_btqrcode_btkonfirmasi;
    Dialog myDialog;
    Button bt_close;
    ImageView iv_qrcode;
    String QRCODE_FORMAT = ".png";

    ViewPager viewPager;
    TabLayout tabLayout;

    private static final String TAG_ID_PRESENSI = "id_presensi";
    private static final String TAG_STATUS_PRESENSI = "status_presensi";
    private static String STATUS = null;
    private static final String TAG_MATAKULIAH = "matakuliah";
    private static final String TAG_KELAS = "kelas";

    private static final String TAG = HistoriPresensiActivity.class.getSimpleName();
    ApiInterface apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_histori_presensi);
        super.onCreate(savedInstanceState);

        //get Extra from previous Activity
        ID_PRESENSI = getIntent().getStringExtra(TAG_ID_PRESENSI);
        STATUS_PRESENSI = getIntent().getStringExtra(TAG_STATUS_PRESENSI);
        MATAKULIAH = getIntent().getStringExtra(TAG_MATAKULIAH);
        KELAS = getIntent().getStringExtra(TAG_KELAS);

        bt_qrcode = findViewById(R.id.bt_qrcode);
        bt_konfirmasi = findViewById(R.id.bt_konfirmasi);
        layout_btqrcode_btkonfirmasi = findViewById(R.id.layout_btqrcode_btkonfirmasi);

        // jika presensi sudah ditutup, hide button konfrimasi dan button qr_code

        apiService = ApiClient.getClient().create(ApiInterface.class);

        // munculkan/hilangkan action presensi
        apiService.isCloseByIdPresensi(ID_PRESENSI).enqueue(new Callback<ResponseStatusPresensi>() {
            @Override
            public void onResponse(Call<ResponseStatusPresensi> call, Response<ResponseStatusPresensi> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("200")) {
                        String status = response.body().getData();
                        STATUS = status;
                        System.out.println("status = " + status);
                        if (status.equalsIgnoreCase(Presensi.OPEN)) {
                            layout_btqrcode_btkonfirmasi.setVisibility(View.VISIBLE);
                            System.out.println("layout ditampilkan");
                        } else {
                            layout_btqrcode_btkonfirmasi.setVisibility(View.GONE);
                            System.out.println("layout dihilangkan");
                        }
                    } else {
                        Log.e(TAG, "onResponse is not success: " + response.body().toString());
                    }
                } else {
                    Log.e(TAG, "onResponse: Error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseStatusPresensi> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
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
                confirmBox.setTitle(R.string.konfirmasi);
                confirmBox.setIcon(R.drawable.ic_live_help_black_24dp);
                confirmBox.setMessage(getString(R.string.anda_yakin_ingin_mengkonfirmasi_sekaligus_menutup_presensi) +" "+ MATAKULIAH + " (" + KELAS + ") ?");
                confirmBox.setCancelable(false);

                confirmBox.setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // lakukan konfirmasi presensi here
                        bt_konfirmasi.setEnabled(false);
                        bt_konfirmasi.setText(R.string.memuat);
                        bt_konfirmasi.setTextColor(getResources().getColor(R.color.textColorSecondary));
                        bt_konfirmasi.setTextColor(getResources().getColor(R.color.textColorPrimaryDisabled));
                        konfirmasiPresensi(ID_PRESENSI);
                    }
                });
                confirmBox.setNegativeButton(getString(R.string.tidak), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
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
                myDialog.setTitle(getString(R.string.qr_code));
                bt_close = myDialog.findViewById(R.id.bt_close);
                iv_qrcode = myDialog.findViewById(R.id.iv_qrcode);
                Picasso.with(getApplicationContext()).load(ServerConfig.QRCODE_PATH + ID_PRESENSI + QRCODE_FORMAT).resize(1000, 1000).into(iv_qrcode);

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

        setupViewPager(viewPager, 0);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void goToMainActivity() {
        Intent intent = new Intent(HistoriPresensiActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void refreshFragment(long scrollPosition, int tabPosition) {
//        Toast.makeText(getApplicationContext(), "Calling refreshFragment() from fragment", Toast.LENGTH_SHORT).show();
        setupViewPager(viewPager, scrollPosition);
        TabLayout.Tab tab = tabLayout.getTabAt(tabPosition);
        if (tab != null)
            tab.select();

    }


    private void konfirmasiPresensi(String id_presensi) {
        System.out.println("masuk ke konfirmasiPresensi( " + id_presensi + ")");
        apiService.presensiDetailKonfirmasiAll(id_presensi).enqueue(new Callback<ResponseStatusPresensi>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseStatusPresensi> call, Response<ResponseStatusPresensi> response) {
                Log.i(TAG, "onResponse: response.body() = " + response.toString());
                if (response.isSuccessful()) {
                    //PERBAIKI HANDLING MESSAGE DISINI
                    Log.i(TAG, "onResponse: response.body() = " + response.body().toString());
                    if (response.body().getStatus().equalsIgnoreCase("200")) {
                        Toast.makeText(getApplicationContext(), "Konfirmasi berhasil dan presensi ditutup", Toast.LENGTH_LONG).show();
                    }

                    refreshFragment(0, 0);
                    layout_btqrcode_btkonfirmasi.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseStatusPresensi> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager, long scrollPosition) {
        viewPager.setAdapter(null);
        HistoriPresensiViewPagerAdapter adapter = new HistoriPresensiViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HistoriPresensiFragment.newInstance(ID_PRESENSI, "Hadir", scrollPosition), getString(R.string.hadir));
        adapter.addFragment(HistoriPresensiFragment.newInstance(ID_PRESENSI, "Tidak Hadir", scrollPosition), getString(R.string.tidak_hadir));
        viewPager.setAdapter(adapter);
    }

    // getSTATUS (close/open)
    public static String getSTATUS() {
        return STATUS;
    }
}
