package com.oufar.emsd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.CaseMap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class ListOfStoreWithMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 200;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;

    private TextView storeName, nothing_txt;
    private CircleImageView storeImage;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CardView close;
    private RelativeLayout maps_holder;
    private ImageView location;
    private int LOCATION = 1;

    private RecyclerViewAdapter_2 adapter;

    private String STORE_ID, STORE_NAME, STORE_IMAGE, STORE_LAT, STORE_LNG, Store_status;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_store_with_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //transparentStatusAndNavigation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.white));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        setNavigationBarButtonsColor(this, R.color.white);

        Bundle bundle = getIntent().getExtras();
        STORE_ID = bundle.getString("Store_id");
        STORE_NAME = bundle.getString("Store_name");
        STORE_IMAGE = bundle.getString("Store_image");
        STORE_LAT = bundle.getString("Store_lat");
        STORE_LNG = bundle.getString("Store_lng");
        Store_status = bundle.getString("Store_status");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        storeName = findViewById(R.id.storeName);
        nothing_txt = findViewById(R.id.nothing_txt);
        storeImage = findViewById(R.id.storeImage);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        close = findViewById(R.id.close);
        maps_holder = findViewById(R.id.maps_holder);
        location = findViewById(R.id.location);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LOCATION == 1){

                    maps_holder.setVisibility(View.VISIBLE);

                    LOCATION = 2;
                }else if (LOCATION == 2){

                    maps_holder.setVisibility(View.GONE);

                    LOCATION = 1;
                }

            }
        });

        if (Store_status.equals("close")){

            close.setVisibility(View.VISIBLE);
        }


        storeName.setText(STORE_NAME);

        if (!STORE_IMAGE.equals("default")) {

            Glide.with(this)
                    .asBitmap()
                    .load(STORE_IMAGE)
                    .into(storeImage);
        }

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
                    //String address = documentSnapshot.get("address").toString();
                    String imageURL = documentSnapshot.get("imageURL").toString();
                    String lat = documentSnapshot.get("lat").toString();
                    String lng = documentSnapshot.get("lng").toString();

                    Order client_order = new Order("", clientId_, name, phone, "",
                            imageURL, lat, lng, "", STORE_ID, STORE_NAME, "on hold", STORE_IMAGE , STORE_LAT, STORE_LNG);

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

    public void t (){

        Toast.makeText(this, "t", Toast.LENGTH_SHORT).show();
    }






    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng Store = new LatLng(33.806285, 2.880058);
        String Title = "Laghouat";

        if (STORE_LAT != null && STORE_LNG != null) {

            double lat = Double.parseDouble(STORE_LAT);
            double lang = Double.parseDouble(STORE_LNG);

            Store = new LatLng(lat, lang);
            Title = STORE_NAME;

            int height = 70;
            int width = 70;
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.marker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            mMap.addMarker(new MarkerOptions().
                    position(Store).title(Title).
                    icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }else {

            mMap.addMarker(new MarkerOptions().
                    position(Store).title(Title).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Store, 15));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

            return;
        }

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMyLocationEnabled(false);

    }


    private void setNavigationBarButtonsColor(Activity activity, int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight(navigationBarColor)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

}
