package com.inkubator.radinaldn.smartabsendosen.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.activities.MainActivity;
import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsendosen.models.KehadiranDosen;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseReverseGeocoding;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseUpdateLocation;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.rests.MyGoogleApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.MyGoogleApiInterface;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 09/08/18.
 */

public class ShareLocService extends Service {

    ApiInterface apiService;
    MyGoogleApiInterface googleApiService;

    SessionManager sessionManager;
    LocationManager locationManager;
    LocationListener locationListener;
    Criteria criteria = new Criteria();
    String latittude, longitude, altitude, bestProvider;
    String nip, status_kehadiran, nama_kota;

    double lowY = 0.466900;

    double topY = 0.468851;
    double lowX = 101.354667;
    double topX = 101.356450;

    private static final String TAG_NIP = "nip";
    private static final String TAG = ShareLocService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        apiService = ApiClient.getClient().create(ApiInterface.class);
        googleApiService = MyGoogleApiClient.getGoogleApiClient().create(MyGoogleApiInterface.class);

        sessionManager = new SessionManager(this);

        nip = sessionManager.getDosenDetail().get(TAG_NIP);

        // for location service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        bestProvider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(bestProvider,
                0,
                100,
                locationListener);



        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Called each time when 1000 milliseconds (1 second) (the period parameter)
                 System.out.println("10 detik berlalu");


                 if (latittude!= null) {
                     Log.i(TAG, "run: cur_loc : "+latittude+" "+longitude+" "+altitude);

                     // check apakah saya berada di dalam segmen ?
                     boolean imInSegment = isInSegment(Double.parseDouble(latittude), Double.parseDouble(longitude));
                     System.out.println("boolean imInsegment ("+latittude+", "+longitude+") : "+imInSegment);

                     if (imInSegment){
                         status_kehadiran = KehadiranDosen.HADIR;
                         nama_kota = "Fakultas Sains dan Teknologi";
                     } else {
                         status_kehadiran = KehadiranDosen.TIDAK_HADIR;


                         String latlng = latittude+","+longitude;
                         // dapatkan data kota mmenggunakan Google Reverse Geocoding
                         nama_kota = getKotaByGeocodeJson(latlng, ServerConfig.GOOGLE_API_KEY);
                     }

                     updateMyLocation(nip, status_kehadiran, nama_kota);
                     Log.d(TAG, "run: updateMyLocation("+nip+", "+status_kehadiran+", "+nama_kota+");");
                 } else {
                     Log.e(TAG, "run: lat, lng, alt is null");
                 }

            }
            },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                10*1000);


        return START_STICKY;

    }

    private String getKotaByGeocodeJson(String latlng, String GOOGLE_API_KEY){
        googleApiService.geocodeJson(latlng, ServerConfig.GOOGLE_API_KEY).enqueue(new Callback<ResponseReverseGeocoding>() {
            @Override
            public void onResponse(Call<ResponseReverseGeocoding> call, Response<ResponseReverseGeocoding> response) {
                System.out.println("response : "+response);
                System.out.println("response : "+response.body().toString());
                if (response.isSuccessful()){
                    if (response.body().getStatus().equalsIgnoreCase("OK")){

                                         /*
                                         ini belum akurat, karna terkadang keldesa, kec dan kabkota bergeser satu indeks lebih besar
                                          */
                        String jalan = response.body().getResults().get(0).getAddressComponents().get(0).getLongName();
                        String keldesa = response.body().getResults().get(0).getAddressComponents().get(1).getLongName();
                        String kec = response.body().getResults().get(0).getAddressComponents().get(2).getLongName();
                        String kabkota = response.body().getResults().get(0).getAddressComponents().get(3).getLongName();
                        String provinsi = response.body().getResults().get(0).getAddressComponents().get(4).getLongName();
                        String negara = response.body().getResults().get(0).getAddressComponents().get(5).getLongName();

                        Log.i(TAG, "onResponse geocodeJson : jalan "+jalan+", keldesa "+keldesa+", kec "+kec+", kabkota "+kabkota+", provinsi "+provinsi+", negara "+negara);

                        // jika masih di riau
                        if (provinsi.equalsIgnoreCase("Riau")){
                            nama_kota = kec+", "+kabkota+".";
                        } else {
                            nama_kota = kabkota+", "+provinsi+", "+negara+".";
                        }

                    } else {
                        Log.e(TAG, "onResponse (not OK):\n status : "+response.body().getStatus());
                        if (response.body().getError_message()!=null) Log.e(TAG, "onResponse errorMessage : " +response.body().getError_message());
                    }
                } else {
                    Log.e(TAG, "onResponse (not succes) : "+response.errorBody() );
                }
            }

            @Override
            public void onFailure(Call<ResponseReverseGeocoding> call, Throwable t) {
                t.getLocalizedMessage();
            }
        });

        return nama_kota;
    }

    private boolean isInSegment(double lat, double lng){

        if ((lat >= lowY && lat <= topY) && (lng >= lowX && lng <= topX)) {
            return true;
        } else {
            return false;
        }
    }

    private void updateMyLocation(String nip, String status_kehadiran, String nama_kota) {
        apiService.dosenUpdateLocation(nip, status_kehadiran, nama_kota).enqueue(new Callback<ResponseUpdateLocation>() {
            @Override
            public void onResponse(Call<ResponseUpdateLocation> call, Response<ResponseUpdateLocation> response) {
                if (response.isSuccessful()){
                    if (response.body().getCode().equalsIgnoreCase("200")){
                        Log.i(TAG, "onResponse: "+response.body().getMessage());
                    } else {
                        Log.e(TAG, "onResponse Error: "+response.errorBody().toString());
                    }
                } else {
                    Log.e(TAG, "onResponse Error: "+response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateLocation> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager==null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        if (locationListener==null){
            locationListener = new MyLocationListener();
        }

        locationManager.removeUpdates(locationListener);
        System.out.println("Service telah di Destroy");
        MainActivity.setSwBagikanLokasi(false, ShareLocService.this);
        this.stopSelf();
    }

    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {

                latittude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                altitude = String.valueOf(location.getAltitude());

                System.out.println("DARI ON lOCATOIN chANGE : "+latittude+" "+longitude+" "+altitude);
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

            Toast.makeText(getBaseContext(), provider + " " +strStatus, Toast.LENGTH_SHORT).show();
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
}
