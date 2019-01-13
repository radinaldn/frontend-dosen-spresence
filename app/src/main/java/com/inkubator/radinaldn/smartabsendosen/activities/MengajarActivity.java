package com.inkubator.radinaldn.smartabsendosen.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.inkubator.radinaldn.smartabsendosen.adapters.MengajarViewPagerAdapter;
import com.inkubator.radinaldn.smartabsendosen.fragments.MengajarFragment;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import org.ankit.gpslibrary.MyTracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MengajarActivity extends AppCompatActivity {

    private static final long BATAS_MAKS_SESSION_LOCATION = 5; // in minutes
    ProgressDialog progressDialog;
    MyTracker tracker;
    Double myLat = 0.0, myLng = 0.0;
    private ProgressDialog pDialog;
    SessionManager sessionManager;

    /*
    beberapa method untuk dipanggil di adapter
     */
    public String getLatitude(){
        return String.valueOf(myLat);
    }

    public String getLongitude(){
        return String.valueOf(myLng);
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    // end of

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        tracker=new MyTracker(this);
        setContentView(R.layout.activity_mengajar);

        // jika memiliki last location
        if(sessionManager.hasLastLocation() && sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED)!=null){

            String sessDate = sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED);

            // get cur datetime
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String saatIni = mdformat.format(currentTime);

            long selisihMenit = hitungSelisihMenit(saatIni, sessDate);

            if (selisihMenit<BATAS_MAKS_SESSION_LOCATION){

                setGunakanLastLocation();
            }
        }




        Toolbar toolbar = findViewById(R.id.toolbar_mengajar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        ViewPager viewPager = findViewById(R.id.viewpager);


        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);


        tabLayout.setupWithViewPager(viewPager);

        // set hari yg terpilih

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println("hari ini hari index ke-"+day);

        // max index hari terakhir (jumat), jika fragment+=1 maka max+index_day += 1
        int max_index_day = 5;


        TabLayout.Tab tab = tabLayout.getTabAt(day-2);
        if (day-1 <= max_index_day && tab!=null){
            tab.select();
        }

    }

    public void showDialogKonfirmasiPindahKeDimanaSayaActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_my_location);
        builder.setMessage("Maaf, anda tidak memiliki data riwayat lokasi, silahkan cek lokasi melalui halaman \"Dimana Saya?\".");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MengajarActivity.this, DimanaSayaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // new GetMyLocation().execute();
                dialog.dismiss();

            }
        });

        builder.create().show();
    }

    private void setGunakanLastLocation() {
        String sessionLat = sessionManager.getMyLocationDetail().get(SessionManager.LATITUDE);
        String sessionLng = sessionManager.getMyLocationDetail().get(SessionManager.LONGITUDE);

        myLat = Double.parseDouble(sessionLat);
        myLng = Double.parseDouble(sessionLng);
    }

    private void setupViewPager(ViewPager viewPager) {

        MengajarViewPagerAdapter adapter = new MengajarViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MengajarFragment.newInstance("Monday"), "Senin");
        adapter.addFragment(MengajarFragment.newInstance("Tuesday"), "Selasa");
        adapter.addFragment(MengajarFragment.newInstance("Wednesday"), "Rabu");
        adapter.addFragment(MengajarFragment.newInstance("Thursday"), "Kamis");
        adapter.addFragment(MengajarFragment.newInstance("Friday"), "Jumat");
        viewPager.setAdapter(adapter);
    }

    void getLocation(){

        myLat = tracker.getLatitude();
        myLng = tracker.getLongitude();

        System.out.println(tracker.getLatitude());

        System.out.println(tracker.getLongitude());
        System.out.println(tracker.getLocation());

        String latlng = myLat+","+myLng;
        System.out.println("myLat : "+myLat);
        System.out.println("myLng : "+myLng);

        if (myLat!=0&&myLng!=0){
            Toast.makeText(getApplicationContext(), "Berhasil mendapatkan lokasi"+"\nProvider : "+tracker.getLocation().getProvider()+"\nLat : "+myLat+"\nLng : "+myLng, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Tidak bisa dapat lokasi", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Info");
            builder.setMessage("Suruh aplikasi membaca lokasi lagi?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new GetMyLocation().execute();
                }
            });

            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goToMainActivity();
                }
            });

            builder.create().show();
        }

    }

    private void goToMainActivity(){
        Intent intent = new Intent(MengajarActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    class GetMyLocation extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(MengajarActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengambil lokasi..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (tracker==null){
                tracker = new MyTracker(MengajarActivity.this);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            getLocation();
            // selesaikan activity

        }

    }

    private long hitungSelisihMenit(String curDate, String sesDate){
        String sessLastLocated = sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date firstDate, secondDate;

        long selisihMenit = 0;

        try {
            firstDate = mdformat.parse(sesDate);
            secondDate = mdformat.parse(curDate);

            selisihMenit = secondDate.getTime()-firstDate.getTime();

//                long diffSeconds = selisih / 1000 % 60;
//                long diffMinutes = selisih / (60 * 1000) % 60;
//                long diffHours = selisih / (60 * 60 * 1000) % 24;
//                long diffDays = selisih / (24 * 60 * 60 * 1000);

//                Toast.makeText(getApplicationContext(), diffDays+" hari, "+diffHours+" jam, "+diffMinutes+" menit, "+diffSeconds+" detik", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }


        selisihMenit = selisihMenit / (60 * 1000);

        return selisihMenit;
    }

    public void btMulaiClick(){
        // jika hasLastLocation

    }

}