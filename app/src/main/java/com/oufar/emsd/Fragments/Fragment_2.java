package com.oufar.emsd.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.client.authentication.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.oufar.emsd.Adapter.RecyclerViewAdapter_3;
import com.oufar.emsd.Database.DB;
import com.oufar.emsd.Model.Order;
import com.oufar.emsd.R;

import org.json.JSONObject;

import java.util.ArrayList;

import kotlin.random.Random;

public class Fragment_2 extends Fragment {

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private DB db;

    private String check = "nothing";
    private SwipeRefreshLayout swipeLayout;
    private RecyclerView recyclerView;
    private TextView counter;
    private ProgressBar progressBar;
    //private FloatingActionButton refresh;
    private ArrayList<Order> mNames = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_2, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db = new DB(getContext());

        swipeLayout = view.findViewById(R.id.swipe);
        recyclerView = view.findViewById(R.id.recyclerView);
        counter = view.findViewById(R.id.counter);
        progressBar = view.findViewById(R.id.progressBar);
        //refresh = view.findViewById(R.id.refresh);


        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancle the Visual indication of a refresh
                        swipeLayout.setRefreshing(false);

                        checkMenu();
                        refreshClientList.clear();
                        initRecyclerView(refreshClientList);
                    }
                }, 500);
            }
        });

        /*refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkMenu();
            }
        });*/

        checkMenu();

        return view;
    }

    public void checkMenu_(){

        mNames.clear();

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            check = "nothing";

            Toast.makeText(getContext(), "No client on your list", Toast.LENGTH_SHORT).show();

            counter.setText("0 standing by");

        }else {

            mNames.clear();

            while (cursor.moveToNext()){

                check = "something";

                /*

                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+  // 0
                CLIENT_ID+ " TEXT, "+  // 1
                NAME+ " TEXT, "+  // 2
                PHONE+ " TEXT, "+  // 3
                ADDRESS+ " TEXT, "+  // 4
                IMAGE_URL+ " TEXT, "+  // 5
                LAT+ " TEXT, "+  // 6
                LNG+ " TEXT "+")";  // 7

                 */

                Order order = new Order(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), "",
                        cursor.getString(8), cursor.getString(9), "", null, null, null);


                //Order orders = new Order("", "", "", "", "", "", "", "");

                mNames.add(order);
            }

            deleteOccurrenceValues(mNames);
            //initRecyclerView();
        }
    }

    int i = 0;
    public void checkMenu(){

        final ArrayList<Order> filterList = new ArrayList<>();
        filterList.clear();

        i = filterList.size();

        progressBar.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    for (DataSnapshot snapshot_2: snapshot.getChildren()){

                        String delivery  = snapshot_2.child("delivery").getValue().toString();
                        String storeName  = snapshot_2.child("storeName").getValue().toString();

                        // storeName gonna be edited by delivery guy when he click accept from on hold to restaurant name

                        if (delivery.equals(firebaseUser.getUid()) && !storeName.equals("on hold")){

                            String id  = snapshot_2.child("id").getValue().toString();
                            String clientId  = snapshot_2.child("clientId").getValue().toString();

                            Order order = new Order(id, clientId, null, null, null, null, null, null, null, null, storeName, null, null, null, null);

                            i++;

                            //counter.setText(i+" standing by");

                            //initRecyclerView(filterList);

                            filterList.add(order);

                            //loadData(filterList);
                        }
                    }
                }

                deleteOccurrenceValues(filterList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //deleteOccurrenceValues(filterList);
    }

    private void deleteOccurrenceValues(ArrayList<Order> filterList) {

        //clientList.clear();

        for(int i = 0; i < filterList.size();i++){

            if (i != 0) {

                if (filterList.get(i).getClientId().equals(filterList.get(i - 1).getClientId())) {
                    filterList.remove(i--);
                }
            }
        }

        counter.setText(filterList.size()+" customer(s) standing by");

        if (filterList.size() == 0){


        }

        loadData(filterList);

        //loadData(filterList);

        //initRecyclerView(filterList);
    }

    private void loadData(final ArrayList<Order> filterList) {

        final ArrayList<Order> clientList = new ArrayList<>();
        clientList.clear();

        for (int i = 0; i < filterList.size(); i++) {

            final String ClientId = filterList.get(i).getClientId();
            final int finalI = i;

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("Client").document(filterList.get(i).getClientId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    String id = documentSnapshot.get("id").toString();
                    String name = documentSnapshot.get("username").toString();
                    //String address = documentSnapshot.get("address").toString();
                    String imageURL = documentSnapshot.get("imageURL").toString();
                    String phone = documentSnapshot.get("phone").toString();
                    String lat = documentSnapshot.get("lat").toString();
                    String lng = documentSnapshot.get("lng").toString();


                    Order order = new Order(id, ClientId, name, phone, "", imageURL, lat, lng, "", "", "", "", null, null, null);

                    clientList.add(order);

                    initRecyclerView(clientList);
                }
            });

        }



        //initRecyclerView(filterList);
    }

    private ArrayList<Order> refreshClientList;

    public void initRecyclerView(ArrayList<Order> clientList){

        refreshClientList = clientList;

        //counter.setText(clientList.size()+" standing by");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //RecyclerViewAdapter_3 adapter = new RecyclerViewAdapter_3(getContext(), mNames);
        RecyclerViewAdapter_3 adapter = new RecyclerViewAdapter_3(getContext(), clientList);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        //Toast.makeText(getContext(), adapter.getItemCount()+"", Toast.LENGTH_SHORT).show();

    }

}





