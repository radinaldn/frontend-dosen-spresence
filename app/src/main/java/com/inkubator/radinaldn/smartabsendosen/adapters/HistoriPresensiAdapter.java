package com.inkubator.radinaldn.smartabsendosen.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.activities.HistoriPresensiActivity;
import com.inkubator.radinaldn.smartabsendosen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsendosen.fragments.HistoriPresensiFragment;
import com.inkubator.radinaldn.smartabsendosen.models.PresensiDetail;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensi;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponsePresensiDetail;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by radinaldn on 17/07/18.
 */

public class HistoriPresensiAdapter extends RecyclerView.Adapter<HistoriPresensiAdapter.HistoriPresensiViewHolder> {

    private static final String TAG_OPEN = "open";
    private Context mContext;
    private HistoriPresensiFragment fragment;
    private ArrayList<PresensiDetail> dataList;
    private static final String TAG = HistoriPresensiAdapter.class.getSimpleName();

    ApiInterface apiService;

    public String ID_PRESENSI;
    private int tabPosition;

    public HistoriPresensiAdapter(ArrayList<PresensiDetail> dataList, Context context, HistoriPresensiFragment fragment) {
        this.dataList = dataList;
        this.mContext = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public HistoriPresensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.histori_presensi_item, parent, false);

        apiService = ApiClient.getClient().create(ApiInterface.class);


//        if (mContext instanceof HistoriPresensiActivity){
//            String id_presensi = ((HistoriPresensiActivity) mContext).getIDPRESENSI();
//
//            apiService.isCloseByIdPresensi(id_presensi).enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Log.d(TAG, "onResponse: "+response.toString());
//                    if (response.isSuccessful()){
//                        Log.d(TAG, "onResponse: "+response.body().toString());
//                    } else {
//                        Log.e(TAG, "onResponse: "+response.errorBody());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Toast.makeText(mContext, "onFailure: "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//        }

