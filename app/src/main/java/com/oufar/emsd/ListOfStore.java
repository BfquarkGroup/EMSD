package com.oufar.emsd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.oufar.emsd.Adapter.RecyclerViewAdapter_2;
import com.oufar.emsd.Model.Order;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListOfStore extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;

    private TextView storeName, nothing_txt;
    private FloatingActionButton refresh;
    private CircleImageView storeImage;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private RecyclerViewAdapter_2 adapter;
    private ArrayList<String> mNames_0 = new ArrayList<>();
    //private ArrayList<String> filterList = new ArrayList<>();
    //private ArrayList<Order> clientList = new ArrayList<>();
    private ArrayList<Order> clientList_ = new ArrayList<>();

    private String STORE_ID, STORE_NAME, STORE_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_store);

        /*Bundle bundle = getIntent().getExtras();
        STORE_ID = bundle.getString("Store_id");
        STORE_NAME = bundle.getString("Store_name");
        STORE_IMAGE = bundle.getString("Store_image");*/

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        storeName = findViewById(R.id.storeName);
        nothing_txt = findViewById(R.id.nothing_txt);
        storeImage = findViewById(R.id.storeImage);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        storeName.setText(STORE_NAME);

        if (!STORE_IMAGE.equals("default")) {

            Glide.with(this)
                    .asBitmap()
                    .load(STORE_IMAGE)
                    .into(storeImage);
        }

        /*refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Refresh();
            }
        });*/

        checkMenu();
    }

    public void checkMenu(){

        progressBar.setVisibility(View.VISIBLE);

        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);
                //clientList.clear();
                //filterList.clear();

                if (dataSnapshot.exists()){

                    //clientList.clear();
                    String storeId, clientId = "", delivery = "", status = "";
                    ArrayList<String> filterList = new ArrayList<>();
                    filterList.clear();

                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        for (DataSnapshot snapshot_: snapshot.getChildren()) {

                            storeId = snapshot_.child("storeId").getValue().toString();
                            clientId = snapshot_.child("clientId").getValue().toString();
                            delivery = snapshot_.child("delivery").getValue().toString();
                            status = snapshot_.child("status").getValue().toString();

                            if (storeId.equals(STORE_ID) && delivery.equals("on hold") && status.equals("accepted")) {

                                filterList.add(clientId);

                                /*if (filterList.size() == 0){

                                    filterList.add(clientId);

                                    i++;

                                    downloadMenu(i);

                                }else {

                                    for (int j = 0; j < filterList.size(); j++){

                                        if (!clientId.equals(filterList.get(j))){

                                            i++;

                                            downloadMenu(i);
                                        }
                                    }
                                }*/
                            }
                        }
                    }

                    deleteOccurrenceValues(filterList);

                }else {

                    progressBar.setVisibility(View.GONE);
                    nothing_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteOccurrenceValues(ArrayList<String> filterList) {

        //clientList.clear();

        for(int i = 0; i < filterList.size();i++){

            if (i != 0) {

                if (filterList.get(i).equals(filterList.get(i - 1))) {
                    filterList.remove(i--);
                }
            }
        }

        downloadMenu(filterList);
    }

    private void downloadMenu(final ArrayList<String> filterList){



        final ArrayList<Order> clientList = new ArrayList<>();
        clientList.clear();

        for (int i = 0; i < filterList.size(); i++) {

            firestore.collection("Client").document(filterList.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    String clientId_ = documentSnapshot.get("id").toString();
                    String name = documentSnapshot.get("username").toString();
                    String phone = documentSnapshot.get("phone").toString();
                    String address = documentSnapshot.get("address").toString();
                    String imageURL = documentSnapshot.get("imageURL").toString();
                    String lat = documentSnapshot.get("lat").toString();
                    String lng = documentSnapshot.get("lng").toString();

                    Order client_order = new Order("", clientId_, name, phone, address,
                            imageURL, lat, lng, "", STORE_ID, STORE_NAME, "on hold", null, null, null);

                    clientList.add(client_order);

                    initRecyclerView(clientList, filterList);

                }
            });
        }

        //counter.setText(filterList.size()+" customer(s) waiting.");
    }

    private void initRecyclerView(ArrayList<Order> clientList, ArrayList<String> filterList){

        progressBar.setVisibility(View.GONE);
        nothing_txt.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter_2(this, clientList, STORE_NAME);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        //counter.setText(clientList.size()+" customer(s) waiting.");

    }
}
