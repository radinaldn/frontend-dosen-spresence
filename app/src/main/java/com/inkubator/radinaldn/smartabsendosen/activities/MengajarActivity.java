package com.inkubator.radinaldn.smartabsendosen.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.inkubator.radinaldn.smartabsendosen.adapters.MengajarAdapter;
import com.inkubator.radinaldn.smartabsendosen.adapters.MengajarViewPagerAdapter;
import com.inkubator.radinaldn.smartabsendosen.fragments.MengajarFragment;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import java.util.List;


public class MengajarActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    LocationManager locationManager;
    LocationListener locationListener;
    Location lastLocation;
    String latittude, longitude, altitude, bestProvider;
    Criteria criteria = new Criteria();

    /*
    beberapa method untuk dipanggil di adapter
     */
    public String getLatitude(){
        return latittude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getAltitude(){
        return altitude;
    }

    public Location getLastLocation(){
        return lastLocation;
    }

    // end of

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mengajar);

        // for location service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        // start dialog
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang membaca lokasi ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();



        List<String> locationProviders = locationManager.getAllProviders();
        for (String provider : locationProviders){
            Log.d("LocationProviders", provider);
        }

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        // Aktifkan untuk mode debugging
        //Toast.makeText(getApplicationContext(), "Mulai menunggu 10 detik ke depan", Toast.LENGTH_SHORT).show();

        Toast.makeText(getApplicationContext(), "Sedang mendapatkan lokasi ...", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 10s

                // Aktifkan untuk mode debugging
                //Toast.makeText(getApplicationContext(), "10 detik berakhir", Toast.LENGTH_SHORT).show();

                //jika sudah menunggu dan tidak null
                if (latittude!=null){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), "Lokasi baru berhasil didapatkan, anda boleh menekan tombol mulai", Toast.LENGTH_SHORT).show();

                } else if (latittude==null){
                    // cek apakah lokasi terakhir yg didapatkan != null
                    if (lastLocation != null){
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "Lokasi terakhir berhasil didapatkan, nyalakan kamera", Toast.LENGTH_SHORT).show();

                        System.out.println("Latitude dari lastLocation : "+lastLocation.getLatitude());
                        System.out.println("Longitude dari lastLocation : "+lastLocation.getLongitude());
                    } else {
                        Toast.makeText(getApplicationContext(), "Lokasi tidak berhasil didapatkan, coba lagi", Toast.LENGTH_LONG).show();
                        //   onBackPressed();
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                }

            }
        }, 10000);

        Toolbar toolbar = findViewById(R.id.toolbar_mengajar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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

    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {

                latittude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                altitude = String.valueOf(location.getAltitude());

                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String strStatus = "";
            switch (status){
                case LocationProvider.AVAILABLE:
                    strStatus = "tersedia";
                    case LocationProvider.OUT_OF_SERVICE:
                        strStatus = "sedang dalam perbaikan";
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            strStatus = "tidak tersedia untuk sementara";
            }

            // Aktifkan untuk mode debugging
            // Toast.makeText(getBaseContext(), provider + " " +strStatus, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Provider: " +provider+ " di-aktifkan", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(), "Provider: " +provider+ " di-nonaktifkan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        bestProvider = locationManager.getBestProvider(criteria, true);
        Log.d("LocationProviders", "Best provider is : "+bestProvider);

        lastLocation = locationManager.getLastKnownLocation(bestProvider);
        if (lastLocation != null) Log.d("LocationProviders", lastLocation.toString());

        locationManager.requestLocationUpdates(
                // aktifkan ambil dari satelit
                //LocationManager.GPS_PROVIDER,
                // aktifkan ambil dari BTS
                //LocationManager.NETWORK_PROVIDER,

                bestProvider,
                0,
                0,
                locationListener
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}