        return new HistoriPresensiViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HistoriPresensiViewHolder holder, int position) {
        holder.tv_id_presensi.setText(dataList.get(position).getIdPresensi());
        ID_PRESENSI = dataList.get(position).getIdPresensi();
        holder.tv_nim.setText(dataList.get(position).getNim());
        holder.tv_nama_mahasiswa.setText(dataList.get(position).getNamaMahasiswa());
        holder.tv_waktu.setText(dataList.get(position).getWaktu());
        holder.tv_jarak.setText(dataList.get(position).getJarak() + " Meter");
        holder.tv_status.setText(dataList.get(position).getStatus());

        int jarak = Integer.parseInt(dataList.get(position).getJarak());
        if (jarak > 0 && jarak <= 50) {
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.GreenBootstrap));
        } else if (jarak > 50 && jarak < 100) {
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else if (jarak > 100) {
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.RedBootstrap));
        } else {
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.colorBlueJeansDark));
        }

        // ubah warna text status kehadiran
        switch (dataList.get(position).getStatus()) {
            case "Hadir":
                holder.tv_status.setBackgroundColor(mContext.getResources().getColor(R.color.GreenBootstrap));
                tabPosition = 0;
                break;
            case "Tidak Hadir":
                holder.tv_status.setBackgroundColor(mContext.getResources().getColor(R.color.RedBootstrap));
                tabPosition = 1;
                break;

        }

        holder.tv_proses.setText(dataList.get(position).getProses());

        Picasso.with(holder.itemView.getContext())
                .load(ServerConfig.IMAGE_PATH + "mahasiswa/" + dataList.get(position).getFoto_mahasiswa())

                .placeholder(R.drawable.dummy_ava)
                .error(R.drawable.dummy_ava)
                .centerCrop()
                .fit()
                .into(holder.iv_avatar);

        if (dataList.get(position).getProses().equalsIgnoreCase("Pending")) {
            holder.iv_proses.setImageResource(R.drawable.ic_on_proggres);
        } else {
            holder.iv_proses.setImageResource(R.drawable.ic_clear_green);
        }

        if (holder.tv_status.getText().equals("Hadir")) {
            holder.bt_cancel.setImageResource(R.drawable.ic_cancel_black_24dp);
//            holder.bt_cancel.setVisibility(View.VISIBLE);
        }

        if (holder.tv_status.getText().equals("Tidak Hadir")) {
            holder.bt_cancel.setImageResource(R.drawable.ic_pencil_black_circular_button);
        }


        Log.d(TAG, "onBindViewHolder: Berhasil memasukk data : " + dataList.get(position).getNamaMahasiswa());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class HistoriPresensiViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_id_presensi, tv_nim, tv_nama_mahasiswa, tv_status, tv_lat, tv_lng, tv_waktu, tv_jarak, tv_proses;
        private Button bt_konfirmasi;
        private ImageButton bt_cancel;
        private ImageView iv_avatar, iv_proses;

        public HistoriPresensiViewHolder(final View itemView) {
            super(itemView);
            tv_id_presensi = itemView.findViewById(R.id.tv_id_presensi);
            tv_nim = itemView.findViewById(R.id.tv_nim);
            tv_nama_mahasiswa = itemView.findViewById(R.id.tv_nama);
            tv_waktu = itemView.findViewById(R.id.tv_waktu);
            tv_jarak = itemView.findViewById(R.id.tv_jarak);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_proses = itemView.findViewById(R.id.tv_proses);
            bt_cancel = itemView.findViewById(R.id.bt_cancel);
            iv_avatar = itemView.findViewById(R.id.iv_foto);
            iv_proses = itemView.findViewById(R.id.iv_proses);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // handling for item click
//                    Toast.makeText(itemView.getContext(), "Anda menekan item "+tv_nama_mahasiswa.getText().toString(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(mContext, "Scroll Position : "+fragment.getRecyclerViewScrollPosition(), Toast.LENGTH_SHORT).show();

                }
            });

            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // jika hadir maka aksi klik batalkan
                    if (tv_status.getText().equals("Hadir")) {
                        AlertDialog.Builder confirmBox = new AlertDialog.Builder(itemView.getContext());
                        confirmBox.setTitle(R.string.batalkan_presensi);
                        confirmBox.setIcon(R.drawable.ic_live_help_black_24dp);
                        confirmBox.setMessage(mContext.getString(R.string.anda_yakin_ingin_membatalkan_presensi) +" "+ tv_nama_mahasiswa.getText().toString() + " ?");
                        confirmBox.setCancelable(false);

                        confirmBox.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // lakukan batalkan presensi here

                                // jika presensi masih open
                                if (HistoriPresensiActivity.getSTATUS().equals(TAG_OPEN)) {
                                    batalkanPresensi(ID_PRESENSI, tv_nim.getText().toString());
                                } else {
                                    //Toast.makeText(mContext, "Status presensi : "+HistoriPresensiActivity.getSTATUS(), Toast.LENGTH_SHORT).show();
                                    batalkanPresensiDanTambahTidakHadir(ID_PRESENSI, tv_nim.getText().toString());
                                }
                            }
                        });
                        confirmBox.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialogKonfirmasi = confirmBox.create();
                        alertDialogKonfirmasi.show();
                    } else if (tv_status.getText().equals("Tidak Hadir")) {
                        AlertDialog.Builder confirmBox = new AlertDialog.Builder(itemView.getContext());
                        confirmBox.setTitle(R.string.terima_presensi);
                        confirmBox.setIcon(R.drawable.ic_live_help_black_24dp);
                        confirmBox.setMessage(mContext.getString(R.string.anda_yakin_ingin_menerima_presensi) +" "+ tv_nama_mahasiswa.getText().toString() + " ?");
                        confirmBox.setCancelable(false);

                        confirmBox.setPositiveButton(mContext.getString(R.string.ya), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // lakukan batalkan presensi here

                                // jika presensi masih open
                                if (HistoriPresensiActivity.getSTATUS().equals(TAG_OPEN)) {
                                    terimaPresensi(ID_PRESENSI, tv_nim.getText().toString());
                                } else {
//                                    Toast.makeText(mContext, "Status presensi : "+HistoriPresensiActivity.getSTATUS(), Toast.LENGTH_SHORT).show();
                                    terimaPresensiDanKurangiTidakHadir(ID_PRESENSI, tv_nim.getText().toString());
                                }

                            }
                        });
                        confirmBox.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialogKonfirmasi = confirmBox.create();
                        alertDialogKonfirmasi.show();
                    }


                }
            });


        }

        private void batalkanPresensi(String id_presensi, String nim) {
            apiService.presensiDetailBatalkanPresensi(id_presensi, nim).enqueue(new Callback<ResponsePresensiDetail>() {
                @Override
                public void onResponse(Call<ResponsePresensiDetail> call, Response<ResponsePresensiDetail> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: " + response.body());
                        fragment.refreshData(fragment.getRecyclerViewScrollPosition(), tabPosition);
                    } else {
                        Toast.makeText(itemView.getContext(), mContext.getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePresensiDetail> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), mContext.getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
                }
            });
        }

        private void batalkanPresensiDanTambahTidakHadir(String id_presensi, String nim) {
            apiService.presensiDetailBatalkanPresensiDanTambahTidakHadir(id_presensi, nim).enqueue(new Callback<ResponsePresensi>() {
                @Override
                public void onResponse(Call<ResponsePresensi> call, Response<ResponsePresensi> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: " + response.body());
                        Toast.makeText(itemView.getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        fragment.refreshData(fragment.getRecyclerViewScrollPosition(), tabPosition);
                    } else {
                        Toast.makeText(itemView.getContext(), mContext.getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePresensi> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), mContext.getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
                }
            });
        }

        private void terimaPresensi(String id_presensi, String nim) {
            apiService.presensiDetailTerimaPresensi(id_presensi, nim).enqueue(new Callback<ResponsePresensiDetail>() {
                @Override
                public void onResponse(Call<ResponsePresensiDetail> call, Response<ResponsePresensiDetail> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: " + response.body());

                        fragment.refreshData(fragment.getRecyclerViewScrollPosition(), tabPosition);
                    } else {
                        Toast.makeText(itemView.getContext(), mContext.getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePresensiDetail> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), mContext.getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
                }
            });
        }

        private void terimaPresensiDanKurangiTidakHadir(String id_presensi, String nim) {
            apiService.presensiDetailTerimaPresensiDanKurangiTidakHadir(id_presensi, nim).enqueue(new Callback<ResponsePresensi>() {
                @Override
                public void onResponse(Call<ResponsePresensi> call, Response<ResponsePresensi> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: " + response.body());
                        fragment.refreshData(fragment.getRecyclerViewScrollPosition(), tabPosition);
                    } else {
                        Toast.makeText(itemView.getContext(), mContext.getString(R.string.terjadi_kesalahan), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePresensi> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), mContext.getString(R.string.gagal_terhubung_ke_server), Toast.LENGTH_LONG).show();
                }
            });
        }

        private void refreshActivity() {
            Intent intent = ((Activity) itemView.getContext()).getIntent();
            ((Activity) itemView.getContext()).finish();
            itemView.getContext().startActivity(intent);
        }


    }
}
