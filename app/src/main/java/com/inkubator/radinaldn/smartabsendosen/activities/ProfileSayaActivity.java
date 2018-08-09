package com.inkubator.radinaldn.smartabsendosen.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;
import com.squareup.picasso.Picasso;

public class ProfileSayaActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ImageView iv_foto;
    public static final String TAG_NIP = "nip";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";
    public static final String TAG_JK = "jk";
    private String nip, imei, nama, foto, jk;
    private TextView tv_nama, tv_nip, tv_imei, tv_jk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_saya);

        sessionManager = new SessionManager(this);

        nip = sessionManager.getDosenDetail().get(TAG_NIP);
        imei = sessionManager.getDosenDetail().get(TAG_IMEI);
        nama = sessionManager.getDosenDetail().get(TAG_NAMA);
        foto = sessionManager.getDosenDetail().get(TAG_FOTO);
        jk = sessionManager.getDosenDetail().get(TAG_JK);

        iv_foto = findViewById(R.id.iv_foto);
        tv_nama = findViewById(R.id.tv_nama);
        tv_nip = findViewById(R.id.tv_nip);
        tv_imei = findViewById(R.id.tv_imei);
        tv_jk = findViewById(R.id.tv_jk);

        Picasso.with(getApplicationContext()).load(ServerConfig.IMAGE_PATH+"/dosen/"+foto).into(iv_foto);
        tv_nip.setText(nip);
        tv_nama.setText(nama);
        tv_imei.setText(imei);
        tv_jk.setText(jk);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);

        collapsingToolbar.setTitle(nama);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
