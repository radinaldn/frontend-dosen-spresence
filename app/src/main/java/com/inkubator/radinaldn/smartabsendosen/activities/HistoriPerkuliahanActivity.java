package com.inkubator.radinaldn.smartabsendosen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;
import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.adapters.HistoriMengajarAdapter;
import com.inkubator.radinaldn.smartabsendosen.models.HistoriMengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseHistoriMengajar;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoriPerkuliahanActivity extends AppCompatActivity {

    private static final String TAG = HistoriPerkuliahanActivity.class.getSimpleName();
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HistoriMengajarAdapter adapter;
    DatePickerTimeline timeline;
    LayoutInflater inflater;
    View dialogView;
    private ArrayList<HistoriMengajar> historiPerkuliahanList = new ArrayList<>();
    private static String NIP;
    private static final String TAG_NIP = "nip";
    SessionManager sessionManager;


    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori_perkuliahan);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_historii_perkuliahan);
        toolbar.setTitle(R.string.histori_perkuliahan);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        // click button back pada title bar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        timeline = findViewById(R.id.dpt_timeline);
        timeline.setDateLabelAdapter(new MonthView.DateLabelAdapter() {
            @Override
            public CharSequence getLabel(Calendar calendar, int index) {
                return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.YEAR) % 2000);
            }
        });


        // get current date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String[] cur_dates = (formatter.format(date)).split("-");
        int cur_year = Integer.parseInt(cur_dates[0]);
        int cur_month = Integer.parseInt(cur_dates[1]);
        int cur_day = Integer.parseInt(cur_dates[2]);
        System.out.println(cur_year + ", " + cur_month + ", " + cur_day);

        // init widget datetimeline
        timeline.setFirstVisibleDate(cur_year, Calendar.JANUARY, 1);
        timeline.setSelectedDate(cur_year, (cur_month - 1), cur_day);
        timeline.setLastVisibleDate(cur_year, Calendar.DECEMBER, 1);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipe_activity_agenda);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // handling refresh recyclerview
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        String tahun = String.valueOf(cur_year);
        String bulan = ((cur_month < 10 ? "0" : "") + cur_month);
        String tanggal = (cur_day < 10 ? "0" : "") + cur_day;
        final String nip = sessionManager.getDosenDetail().get(TAG_NIP);

        refreshUI(nip, tahun + "-" + bulan + "-" + tanggal);

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                //Toast.makeText(getApplicationContext(), "Sekarang tahun "+year+", bulan "+(month+1)+", tanggal "+day, Toast.LENGTH_LONG).show();
                historiPerkuliahanList.clear();
                int month2 = month + 1;
                String tahun = String.valueOf(year);
                String bulan = ((month2 < 10 ? "0" : "") + month2);
                String tanggal = (day < 10 ? "0" : "") + day;
                String date = tahun + "-" + bulan + "-" + tanggal;

                refreshUI(nip, date);
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(HistoriPerkuliahanActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void refreshUI(String nip, final String date) {
        historiPerkuliahanList.clear();

        apiService.presensiFindByNipAndDate(nip, date).enqueue(new Callback<ResponseHistoriMengajar>() {
            @Override
            public void onResponse(Call<ResponseHistoriMengajar> call, Response<ResponseHistoriMengajar> response) {
                System.out.println(response.toString());
                if (response.isSuccessful()) {


                    historiPerkuliahanList.addAll(response.body().getHistoriMengajar());

                    adapter = new HistoriMengajarAdapter(historiPerkuliahanList, HistoriPerkuliahanActivity.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HistoriPerkuliahanActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    if (response.body().getHistoriMengajar().size() < 1) {
                        Toast.makeText(getApplicationContext(), getString(R.string.tidak_ada_perkuliahan_tanggal) + date, Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseHistoriMengajar> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
            }
        });


    }
}
