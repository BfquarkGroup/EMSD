package com.oufar.emsd.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.oufar.emsd.Model.Store;
import com.oufar.emsd.R;

import java.util.ArrayList;

public class Fragment_1 extends Fragment {



    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText searchInput;


    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    //vars
    private ArrayList<Store> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();


    private LinearLayout search;
    private int SEARCH = 1;

    /*private StoreAdapter storeAdapter;
    private ArrayList<Store> storeArrayList = new ArrayList<Store>();

    private ListView listView;*/
    private FloatingActionButton btn_search;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_1, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        //listView = view.findViewById(R.id.listView);
        btn_search = view.findViewById(R.id.btn_search);
        search = view.findViewById(R.id.search);
        searchInput = view.findViewById(R.id.searchInput);

        //downloadStores();

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SEARCH == 1){

                    search.setVisibility(View.VISIBLE);

                    SEARCH = 2;
                }else if (SEARCH == 2){

                    search.setVisibility(View.GONE);

                    SEARCH = 1;
                }
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        //getImages();

        downloadStores();

        return view;
    }


    private String address, delivery, description, email, id, imageURL, phone, username;

    private void downloadStores() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            firestore.collection("Store").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    progressBar.setVisibility(View.GONE);

                    mNames.clear();
                    mImageUrls.clear();

                    assert queryDocumentSnapshots != null;

                    if (firebaseUser != null && queryDocumentSnapshots != null) {

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {

                            assert doc != null;

                            address = doc.getString("address");
                            delivery = doc.getString("delivery");
                            description = doc.getString("description");
                            email = doc.getString("email");
                            id = doc.getString("id");
                            imageURL = doc.getString("imageURL");
                            phone = doc.getString("phone");
                            username = doc.getString("username");
                            String latitude = doc.getString("lat");
                            String longitude = doc.getString("lng");
                            String status = doc.getString("status");

                            Store store = new Store(address, delivery, description, email, id, imageURL, phone, username, latitude, longitude, status);
                            mNames.add(store);
                            mImageUrls.add("https://article.images.consumerreports.org/f_auto/prod/content/dam/CRO-Images-2020/Magazine/05May/CR-Health-Inlinehero-HealthyFastFood-3-20-v2");


                        }
                    }

                    initRecyclerView();
                }
            });
        }

        //===========================================================================
    }

    private void initRecyclerView(){

        // LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        com.oufar.emsd.Adapter.RecyclerViewAdapter adapter = new com.oufar.emsd.Adapter.RecyclerViewAdapter(getContext(), mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
    }

}
