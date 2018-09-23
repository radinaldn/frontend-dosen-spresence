package com.inkubator.radinaldn.smartabsendosen.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.activities.HistoriMengajarActivity;
import com.inkubator.radinaldn.smartabsendosen.activities.HistoriPresensiActivity;
import com.inkubator.radinaldn.smartabsendosen.activities.MengajarActivity;
import com.inkubator.radinaldn.smartabsendosen.models.Mengajar;
import com.inkubator.radinaldn.smartabsendosen.models.Ruangan;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensi;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseRuangan;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 09/07/18.
 */

public class MengajarAdapter extends RecyclerView.Adapter<MengajarAdapter.MengajarViewHolder> {

    private static final String TAG_NIP = "nip";
    private Context mContext;

    private ArrayList<Mengajar> dataList;
    private static final String TAG = MengajarAdapter.class.getSimpleName();

    SessionManager sessionManager;
    ApiInterface apiService;

    public MengajarAdapter(ArrayList<Mengajar> dataList, Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    private static final String TAG_ID_MENGAJAR = "id_mengajar";

    @NonNull
    @Override
    public MengajarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.mengajar_item, parent, false);

        apiService = ApiClient.getClient().create(ApiInterface.class);


        sessionManager = new SessionManager(mContext);

        return new MengajarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MengajarViewHolder holder, int position) {
        holder.tv_idmengajar.setText(dataList.get(position).getIdMengajar());
        holder.tv_matakuliah.setText(dataList.get(position).getNamaMatakuliah());
        holder.tv_kelas.setText("Kelas "+dataList.get(position).getNamaKelas());
        holder.tv_sks.setText(dataList.get(position).getSks()+" SKS");
        holder.tv_waktu.setText("Pukul "+dataList.get(position).getWaktuMulai());


    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class MengajarViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_idmengajar, tv_matakuliah, tv_kelas, tv_sks, tv_waktu;
        private Button bt_mulai, bt_histori;

        private static final String TAG_ID_PRESENSI = "id_presensi";
        private static final String TAG_STATUS_PRESENSI = "status_presensi";

        public String ID_PRESENSI;
        public final String STATUS_PRESENSI = "open";

        public static final String TAG_OK = "OK";

        AlertDialog alertDialog1;
        final Context context = this.itemView.getContext();

        final List<Ruangan> ruangan = new ArrayList<>();
        final List<String> namaRuangan = new ArrayList<>();
        ArrayAdapter arrayAdapter;

        public MengajarViewHolder(final View itemView) {
            super(itemView);
            tv_idmengajar = itemView.findViewById(R.id.tv_idmengajar);
            tv_matakuliah = itemView.findViewById(R.id.tv_matakuliah);
            tv_kelas = itemView.findViewById(R.id.tv_kelas);
            tv_sks= itemView.findViewById(R.id.tv_sks);
            tv_waktu= itemView.findViewById(R.id.tv_waktu);
            bt_mulai = itemView.findViewById(R.id.bt_mulai);
            bt_histori = itemView.findViewById(R.id.bt_histori);


            // ketika matakuliah diklik untuk memulai perkuliahan
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Anda menekan item "+tv_idmengajar.getText().toString(), Toast.LENGTH_SHORT).show();

                }
            });

            // cek apakah Latitude, Longitude dan Altitude == null
            if(mContext instanceof MengajarActivity) {
                String current_lat = ((MengajarActivity) mContext).getLatitude();
                String current_lng = ((MengajarActivity) mContext).getLongitude();
                String current_alt = ((MengajarActivity) mContext).getAltitude();
                Location last_location = ((MengajarActivity) mContext).getLastLocation();

                if(current_lat==null){ // jika null

                    if (last_location!=null){
                        bt_mulai.setBackgroundColor(itemView.getResources().getColor(R.color.colorPrimary));
                        bt_mulai.setTextColor(itemView.getResources().getColor(R.color.textColorWhite));
                        // bt_mulai di klik
                        bt_mulai.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(itemView.getContext(), "Silahkan pilih ruangan perkuliahan", Toast.LENGTH_SHORT).show();
                                String id_mengajar = tv_idmengajar.getText().toString();
                                String matakuliah = tv_matakuliah.getText().toString();
                                String kelas = tv_kelas.getText().toString();

                                showPopUpRuangan(id_mengajar, matakuliah, kelas);
                            }
                        });
                    } else {
                        bt_mulai.setEnabled(!bt_mulai.isEnabled());
                        bt_mulai.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(itemView.getContext(), "Aplikasi masih membaca lokasi anda, pastikan GPS sudah diaktifkan.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                } else { // jika tidak
                    bt_mulai.setBackgroundColor(itemView.getResources().getColor(R.color.colorPrimary));
                    bt_mulai.setTextColor(itemView.getResources().getColor(R.color.textColorWhite));
                    // bt_mulai di klik
                    bt_mulai.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(itemView.getContext(), "Silahkan pilih ruangan perkuliahan", Toast.LENGTH_SHORT).show();
                            String id_mengajar = tv_idmengajar.getText().toString();
                            String matakuliah = tv_matakuliah.getText().toString();
                            String kelas = tv_kelas.getText().toString();

                            showPopUpRuangan(id_mengajar, matakuliah, kelas);
                        }
                    });
                }

                System.out.println("current_lat : " + current_lat);
                System.out.println("current_lng: " + current_lng);
                System.out.println("current_alt: " + current_alt);
            }

            // bt_histori di klik
            bt_histori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // arahkan ke Activity history presensi
                    Intent i = new Intent(itemView.getContext(), HistoriMengajarActivity.class);
                    i.putExtra(TAG_ID_MENGAJAR, tv_idmengajar.getText());
                    Toast.makeText(itemView.getContext(), "ID_MENGAJAR : "+tv_idmengajar.getText(), Toast.LENGTH_SHORT).show();
                    itemView.getContext().startActivity(i);
                }
            });

            // get data ruangan
            apiService.ruanganFindAll().enqueue(new Callback<ResponseRuangan>() {
                @Override
                public void onResponse(Call<ResponseRuangan> call, Response<ResponseRuangan> response) {
                    if(response.isSuccessful()){
                        if (response.body().getRuangan().size()>0){
                            for (int i = 0; i < response.body().getRuangan().size(); i++) {
                                ruangan.add(new Ruangan(response.body().getRuangan().get(i).getIdRuangan(), response.body().getRuangan().get(i).getNama())); // mengisi object Ruangan
                                namaRuangan.add(response.body().getRuangan().get(i).getNama()); // mengisi List namaRuangan (tanpa id)
                                Log.i(TAG, "onResponse info: response "+i+" ruanganFindAll = "+response.body().getRuangan().get(i).getNama());
                            }

                            arrayAdapter = new ArrayAdapter<String>(
                                    itemView.getContext(),
                                    android.R.layout.simple_list_item_single_choice,
                                    namaRuangan
                            );
                        }

                    } else {
                        Log.e(TAG, "onResponse error: "+response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ResponseRuangan> call, Throwable t) {
                    t.printStackTrace();
                }
            });



        }

        private void showPopUpRuangan(final String id_mengajar, final String matakuliah, final String kelas){

            final AlertDialog.Builder builder = new AlertDialog.Builder(this.itemView.getContext());
            final AlertDialog.Builder confirmBox = new AlertDialog.Builder(this.itemView.getContext());
            builder.setTitle("Pilih ruangan");
            builder.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, final int item) {
                    final String selectedIdRuangan = ruangan.get(item).getIdRuangan();
                    final String selectedNamaRuangan = ruangan.get(item).getNama();

                    // show confirmation box
                    confirmBox.setTitle("Konfirmasi");
                    confirmBox.setIcon(R.drawable.ic_live_help_black_24dp);
                    confirmBox.setMessage("Anda yakin ingin memulai perkuliahan "+matakuliah+" kelas "+kelas+" di ruangan "+selectedNamaRuangan+" ?");
                    confirmBox.setCancelable(false);
                    confirmBox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(mContext instanceof MengajarActivity){
                                String current_lat = ((MengajarActivity) mContext).getLatitude();
                                String current_lng = ((MengajarActivity) mContext).getLongitude();
                                String current_alt = ((MengajarActivity) mContext).getAltitude();

                                System.out.println("current_lat : "+current_lat);

                                Toast.makeText(itemView.getContext(), "Latitude dari MengajarActivity: "+current_lat+
                                                "\nLongitude dari MengajarActivity: "+current_lng+
                                                "\nAltitude dari MengajarActivity: "+current_alt
                                        , Toast.LENGTH_LONG).show();

                                if (current_lat == null) {
                                    Toast.makeText(itemView.getContext(), "Aplikasi sedang membaca lokasi anda, scroll layar ke bawah dan coba lagi.", Toast.LENGTH_LONG).show();
                                } else {
                                    presensiAdd(id_mengajar, selectedIdRuangan, current_lat, current_lng, sessionManager.getDosenDetail().get(TAG_NIP), selectedNamaRuangan);
                                }



                            }


                        }
                    });
                    confirmBox.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "anda menekan Tidak", Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog alertDialogKonfirmasi = confirmBox.create();
                    alertDialogKonfirmasi.show();

                    alertDialog1.dismiss();
                }
            });
            alertDialog1 = builder.create();
            alertDialog1.show();
        }

        public void presensiAdd(String id_mengajar, String id_ruangan, String lat, String lng, String nip, String nama_ruangan){
            apiService.presensiAdd(id_mengajar, id_ruangan, lat, lng, nip, nama_ruangan).enqueue(new Callback<ResponsePresensi>() {
                @Override
                public void onResponse(Call<ResponsePresensi> call, Response<ResponsePresensi> response) {
                    Log.d(TAG, "onResponse: "+response);
                    Log.d(TAG, "onResponse presensiAdd response status : "+response.body().getStatus());
                    Log.d(TAG, "onResponse presensiAdd response id_presensi : "+response.body().getIdPresensi());
                    if(response.isSuccessful()){
                        if(response.body().getStatus().equalsIgnoreCase(TAG_OK)){
                            Toast.makeText(context, "PresensiDetail berhasil dibuka, qr-code berhasil di generate, kuliah dah boleh dimulai", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(itemView.getContext(), HistoriPresensiActivity.class);
                            Log.i(TAG, "onResponse: getIdPresensi() : "+response.body().getIdPresensi());
                            i.putExtra(TAG_ID_PRESENSI,response.body().getIdPresensi());
                            i.putExtra(TAG_STATUS_PRESENSI, STATUS_PRESENSI);
                            itemView.getContext().startActivity(i);
                        } else {
                            Toast.makeText(context, "onResponse error : "+response.body().getStatus(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePresensi> call, Throwable t) {
                    t.getLocalizedMessage();
                }
            });
        }
    }


}
