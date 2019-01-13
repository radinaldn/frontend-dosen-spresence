package com.inkubator.radinaldn.smartabsendosen.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import org.ankit.gpslibrary.MyTracker;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DimanaSayaActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final long BATAS_MAKS_SESSION_LOCATION = 5; // in minutes
    private GoogleMap mMap;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    MyTracker tracker;
    TextView tvLastLoc;
    Button btDimanaSaya, btSimpanLokasi;
    Marker myMarker;
    LatLng latLng;
    String saatIni;
    private ProgressDialog pDialog;
    SessionManager sessionManager;
    boolean canGetLocation = false;
    View parentLayout;
    boolean isGPSEnabled;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimana_saya);

        // init true time


        sessionManager = new SessionManager(this);

        btDimanaSaya = findViewById(R.id.btDimanaSaya);
        btSimpanLokasi = findViewById(R.id.btSimpanLokasi);
        tvLastLoc = findViewById(R.id.tvLastLoc);

        btSimpanLokasi.setOnClickListener(this);
        btDimanaSaya.setOnClickListener(this);

        parentLayout = findViewById(android.R.id.content);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);



        // Do Runtime Permission
        try {
            if(ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_PERMISSION);
            } else {
                // read location
                if (isMockSettingsON(DimanaSayaActivity.this)){
                    showSnacMockLoc();
                } else {
                    if (!isGPSEnabled){
                        showDialogTurnOnGPS();
                    } else {
                        new GetMyLocation().execute();
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        // print last loc into textview
        if (sessionManager.hasLastLocation()){
            showSessionLocation();
        }

        // Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar_dimana_saya);
        toolbar.setTitle(R.string.dimana_saya);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        // click button back pada title bar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    private void showSnacMockLoc() {
        Snackbar.make(parentLayout, "Mohon non-aktifkan Fitur Lokasi Tiruan", Snackbar.LENGTH_LONG).setAction("Buka Pengaturan", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            }
        }).show();
    }

    private void showSnackAutoDate(){
        Snackbar.make(parentLayout, "Mohon aktifkan Tanggal dan Waktu otomatis", Snackbar.LENGTH_LONG).setAction("Buka Pengaturan", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);

            }
        }).show();
    }

    private void showSessionLocation() {
//        tvLastLoc.setText("Lat : "+sessionManager.getMyLocationDetail().get(SessionManager.LATITUDE)+" Lng : "+sessionManager.getMyLocationDetail().get(SessionManager.LONGITUDE)+"\nUpdate : "+sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED));
        String sessDate = sessionManager.getMyLocationDetail().get(SessionManager.LAST_LOCATED);

        // get cur datetime
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String saatIni = mdformat.format(currentTime);

        long selisih = hitungSelisihMenit(saatIni, sessDate);

        if (selisih>5) {
            tvLastLoc.setText("Update : "+sessDate+" (Tidak Berlaku)\n*Riwayat lokasi hanya berlaku untuk "+BATAS_MAKS_SESSION_LOCATION+" menit.");
            tvLastLoc.setTextColor(getResources().getColor(R.color.RedBootstrap));
        } else {
            tvLastLoc.setText("Update : "+sessDate+" (Berlaku)\n*Riwayat lokasi hanya berlaku untuk "+BATAS_MAKS_SESSION_LOCATION+" menit.");
            tvLastLoc.setTextColor(getResources().getColor(R.color.GreenBootstrap)); }

    }

    private void goToMainActivity(){
        Intent intent = new Intent(DimanaSayaActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btDimanaSaya){
            // located me
            if (isMockSettingsON(this)){
                showSnacMockLoc();
            } else {
                new GetMyLocation().execute();
            }

        } else if(id==R.id.btSimpanLokasi) {
            // save my current position
            if (isMockSettingsON(this)){
                showSnacMockLoc();
            } else if (canGetLocation && !isMockSettingsON(this)){
                if(latLng!=null &&latLng.latitude!=0){
                    String strMyLat = String.valueOf(latLng.latitude);
                    String strMyLng = String.valueOf(latLng.longitude);

                    // check apakah waktu di set manual oleh user yang terlalu kreatif

                    // utk android API 17 keatas
                    if (isAutoDateTimeSettingsON(this)){
                        sessionManager.createMyLocationSession(strMyLat, strMyLng, saatIni);
                        showSessionLocation();
                    } else {
                        showSnackAutoDate();
                    }

                    Toast.makeText(getApplicationContext(), "Lokasi berhasil disimpan, silahkan klik tombol mulai.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MengajarActivity.class);
                    startActivity(i);


                }
            }

        }
    }

    void getLocation(){

        latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());

        System.out.println(tracker.getLatitude());

        System.out.println(tracker.getLongitude());
        System.out.println(tracker.getLocation());


        if (tracker.canGetLocation()&&tracker.getLatitude()!=0){
            canGetLocation = true;

            // change button simpan state
            btSimpanLokasi.setEnabled(true);
            btSimpanLokasi.setBackgroundColor(getResources().getColor(R.color.HoloOrangeDark));
            btSimpanLokasi.setText(getResources().getString(R.string.simpan_lokasi));

            //Toast.makeText(getApplicationContext(), "Bisa dapat lokasi", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Menggunakan provider : "+tracker.getLocation().getProvider(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Menggunakan provider : "+tracker.getLocation().getProvider()+"\nmyLat : "+latLng.latitude+"\nmyLng : "+latLng.longitude, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "Address : "+tracker.address+"\nCityname : "+tracker.cityName+"\nState : "+tracker.state+"\ncountryName : "+tracker.countryName+"\ncountryCode : "+tracker.countryCode+"\nipAddress : "+tracker.ipAddress+"\nmacAddress : "+tracker.macAddress, Toast.LENGTH_LONG).show();

            latLng = new LatLng(tracker.getLatitude(), tracker.getLongitude());

            if (isAutoDateTimeSettingsON(this)){
                moveMarker(latLng.latitude, latLng.longitude);
                Toast.makeText(getApplicationContext(), "Marker dipindahkan dengan : "+tracker.getLocation().getProvider()+"\nmyLat : "+tracker.getLatitude()+"\nmyLng : "+tracker.getLongitude(), Toast.LENGTH_LONG).show();
            } else {
                showSnackAutoDate();
            }


        } else {
            canGetLocation = false;

            // change button simpan state
            btSimpanLokasi.setEnabled(false);
            btSimpanLokasi.setBackgroundColor(getResources().getColor(R.color.buttonColorDisabled));
            btSimpanLokasi.setText(getResources().getString(R.string.membaca_lokasi));

            Toast.makeText(getApplicationContext(), "Tidak bisa dapat lokasi", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Info");
            builder.setMessage("Suruh aplikasi membaca lokasi lagi?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isMockSettingsON(DimanaSayaActivity.this)){
                        showSnacMockLoc();
                    } else {
                        new GetMyLocation().execute();
                    }
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

    private void moveMarker(double latitude, double longitude) {

        if (myMarker!=null) myMarker.remove();

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        saatIni = mdformat.format(currentTime);

        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);

        myMarker = mMap.addMarker(new
                MarkerOptions().position(new LatLng(latitude, longitude)).title("Posisi saya").snippet("Update : "+saatIni).icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_marker)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static boolean isAutoDateTimeSettingsON(Context context){
        // returns true if auto date time settings enabled, false if not enabled.
        if (Build.VERSION.SDK_INT > 16){
            if (android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0)==1){
                return true;
            } else {
                return false;
            }
        } else {
            if (android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0)==1){
                return true;
            } else {
                return false;
            }
        }
    }

    class GetMyLocation extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(DimanaSayaActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengambil lokasi..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (tracker==null){
                tracker = new MyTracker(DimanaSayaActivity.this);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            getLocation();
            //Toast.makeText(getApplicationContext(), "Selesai mendapatkan lokasi ", Toast.LENGTH_LONG).show();
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showDialogTurnOnGPS() {
        // check apakah GPS aktif?
            AlertDialog.Builder confirmBox = new AlertDialog.Builder(DimanaSayaActivity.this);
            confirmBox.setTitle(R.string.gps_is_not_activated);
            confirmBox.setIcon(R.drawable.ic_announcement_black_24dp);
            confirmBox.setMessage(R.string.do_you_wanna_turn_on_gps);
            confirmBox.setCancelable(false);

            confirmBox.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            });
            confirmBox.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            });

            AlertDialog alertDialogKonfirmasi = confirmBox.create();
            alertDialogKonfirmasi.show();

    }
}
