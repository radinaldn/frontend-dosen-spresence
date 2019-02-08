package com.inkubator.radinaldn.smartabsendosen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.adapters.HistoriMengajarAdapter;
import com.inkubator.radinaldn.smartabsendosen.models.HistoriMengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseHistoriMengajar;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

public class HistoriMengajarActivity extends AppCompatActivity {

    private static final String TAG_ID_MENGAJAR = "id_mengajar";
    private static final String TAG = HistoriMengajarActivity.class.getSimpleName();
    private static String ID_MENGAJAR;

    SwipeRefreshLayout swipeRefreshLayout;
    ApiInterface apiService;

    private RecyclerView recyclerView;
    private HistoriMengajarAdapter adapter;
    private ArrayList<HistoriMengajar> historiMengajarArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_mengajar);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        ID_MENGAJAR = getIntent().getStringExtra(TAG_ID_MENGAJAR);

        getHistoriMengajar(ID_MENGAJAR);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipe_activity_histori_mengajar);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHistoriMengajar(ID_MENGAJAR);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_histori_mengajar);
        toolbar.setTitle(R.string.histori_mengajar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        // click button back pada title bar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(HistoriMengajarActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void getHistoriMengajar(String idMengajar) {
        apiService.presensiHistoriMengajarByIdMengajar(idMengajar).enqueue(new Callback<ResponseHistoriMengajar>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseHistoriMengajar> call, Response<ResponseHistoriMengajar> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: response : " + response);

                    if (response.body().getHistoriMengajar().size() > 0) {
                        historiMengajarArrayList = new ArrayList<>();
                        for (int i = 0; i < response.body().getHistoriMengajar().size(); i++) {
                            Log.i(TAG, "onResponse: Pertemuan " + response.body().getHistoriMengajar().get(i).getPertemuan());

                            String id_presensi = response.body().getHistoriMengajar().get(i).getIdPresensi();
                            String matakuliah = response.body().getHistoriMengajar().get(i).getNamaMatakuliah();
                            String pertemuan = response.body().getHistoriMengajar().get(i).getPertemuan();
                            String kelas = response.body().getHistoriMengajar().get(i).getNamaKelas();
                            String ruangan = response.body().getHistoriMengajar().get(i).getNamaRuangan();
                            String waktu = response.body().getHistoriMengajar().get(i).getWaktu();
                            String total_hadir = response.body().getHistoriMengajar().get(i).getTotalHadir();
                            String total_tidak_hadir = response.body().getHistoriMengajar().get(i).getTotalTidakHadir();

                            historiMengajarArrayList.add(new HistoriMengajar(id_presensi, matakuliah, pertemuan, kelas, ruangan, waktu, total_hadir, total_tidak_hadir));
                            adapter = new HistoriMengajarAdapter(historiMengajarArrayList, getApplicationContext());
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HistoriMengajarActivity.this);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);


                        }
                    } else {
                        KToast.warningToast(HistoriMengajarActivity.this, getString(R.string.histori_mengajar_kosong), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                    }
                } else {
//                    KToast.errorToast(HistoriMengajarActivity.this, "onResponse Error : "+response.errorBody(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                    KToast.errorToast(HistoriMengajarActivity.this, getString(R.string.terjadi_kesalahan), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseHistoriMengajar> call, Throwable t) {
//                KToast.errorToast(HistoriMengajarActivity.this, "onResponse Error : "+t.getLocalizedMessage(), Gravity.BOTTOM, KToast.LENGTH_SHORT);
                KToast.errorToast(HistoriMengajarActivity.this, getString(R.string.gagal_terhubung_ke_server), Gravity.BOTTOM, KToast.LENGTH_SHORT);
            }
        });


    }

}
