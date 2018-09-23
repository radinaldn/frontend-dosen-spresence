package com.inkubator.radinaldn.smartabsendosen.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.inkubator.radinaldn.smartabsendosen.adapters.MengajarAdapter;
import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.models.Mengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseMengajar;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.services.ShareLocService;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    TextView tvname, tvjurusan, tv_libur;;
    private ProgressDialog pDialog;
    private SwitchCompat swbagikanlokasi;

    private static final String TAG = MengajarActivity.class.getSimpleName();
    private static String NIP;
    public static final String TAG_NIP = "nip";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";

    SessionManager sessionManager;
    static SessionManager staticSessionManager;

    boolean isGPSEnabled;
    LocationManager manager;

    // function for set the state of switch (on/off)
    public static void setSwBagikanLokasi(boolean status, Context context){
        if (staticSessionManager==null){
            staticSessionManager = new SessionManager(context);
        }
        staticSessionManager.setStatusSwitchShareLoc(false);
    }

    SwipeRefreshLayout swipeRefreshLayout;
    ApiInterface apiService;

    private RecyclerView recyclerView;
    private MengajarAdapter adapter;
    private ArrayList<Mengajar> mengajarArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

        sessionManager = new SessionManager(this);

        if(!sessionManager.isLoggedIn()){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            // agar tidak balik ke activity ini lagi
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();
        }

        apiService = ApiClient.getClient().create(ApiInterface.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fab action click handling

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // switch share loc
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_share_loc);
        View actionView = MenuItemCompat.getActionView(menuItem);

        swbagikanlokasi = (SwitchCompat) actionView.findViewById(R.id.drawer_switch);
        swbagikanlokasi.setChecked(sessionManager.getStatusSwitchShareLoc());
        swbagikanlokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, (swbagikanlokasi.isChecked()) ? "is checked!!!" : "not checked!!!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                if (swbagikanlokasi.isChecked()){
                    Snackbar.make(v, "Fitur bagikan info kehadiran aktif", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    sessionManager.setStatusSwitchShareLoc(true);

                    // Tampilkan Simple Notif
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
                    mBuilder.setSmallIcon(R.drawable.help);
                    mBuilder.setContentTitle("Notification alert, Click me!");
                    mBuilder.setContentText("Hi, This is Android notification detail!");
                    mBuilder.setAutoCancel(false);

                    Intent notificationIntent = new Intent(MainActivity.this, KehadiranDosenActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(contentIntent);

                    // Add as notification
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(0, mBuilder.build());

                    // jalankan service background
                    startService(new Intent(MainActivity.this, ShareLocService.class));

                } else {
                    Snackbar.make(v, "Fitur bagikan infor kehadiran non-aktif", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    sessionManager.setStatusSwitchShareLoc(false);

                    // stop service
                    stopService(new Intent(MainActivity.this, ShareLocService.class));
                }
            }
        });

        Log.i(TAG, "onCreate: "+sessionManager.getDosenDetail().get(TAG_NAMA));

        View header = navigationView.getHeaderView(0);

        imageView = header.findViewById(R.id.imageView);

        tvname = header.findViewById(R.id.tvName);
        tvjurusan = header.findViewById(R.id.tvJurusan);

        Picasso.with(getApplicationContext()).load(ServerConfig.IMAGE_PATH+"/dosen/"+sessionManager.getDosenDetail().get(TAG_FOTO)).resize(100, 100).into(imageView);
        tvname.setText(sessionManager.getDosenDetail().get(TAG_NAMA));
        tvjurusan.setText("(" +sessionManager.getDosenDetail().get(TAG_NIP)+ ")");

        tv_libur = findViewById(R.id.tv_libur);
        /**
        Isi data Kuliah hari ini
         */

        recyclerView = findViewById(R.id.recyclerView);

        NIP = sessionManager.getDosenDetail().get(TAG_NIP);

        getMengajarHariIni(NIP);

    }

    private void getMengajarHariIni(String nip) {
        System.out.println("Ambil data mengajar hari ini");
        apiService.mengajarFindAllTodayByNip(nip).enqueue(new Callback<ResponseMengajar>() {
            @Override
            public void onResponse(Call<ResponseMengajar> call, Response<ResponseMengajar> response) {
                System.out.println(response.toString());
                if(response.isSuccessful()){
                    System.out.println("Ada data : "+response.body().getMengajar().size());
                    if(response.body().getMengajar().size()>0){
                        //hilangkan pesan libur
                        tv_libur.setVisibility(View.GONE);
                        mengajarArrayList = new ArrayList<>();
                        mengajarArrayList.addAll(response.body().getMengajar());

                        adapter = new MengajarAdapter(mengajarArrayList, getApplicationContext());
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        tv_libur.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMengajar> call, Throwable t) {
                t.getLocalizedMessage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {
            // Handling for logout action
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            // redirect to login page
            sessionManager.logoutDosen();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.absen) {
            // Handle the camera action
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.jadwal_mengajar) {
            // check apakah GPS aktif?
            isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGPSEnabled) {

                android.app.AlertDialog.Builder confirmBox = new android.app.AlertDialog.Builder(MainActivity.this);
                confirmBox.setTitle("GPS belum diaktifkan");
                confirmBox.setIcon(R.drawable.ic_announcement_black_24dp);
                confirmBox.setMessage("Apakah anda ingin mengaktifkan GPS ?\nMengaktifkan GPS akan membantu aplikasi memperoleh lokasi yang akurat.");
                confirmBox.setCancelable(false);

                confirmBox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Aktifkan GPS terlebih dahulu dan coba lagi", Toast.LENGTH_SHORT).show();
                    }
                });
                confirmBox.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do scan qr code here
                        // sebelum masuk ke ScanningQRCodeActivity.class lakukan pengecekkan apakah user menghidupkan lokasi palsu?
                        Intent i = new Intent(getApplicationContext(), MengajarActivity.class);
                        startActivity(i);
                    }
                });

                android.app.AlertDialog alertDialogKonfirmasi = confirmBox.create();
                alertDialogKonfirmasi.show();

            } else {
                Intent i = new Intent(getApplicationContext(), MengajarActivity.class);
                startActivity(i);
            }

        } else if (id == R.id.kehadiran_dosen) {
            Intent intent = new Intent(MainActivity.this, KehadiranDosenActivity.class);
            startActivity(intent);

        } else if(id == R.id.nav_share_loc) {
            swbagikanlokasi.setChecked(!swbagikanlokasi.isChecked());
            Snackbar.make(item.getActionView(), (swbagikanlokasi.isChecked()) ? "is checked!!!" : "not checked!!!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        } else if (id == R.id.nav_pengaturan) {
            Intent intent = new Intent(MainActivity.this, ProfileSayaActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    MainActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(R.string.license_title);

            // Setting Dialog Message
            alertDialog.setMessage("Smart Absen v1.0\nCopyright ©INKUBATOR 2018");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.attend_logo);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
