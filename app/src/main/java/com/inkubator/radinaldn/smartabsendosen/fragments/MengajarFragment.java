package com.inkubator.radinaldn.smartabsendosen.fragments;

/**
 * Created by radinaldn on 09/07/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsendosen.R;
import com.inkubator.radinaldn.smartabsendosen.adapters.MengajarAdapter;
import com.inkubator.radinaldn.smartabsendosen.models.Mengajar;
import com.inkubator.radinaldn.smartabsendosen.responses.ResponseMengajar;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsendosen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsendosen.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by radinaldn on 08/07/18.
 */

public class MengajarFragment extends android.support.v4.app.Fragment {

    private RecyclerView recyclerView;
    private MengajarAdapter adapter;
    private ArrayList<Mengajar> mengajarArrayList;

    SwipeRefreshLayout swipeRefreshLayout;

    ApiInterface apiService;

    public static final String TAG_NIP = "nip";
    public static final String TAG = MengajarFragment.class.getSimpleName();
    SessionManager sessionManager;
    private String nip;
    private String dayname;
    private static final String ARG_DAYNAME = "dayname";

    public static MengajarFragment newInstance(String dayname) {

        Bundle args = new Bundle();
        args.putString(ARG_DAYNAME, dayname);

        MengajarFragment fragment = new MengajarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MengajarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            dayname = extras.getString(ARG_DAYNAME);
        }

        sessionManager = new SessionManager(getContext());

        nip = sessionManager.getDosenDetail().get(TAG_NIP);

        apiService = ApiClient.getClient().create(ApiInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mengajar, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        swipeRefreshLayout = view.findViewById(R.id.swipe_fragmen_mengajar);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh, R.color.refresh1, R.color.refresh2);
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMengajar(nip, dayname);

            }
        });

        getMengajar(nip, dayname);


//        TextView textView = view.findViewById(R.id.title);
//        Toast.makeText(getContext(), "dayname = "+dayname, Toast.LENGTH_SHORT).show();
//        textView.setText(nip + " " +dayname);

        return view;
    }

    private void getMengajar(String nip, final String dayname) {

        Call<ResponseMengajar> call = apiService.mengajarFindAllByNipAndName(nip, dayname);

        call.enqueue(new Callback<ResponseMengajar>() {
            @Override
            public void onResponse(Call<ResponseMengajar> call, Response<ResponseMengajar> response) {
                if (response.isSuccessful()){
                    Log.i(TAG, "onResponse: response = "+response);

                    if (response.body().getMengajar().size()>0) {
                        mengajarArrayList = new ArrayList<>();

                        mengajarArrayList.addAll(response.body().getMengajar());

                        adapter = new MengajarAdapter(mengajarArrayList, getContext(), TAG);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(adapter);

                        swipeRefreshLayout.setRefreshing(false);

                    } else {
                        Log.i(TAG, "onResponse: tidak ada matakuliah hari "+dayname );
                    }
                } else {
                    Log.e(TAG, "onResponse error : "+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseMengajar> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }


}
