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
import com.inkubator.radinaldn.smartabsendosen.activities.MainActivity;
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
    String parentActivityName;

    public MengajarAdapter(ArrayList<Mengajar> dataList, Context context, String parentActivityName) {
        this.dataList = dataList;
        this.mContext = context;
        this.parentActivityName = parentActivityName;
    }

    private static final String TAG_ID_MENGAJAR = "id_mengajar";
    private static final String TAG_MATAKULIAH = "matakuliah";
    private static final String TAG_KELAS = "kelas";

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

        private static final String TAG_OK = "OK";
        private static final String TAG_FAILED = "FAILED";

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

            System.out.println("parentActivityName : "+parentActivityName);


            if (parentActivityName.equals(MainActivity.TAG)){
                bt_mulai.setVisibility(View.INVISIBLE);
            } else {
                bt_mulai.setVisibility(View.VISIBLE);
            }

            if(mContext instanceof MengajarActivity){
                if (((MengajarActivity) mContext).getLatitude().equalsIgnoreCase("0.0")){
                    setDisabledBtMulai();
                } else {
                    setEnabledBtMulai();
                }


            }



            // ketika matakuliah diklik untuk memulai perkuliahan
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Aktifkan untuk mode debugging
                    //Toast.makeText(itemView.getContext(), "Anda menekan item "+tv_idmengajar.getText().toString(), Toast.LENGTH_SHORT).show();

                }
            });


            // bt_histori di klik
            bt_histori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // arahkan ke Activity history presensi
                    Intent i = new Intent(itemView.getContext(), HistoriMengajarActivity.class);
                    i.putExtra(TAG_ID_MENGAJAR, tv_idmengajar.getText());

                    // Aktifkan untuk mode debugging
                    //Toast.makeText(itemView.getContext(), "ID_MENGAJAR : "+tv_idmengajar.getText(), Toast.LENGTH_SHORT).show();

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

        private void setEnabledBtMulai() {
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

        private void setDisabledBtMulai() {
            //bt_mulai.setEnabled(false);
            bt_mulai.setBackgroundColor(context.getResources().getColor(R.color.textColorPrimaryDisabled));
            bt_mulai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MengajarActivity) mContext).showDialogKonfirmasiPindahKeDimanaSayaActivity();
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


                                if (current_lat.equalsIgnoreCase("0.0")) {
                                    Toast.makeText(itemView.getContext(), "Aplikasi tidak berhasil mendapatkan lokasi anda."+"\nLat : "+current_lat+"\nLng : "+current_lng, Toast.LENGTH_LONG).show();

                                } else {
//                                    Toast.makeText(context, "presensiAdd()\nlat : "+current_lat+"\nlng : "+current_lng, Toast.LENGTH_SHORT).show();
                                    // uncomment agar data dikirim ke rest api
                                    presensiAdd(id_mengajar, selectedIdRuangan, current_lat, current_lng, sessionManager.getDosenDetail().get(TAG_NIP), selectedNamaRuangan, matakuliah, kelas);
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

        public void presensiAdd(String id_mengajar, String id_ruangan, String lat, String lng, String nip, String nama_ruangan, final String matakuliah, final String kelas){
            System.out.println();
            apiService.presensiAdd(id_mengajar, id_ruangan, lat, lng, nip, nama_ruangan).enqueue(new Callback<ResponsePresensi>() {
                @Override
                public void onResponse(Call<ResponsePresensi> call, Response<ResponsePresensi> response) {
                    System.out.println(response.toString());
                    if(response.isSuccessful()){
                        if(response.body().getStatus().equals(TAG_OK) || response.body().getStatus().equals(TAG_FAILED)){
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(itemView.getContext(), HistoriPresensiActivity.class);
                            Log.i(TAG, "onResponse: getIdPresensi() : "+response.body().getIdPresensi());
                            i.putExtra(TAG_ID_PRESENSI,response.body().getIdPresensi());
                            i.putExtra(TAG_STATUS_PRESENSI, STATUS_PRESENSI);
                            i.putExtra(TAG_MATAKULIAH, matakuliah);
                            i.putExtra(TAG_KELAS, kelas);
                            itemView.getContext().startActivity(i);
                        } else {
                            Toast.makeText(context, "onResponse error : "+response.toString(), Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(context, "Error memulai presensi", Toast.LENGTH_LONG).show();
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
