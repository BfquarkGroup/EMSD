package com.oufar.emsd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
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

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.oufar.emsd.Adapter.RecyclerViewAdapter_2;
import com.oufar.emsd.Adapter.RecyclerViewAdapter_3;
import com.oufar.emsd.Adapter.RecyclerViewAdapter_Scan;
import com.oufar.emsd.Model.Menu;
import com.oufar.emsd.Model.Order;
import com.oufar.emsd.Model.Plate;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Scan extends AppCompatActivity {

    private CodeScanner codeScanner;
    private CodeScannerView scannView;
    private TextView clientID, client_name, clientName, nothing_txt;
    private CircleImageView client_picture, check;
    private RelativeLayout scanLayout, menuLayout;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String QRCode_txt = "";
    private ImageView image, QRCode;
    private ProgressBar progressBar;

    private String CLIENT_ID, CLIENT_NAME, CLIENT_IMAGE;

    private ArrayList<Menu> menuArrayList = new ArrayList<>();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

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

        scannView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this,scannView);
        client_picture = findViewById(R.id.client_picture);
        client_name = findViewById(R.id.client_name);
        check = findViewById(R.id.check);
        scanLayout = findViewById(R.id.scanLayout);
        menuLayout = findViewById(R.id.menuLayout);
        image = findViewById(R.id.image);
        clientName = findViewById(R.id.clientName);
        QRCode = findViewById(R.id.QRCode);
        recyclerView = findViewById(R.id.recyclerView);
        nothing_txt = findViewById(R.id.nothing_txt);
        progressBar = findViewById(R.id.progressBar);

        clientName.setText(CLIENT_NAME);

        Glide.with(this)
                .asBitmap()
                .load(CLIENT_IMAGE)
                .into(image);

        Glide.with(this)
                .asBitmap()
                .load(CLIENT_IMAGE)
                .into(client_picture);

        client_name.setText(CLIENT_NAME);

        codeScanner.setDecodeCallback(new DecodeCallback() {

            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        QRCode_txt = result.getText();

                        if (QRCode_txt.equals(CLIENT_ID)){

                            check.setImageResource(R.drawable.correct);
                            scanLayout.setVisibility(View.GONE);
                            menuLayout.setVisibility(View.VISIBLE);

                        }else if (!QRCode_txt.equals(CLIENT_ID)){

                            check.setImageResource(R.drawable.incorrect);
                        }else if (QRCode_txt.isEmpty()){

                            check.setImageResource(R.drawable.refresh);
                        }
                        //clientID.setText(QRCode_txt);
                    }
                });

            }
        });

        QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanLayout.setVisibility(View.VISIBLE);
                menuLayout.setVisibility(View.GONE);

            }
        });

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();

                check.setImageResource(R.drawable.refresh);
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //progressBar.setVisibility(View.GONE);

                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.clear();

                menuArrayList.clear();
                progressBar.setVisibility(View.VISIBLE);

                int i = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot snapshot_2 : snapshot.getChildren()) {

                        String delivery = snapshot_2.child("delivery").getValue().toString();
                        String storeName = snapshot_2.child("storeName").getValue().toString();
                        String status = snapshot_2.child("status").getValue().toString();

                        String clientEmail = snapshot_2.child("clientEmail").getValue().toString();
                        String clientId = snapshot_2.child("clientId").getValue().toString();
                        String description = snapshot_2.child("description").getValue().toString();
                        String id = snapshot_2.child("id").getValue().toString();
                        String imageURL = snapshot_2.child("imageURL").getValue().toString();
                        String number = snapshot_2.child("number").getValue().toString();
                        String plate = snapshot_2.child("plate").getValue().toString();
                        String price = snapshot_2.child("price").getValue().toString();
                        String storeEmail = snapshot_2.child("storeEmail").getValue().toString();
                        String storeId = snapshot_2.child("storeId").getValue().toString();


                        if (delivery.equals(firebaseUser.getUid()) && !storeName.equals("on hold") && status.equals("accepted")) {

                            i += 1;

                            Menu menu = new Menu(delivery, storeName, status, clientEmail, clientId, description, id, imageURL, number, plate, price, storeEmail, storeId);
                            menuArrayList.add(menu);

                            //Toast.makeText(Scan.this, ""+i, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                progressBar.setVisibility(View.GONE);
                nothing_txt.setVisibility(View.GONE);
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter_Scan recyclerViewAdapter_scan = new RecyclerViewAdapter_Scan(Scan.this, menuArrayList);
        recyclerView.setAdapter(recyclerViewAdapter_scan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();

    }

    public void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(Scan.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).check();
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
