package com.inkubator.radinaldn.smartabsendosen.activities;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.adapters.MengajarAdapter;
import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsendosen.models.KehadiranDosen;
import com.inkubator.radinaldn.smartabsendosen.models.Mengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseKehadiranDosen;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseMengajar;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.services.ShareLocService;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {

    ImageView imageView;
    TextView tvname, tvjurusan, tv_libur;
    ;
    private ProgressDialog pDialog;
    private SwitchCompat swbagikanlokasi;
    NotificationManager nManager;

    public static final String TAG = MainActivity.class.getSimpleName();
    private static String NIP;
    public static final String TAG_NIP = "nip";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";

    private static final String TAG_Dimana = "Dimana";
    private static final String TAG_dimana = "dimana";
    private static final String TAG_Di_mana = "Di mana";
    private static final String TAG_di_mana = "di mana";
    private static final String TAG_Pak = "Pak";
    private static final String TAG_pak = "pak";
    private static final String TAG_Bu = "Bu";
    private static final String TAG_bu = "bu";

    SessionManager sessionManager;
    static SessionManager staticSessionManager;


    // function for set the state of switch (on/off)
    public static void setSwBagikanLokasi(boolean status, Context context) {
        if (staticSessionManager == null) {
            staticSessionManager = new SessionManager(context);
        }
        staticSessionManager.setStatusSwitchShareLoc(false);
    }

    SwipeRefreshLayout swipeRefreshLayout;
    ApiInterface apiService;

    private RecyclerView recyclerView;
    private MengajarAdapter adapter;
    private ArrayList<Mengajar> mengajarArrayList;

    private TextToSpeech tts;
    String RESULT_STT; // for SpeechToText result
    String RESULT_TTS; // for TextToSPeech result


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        Log.d(TAG, "sesionManager.isLoggedIn(): " + sessionManager.isLoggedIn());

        if (!sessionManager.isLoggedIn()) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            // agar tidak balik ke activity ini lagi
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_main);

        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);


        apiService = ApiClient.getClient().create(ApiInterface.class);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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
                if (swbagikanlokasi.isChecked()) {
                    jalankanServiceShareLoc(v);

                } else {
                    stopServiceShareLoc(v);
                }
            }
        });

        Log.i(TAG, "onCreate: " + sessionManager.getDosenDetail().get(TAG_NAMA));

        View header = navigationView.getHeaderView(0);

        imageView = header.findViewById(R.id.imageView);

        tvname = header.findViewById(R.id.tvName);
        tvjurusan = header.findViewById(R.id.tvJurusan);

        Picasso.with(getApplicationContext())
                .load(ServerConfig.IMAGE_PATH + "/dosen/" + sessionManager.getDosenDetail().get(TAG_FOTO))
                .resize(100, 100)
                .placeholder(R.drawable.dummy_ava)
                .error(R.drawable.dummy_ava)
                .centerCrop()
                .into(imageView);

        tvname.setText(sessionManager.getDosenDetail().get(TAG_NAMA));
        tvjurusan.setText("(" + sessionManager.getDosenDetail().get(TAG_NIP) + ")");

        tv_libur = findViewById(R.id.tv_libur);
        /**
         Isi data Kuliah hari ini
         */

        recyclerView = findViewById(R.id.recyclerView);

        NIP = sessionManager.getDosenDetail().get(TAG_NIP);

        getMengajarHariIni(NIP);

        tts = new TextToSpeech(this, this);

        // init SpeechRecognizer
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for (int i = 0; i < matches.size(); i++) {
                    Log.i(TAG, "onResults speechRecog (: " + i + 1 + ")" + matches.get(i));
                }

                // displaying the first match
                if (matches != null) {

                    Toast.makeText(getApplicationContext(), matches.get(0), Toast.LENGTH_LONG).show();
                    RESULT_STT = matches.get(0);

                    /**
                     * Rules menerima inputan suara
                     */

                    //split result
                    String[] hasils = RESULT_STT.split(" ");

                    // jika hasils[0] == di || hasil[0] == dimana
                    if (hasils[0].equalsIgnoreCase("di") || RESULT_STT.equalsIgnoreCase(TAG_dimana)) {

                        String nama_dosen = null;

                        if (hasils[0].equalsIgnoreCase("di")) {
                            if (hasils[2].equals("pak")) {
                                String str_new = RESULT_STT.replaceFirst("di mana pak ", "");
                                nama_dosen = str_new;

                            }
                            if (hasils[2].equals("Pak")) {
                                String str_new = RESULT_STT.replaceFirst("di mana Pak ", "");
                                nama_dosen = str_new;

                            }
                            if (hasils[2].equalsIgnoreCase("bu")) {
                                String str_new = RESULT_STT.replaceFirst("di mana bu ", "");
                                nama_dosen = str_new;
                            }
                            if (hasils[2].equalsIgnoreCase("Bu")) {
                                String str_new = RESULT_STT.replaceFirst("di mana Bu ", "");
                                nama_dosen = str_new;
                            }

                        } else if (hasils[0].equalsIgnoreCase("dimana")) {
                            if (hasils[1].equalsIgnoreCase("pak")) {
                                String str_new = RESULT_STT.replaceFirst("dimana pak", "");
                                nama_dosen = str_new;

                            }
                            if (hasils[1].equalsIgnoreCase("Pak")) {
                                String str_new = RESULT_STT.replaceFirst("dimana Pak", "");
                                nama_dosen = str_new;

                            }
                            if (hasils[1].equalsIgnoreCase("bu")) {
                                String str_new = RESULT_STT.replaceFirst("dimana bu", "");
                                nama_dosen = str_new;
                            }
                            if (hasils[1].equalsIgnoreCase("Bu")) {
                                String str_new = RESULT_STT.replaceFirst("dimana Bu", "");
                                nama_dosen = str_new;
                            }
                        }

                        if (nama_dosen != null) {
                            //Jika berhasil dan sesuai, cari data kehadiran dosen
                            final String finalNama_dosen = nama_dosen;
                            apiService.dosenKehadiranFindByName(nama_dosen).enqueue(new Callback<ResponseKehadiranDosen>() {
                                @Override
                                public void onResponse(Call<ResponseKehadiranDosen> call, Response<ResponseKehadiranDosen> response) {
                                    if (response.isSuccessful()) {

                                        System.out.println(response.toString());
                                        System.out.println(response.body().toString());
                                        if (response.body().getKehadiranDosen().size() == 1) {

                                            List<KehadiranDosen> hasil_req = response.body().getKehadiranDosen();
                                            String nama_dosen = hasil_req.get(0).getNama_dosen();
                                            String status_kehadiran = hasil_req.get(0).getStatus_kehadiran();
                                            String nama_kota = hasil_req.get(0).getNama_kota();
                                            String last_update = hasil_req.get(0).getLast_update();

                                            RESULT_TTS = finalNama_dosen + " yang ditemukan " + nama_dosen + " dengan status " + status_kehadiran + ", berada di " + nama_kota + " terakhir update " + last_update;
                                            speakOut();
                                        } else if (response.body().getKehadiranDosen().size() > 1) {
                                            Toast.makeText(getApplicationContext(), "Data " + finalNama_dosen + " ada " + response.body().getKehadiranDosen().size(), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Tidak ada  Data " + finalNama_dosen, Toast.LENGTH_LONG).show();
                                        }


                                    } else {
                                        Toast.makeText(getApplicationContext(), "Tidak dapat memuat data " + finalNama_dosen, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseKehadiranDosen> call, Throwable t) {
                                    t.getLocalizedMessage();
                                }
                            });
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), RESULT_STT, Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), matches.get(0), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //  onPressed
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorSecondary)));
                        Toast.makeText(getApplicationContext(), R.string.silahkan_berbicara, Toast.LENGTH_SHORT).show();
                        return true;

                    case MotionEvent.ACTION_UP:
                        //onReleased
                        mSpeechRecognizer.stopListening();
                        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        return true;
                }

                return false;
            }
        });

    }

    private void stopServiceShareLoc(View v) {
        Snackbar.make(v, "Fitur bagikan info kehadiran non-aktif", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        sessionManager.setStatusSwitchShareLoc(false);

        nManager.cancel(0);

        // beri notif untuk end task app
        // Tampilkan Simple Notif
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setSmallIcon(R.drawable.attend_logo);
        mBuilder.setContentTitle(getResources().getString(R.string.app_name));
        mBuilder.setContentText("Fitur masih berjalan, end task app agar berhenti!");
//        mBuilder.setOngoing(true);

        Intent notificationIntent = new Intent(MainActivity.this, KehadiranDosenActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        nManager.notify(1, mBuilder.build());
        // end of notif end task app

        // stop service
        stopService(new Intent(MainActivity.this, ShareLocService.class));
    }

    private void jalankanServiceShareLoc(View v) {
        Snackbar.make(v, "Fitur bagikan info kehadiran aktif", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        sessionManager.setStatusSwitchShareLoc(true);

        // Tampilkan Simple Notif
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setSmallIcon(R.drawable.attend_logo);
        mBuilder.setContentTitle(getResources().getString(R.string.app_name));
        mBuilder.setContentText("Share location sedang berjalan!");
        mBuilder.setOngoing(true);

        Intent notificationIntent = new Intent(MainActivity.this, KehadiranDosenActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // Add as notification
        nManager.notify(0, mBuilder.build());

        System.out.println("mau menjalanka service...");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Service dijalankan...");
//                startService(new Intent(MainActivity.this, ShareLocService.class));
//
//            }
//        }).start();
        System.out.println("Service dijalankan...");
        startService(new Intent(MainActivity.this, ShareLocService.class));

        // jalankan service background
    }


    private void getMengajarHariIni(String nip) {
        System.out.println("Ambil data mengajar hari ini");
        apiService.mengajarFindAllTodayByNip(nip).enqueue(new Callback<ResponseMengajar>() {
            @Override
            public void onResponse(Call<ResponseMengajar> call, Response<ResponseMengajar> response) {
                System.out.println(response.toString());
                if (response.isSuccessful()) {
                    System.out.println("Ada data : " + response.body().getMengajar().size());
                    if (response.body().getMengajar().size() > 0) {
                        //hilangkan pesan libur
                        tv_libur.setVisibility(View.GONE);
                        mengajarArrayList = new ArrayList<>();
                        mengajarArrayList.addAll(response.body().getMengajar());

                        adapter = new MengajarAdapter(mengajarArrayList, MainActivity.this, TAG);
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

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/15nIADlcuGslPHrr2"));
            startActivity(browserIntent);

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

        if (id == R.id.nav_help) {

        } else if (id == R.id.jadwal_mengajar) {

            //showDialogTurnOnGPS();
            Intent i = new Intent(getApplicationContext(), MengajarActivity.class);
            startActivity(i);


        } else if (id == R.id.histori_saya) {
            Intent intent = new Intent(MainActivity.this, HistoriPerkuliahanActivity.class);
            startActivity(intent);
        } else if (id == R.id.kehadiran_dosen) {
            Intent intent = new Intent(MainActivity.this, KehadiranDosenActivity.class);
            startActivity(intent);

        } else if (id == R.id.dimana_saya) {
            Intent intent = new Intent(MainActivity.this, DimanaSayaActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share_loc) {
            swbagikanlokasi.setChecked(!swbagikanlokasi.isChecked());
            //Snackbar.make(item.getActionView(), (swbagikanlokasi.isChecked()) ? "is checked!!!" : "not checked!!!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            if (swbagikanlokasi.isChecked()) {
                jalankanServiceShareLoc(item.getActionView());

            } else {
                stopServiceShareLoc(item.getActionView());
            }

        } else if (id == R.id.nav_pengaturan) {
            Intent intent = new Intent(MainActivity.this, ProfileSayaActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    MainActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(R.string.license_title);

            // Setting Dialog Message
            alertDialog.setMessage(getResources().getString(R.string.license_app));

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.attend_logo);
            // Setting OK Button


            // Showing Alert Message
            alertDialog.show();
        } else if (id == R.id.nav_lapor_bug) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/radinaldn/"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("id", "ID"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS: This language is not supported");
            } else {
                // if no error
//                speakOut();
            }
        } else {
            Log.e(TAG, "Initialization failed");
        }
    }

    private void speakOut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(RESULT_TTS, TextToSpeech.QUEUE_FLUSH, null, "id1");
        } else {
            Toast.makeText(getApplicationContext(), RESULT_TTS, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        //shutdown tts
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        // kill notifCompat id=1
        nManager.cancel(1);
        super.onDestroy();
    }
}
