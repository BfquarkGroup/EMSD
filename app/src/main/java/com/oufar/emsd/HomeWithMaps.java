package com.oufar.emsd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.oufar.emsd.Adapter.RecyclerViewAdapter_3;
import com.oufar.emsd.Fragments.Fragment_1;
import com.oufar.emsd.Fragments.Fragment_2;
import com.oufar.emsd.Fragments.Fragment_3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeWithMaps extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnMarkerClickListener {


    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 200;

    private ChipNavigationBar navigation_bar;
    private FrameLayout fragmentContainer;
    private int index = 0;
    private FloatingActionButton btn_home, btn_search, btn_refresh, btn_client;
    private CircleImageView click_to_search, btn_logout, logo;
    private RelativeLayout relativeLayout_2;
    private EditText search_location;
    private SupportMapFragment mMapFragment;
    private RelativeLayout relativeLayout;
    private Fragment fragment = null;
    private FragmentManager fragmentManager;

    private int click = 1;
    private int click_ = 1;
    private String input;

    private String check = "";

    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    String txt_username = "";
    String txt_phone = "";
    String txt_wilaya = "";
    String txt_city = "";
    String txt_description = "";
    String imageURL = "";
    TextView username;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Marker userLocationMarker;
    private Circle userLocationAccuracyCircle;

    private String CLIENT_ID = "", CLIENT_NAME = "", CLIENT_IMAGE = "", CLIENT_LAT = "", CLIENT_LNG = "";
    private String Store_id = "", Store_name = "", Store_image = "", Store_lat = "", Store_lng = "";

    @SuppressLint({"ClickableViewAccessibility", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_with_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.custom_green));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.custom_green));
        }
        setNavigationBarButtonsColor(this, R.color.custom_green);

        Bundle bundle = getIntent().getExtras();
        CLIENT_ID = bundle.getString("Client_id");
        CLIENT_NAME = bundle.getString("Client_name");
        CLIENT_IMAGE = bundle.getString("Client_image");
        CLIENT_LAT = bundle.getString("Client_lat");
        CLIENT_LNG = bundle.getString("Client_lng");
        Store_id = bundle.getString("Store_id");
        Store_name = bundle.getString("Store_name");
        Store_image = bundle.getString("Store_image");
        Store_lat = bundle.getString("Store_lat");
        Store_lng = bundle.getString("Store_lng");


        /*Bundle bundle = getIntent().getExtras();

        if (bundle.getString("Store_name") != null) {

            STORE_NAME = bundle.getString("Store_name");
            STORE_LAT = bundle.getString("Store_lat");
            STORE_LNG = bundle.getString("Store_lng");

            if (!STORE_NAME.equals("") && !STORE_LAT.equals("") && !STORE_LNG.equals("")) {

                seeOnMap(STORE_NAME, STORE_LAT, STORE_LNG);
            }

        }*/

        final SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firestore = FirebaseFirestore.getInstance();

        downloadInfo_();

        // hiding map
        mMapFragment = (( SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map));
        getSupportFragmentManager().beginTransaction().hide(mMapFragment).commit();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        navigation_bar = findViewById(R.id.navigation_bar);
        username = findViewById(R.id.username);
        btn_logout = findViewById(R.id.btn_logout);
        logo = findViewById(R.id.logo);
        //btn_map_2 = findViewById(R.id.btn_map_2);
        btn_home = findViewById(R.id.btn_home);
        btn_search = findViewById(R.id.btn_search);
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_client = findViewById(R.id.btn_client);
        search_location = findViewById(R.id.search_location);
        click_to_search = findViewById(R.id.click_to_search);
        relativeLayout = findViewById(R.id.relativeLayout);
        fragmentContainer = findViewById(R.id.fragmentContainer);



        if (!CLIENT_NAME.equals("")) {

            navigation_bar.setItemSelected(R.id.map, true);
            showMap();
        }else {

            navigation_bar.setItemSelected(R.id.home, true);
        }


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //view_pager.setAdapter(viewPagerAdapter);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        final Fragment_1 fragment_1 = new Fragment_1();
        final Fragment_2 fragment_2 = new Fragment_2();
        final Fragment_3 fragment_3 = new Fragment_3();
        final com.oufar.emsd.Fragment_4 fragment_4 = new com.oufar.emsd.Fragment_4();
        adapter.addFragment(fragment_1);
        adapter.addFragment(fragment_2);
        adapter.addFragment(fragment_3);
        adapter.addFragment(fragment_4);
        //view_pager.setAdapter(adapter);

        index = 1;

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment_1)
                .commit();

        navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {

                switch(id){

                    case R.id.home:
                        //view_pager.setCurrentItem(0);
                        fragment = fragment_1;
                        hideMap();
                        break;

                    case R.id.list:
                        //view_pager.setCurrentItem(1);
                        fragment = fragment_2;
                        hideMap();
                        break;

                    case R.id.map:
                        showMap();
                        fragment = fragment_4;
                        break;

                    case R.id.profile:
                        //view_pager.setCurrentItem(2);
                        fragment = fragment_3;
                        hideMap();
                        break;
                }

                if (fragment != null){

                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .commit();
                }else if (fragment == fragment_4){

                    showMap();
                }
            }
        });


        relativeLayout_2 = findViewById(R.id.relativeLayout_2);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                //hideMap();

                navigation_bar.setItemSelected(R.id.home, true);
                //view_pager.setCurrentItem(0);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                check = "logout";
                Intent intent = new Intent(HomeWithMaps.this, Login.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeWithMaps.this, ListOfStoreWithMaps.class);
                //startActivity(intent);
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (click == 1) {

                    relativeLayout_2.setVisibility(View.VISIBLE);
                    click_to_search.setVisibility(View.VISIBLE);
                    search_location.setInputType(1);
                    click = 2;
                } else if (click == 2) {
                    click = 1;
                    relativeLayout_2.setVisibility(View.GONE);
                    click_to_search.setVisibility(View.GONE);
                }
            }
        });

        click_to_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input = search_location.getText().toString();

                if (!input.isEmpty()) {

                    try {
                        search();
                    } catch (IOException e) {

                    }
                    input = "";
                }
            }
        });

       /* btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(HomeWithMaps.this, "no action added yet", Toast.LENGTH_SHORT).show();


                if (click_ == 1) {

                   if (ContextCompat.checkSelfPermission(HomeWithMaps.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        mMap.setMyLocationEnabled(false);
                        startLocationUpdates();
                    }


                    mMap.setMyLocationEnabled(false);

                    click_ = 2;
                } else if (click_ == 2) {

                    click_ = 1;

                    //stopLocationUpdates();

                    mMap.clear();

                    mMap.setMyLocationEnabled(true);
                }

            }
        });*/

       btn_refresh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               showMap();

               mMap.setMyLocationEnabled(true);

               if (mMap.getMyLocation() != null) {

                   Location location = new Location(mMap.getMyLocation());
                   LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                   mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                   mMap.getUiSettings().setZoomControlsEnabled(true);
               }else {

                   Toast.makeText(HomeWithMaps.this, "activate your GPS", Toast.LENGTH_SHORT).show();
               }
           }
       });

        btn_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Clear the map then add a marker in Lectery hall and move the camera

                /*
                mMap.clear();
                double lat = Double.parseDouble(room_lat);
                double lang = Double.parseDouble(room_long);

                LatLng Room = new LatLng(lat, lang);
                mMap.addMarker(new MarkerOptions().position(Room).title(room));//" Emplacement de la salle "
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Room));*/

                Toast.makeText(HomeWithMaps.this, "no action added yet", Toast.LENGTH_SHORT).show();
            }
        });

        //transparentStatusAndNavigation();
    }

    private void showMap(){

        // making map visible and hiding home
        mMapFragment = (( SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map));
        getSupportFragmentManager().beginTransaction().show(mMapFragment).commit();
        relativeLayout.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
        btn_search.setVisibility(View.VISIBLE);
        btn_refresh.setVisibility(View.VISIBLE);
        btn_client.setVisibility(View.VISIBLE);
        btn_home.setVisibility(View.VISIBLE);
        //btn_map_2.setVisibility(View.GONE);
        click = 1;
    }

    private void hideMap(){

        // making home visible and hiding map
        mMapFragment = (( SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map));
        getSupportFragmentManager().beginTransaction().hide(mMapFragment).commit();
        relativeLayout.setVisibility(View.GONE);
        btn_search.setVisibility(View.GONE);
        click_to_search.setVisibility(View.GONE);
        relativeLayout_2.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        btn_client.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        //btn_map_2.setVisibility(View.VISIBLE);
        btn_home.setVisibility(View.GONE);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {



        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter (FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);

        }

    }

    public  void  downloadInfo_(){

        if (!firebaseUser.getUid().isEmpty()) {

            firestore.collection("DeliveryGuy").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    assert documentSnapshot != null;

                    if (!check.equals("logout")) {

                        txt_username = documentSnapshot.get("username").toString();
                        txt_phone = documentSnapshot.get("phone").toString();
                        txt_wilaya = documentSnapshot.get("wilaya").toString();
                        txt_city = documentSnapshot.get("city").toString();
                        txt_description = documentSnapshot.get("description").toString();
                        imageURL = documentSnapshot.get("imageURL").toString();
                    }
                    //Toast();
                }
            });
        }
    }

    public String Username() {
        return txt_username;
    }
    public String Phone() {
        return txt_phone;
    }
    public String Wilaya() {
        return txt_wilaya;
    }
    public String City() {
        return txt_city;
    }
    public String Description() {
        return txt_description;
    }
    public String ImageURL() {
        return imageURL;
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

        LatLng latLng = new LatLng(33.806285, 2.880058);

        if (!CLIENT_LAT.equals("") && !CLIENT_LNG.equals("")){

            String Title = "client";

            double lat = Double.parseDouble(CLIENT_LAT);
            double lang = Double.parseDouble(CLIENT_LNG);

            latLng = new LatLng(lat, lang);
            Title = CLIENT_NAME;

            int height = 90;
            int width = 90;
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.client_location);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            mMap.addMarker(new MarkerOptions().
                    position(latLng).title(Title).
                    icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }else {

            mMap.addMarker(new MarkerOptions().
                    position(latLng).title("الأغواط 03").
                    snippet("Laghouat").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

            return;
        }

        mMap.setMyLocationEnabled(true);

    }

    LocationCallback locationCallback = new LocationCallback(){

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);



            if (mMap != null){
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void setUserLocationMarker(Location location){

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null){

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ycar));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float)0.5, (float)0.5);

            userLocationMarker = mMap.addMarker(markerOptions);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }else {

            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }

        if (userLocationAccuracyCircle == null){

            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());

            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
        }else {

            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    private void startLocationUpdates(){

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates(){

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /*@Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            startLocationUpdates();
        }else {
            // request permission
        }
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    public void search() throws IOException {


        String location = search_location.getText().toString();
        List<Address> addressList = null;
        MarkerOptions mo = new MarkerOptions();

        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                //Log.d("error", "hereeee");
                addressList = geocoder.getFromLocationName(location, 5);
                //Log.d("error", "hereeee2");
                for (int i = 0; i < addressList.size(); i++) {
                    Address myAdress = addressList.get(i);
                    LatLng latLng = new LatLng(myAdress.getLatitude(), myAdress.getLongitude());
                    mo.position(latLng);
                    mo.title(location).snippet(myAdress.getPhone());
                    //mo.snippet(myAdress.getPostalCode());
                    mMap.addMarker(mo);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    //String name = myAdress.getPostalCode();

                }

                if (click == 2) {
                    click = 1;
                    relativeLayout_2.setVisibility(View.GONE);
                    click_to_search.setVisibility(View.GONE);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("error", e.getMessage() + "ERROR");
            }


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && Store_id.equals("")) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        else if (!Store_id.equals("")){

            Intent intent = new Intent(HomeWithMaps.this, ListOfStoreWithMaps.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Store_id", Store_id);// sendingBack Store id
            intent.putExtra("Store_name", Store_name);// sendingBack Store name
            intent.putExtra("Store_image", Store_image);// sendingBack Store image
            intent.putExtra("Store_lat", Store_lat);// sendingBack Store lat
            intent.putExtra("Store_lng", Store_lng);// sendingBack Store lng
            Store_id = "";
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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
