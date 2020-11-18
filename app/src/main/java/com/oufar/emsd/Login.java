package com.oufar.emsd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oufar.emsd.MainActivity;
import com.oufar.emsd.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button btn_login;
    ProgressBar progressBar;

    String txt_email, txt_password;

    FirebaseAuth auth;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.white));
        }

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);

        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_email = email.getText().toString();
                txt_password = password.getText().toString();

                if (txt_email.isEmpty()){
                    Toast.makeText(Login.this, "enter your email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()){
                    Toast.makeText(Login.this, "enter a valid email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (txt_password.isEmpty()){
                    Toast.makeText(Login.this, "enter your password", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                login();
            }
        });
    }

    private void login() {


        auth.signInWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            download_data();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void download_data() {


        FirebaseUser firebaseUser = auth.getCurrentUser();

        //OneSignal.sendTag("User_ID",loggmail);
        final String userid = firebaseUser.getUid();

        final Intent intent = new Intent (Login.this, HomeWithMaps.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Client_id", "");// sending Client id
        intent.putExtra("Client_name", "");// sending Client name
        intent.putExtra("Client_image", "");// sending Client image
        intent.putExtra("Client_lat", "");// sending Client lat
        intent.putExtra("Client_lng", "");// sending Client lng
        intent.putExtra("Store_id", "");// sending Store id
        intent.putExtra("Store_name", "");// sending Store name
        intent.putExtra("Store_image", "");// sending Store image
        intent.putExtra("Store_lat", "");// sending Store lat
        intent.putExtra("Store_lng", "");// sending Store lng

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.getInstance().collection("DeliveryGuy").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {

                    progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "User", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseAuth.getInstance().signOut();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Wrong user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }
}
