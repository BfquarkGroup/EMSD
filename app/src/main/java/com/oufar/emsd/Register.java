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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oufar.emsd.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText username, wilaya, city, phone, email, password, confirm_password;
    Button btn_register;
    ProgressBar progressBar;

    FirebaseAuth auth;
    private FirebaseFirestore firestore;

    String txt_username;
    String txt_wilaya;
    String txt_city;
    String txt_phone;
    String txt_email;
    String txt_password;
    String txt_confirm_password;

    @SuppressLint({"NewApi", "PrivateResource"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.white));
        }

        auth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.username);
        wilaya = findViewById(R.id.wilaya);
        city = findViewById(R.id.city);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);

        progressBar = findViewById(R.id.progressBar);

        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                txt_username = username.getText().toString();
                txt_wilaya = wilaya.getText().toString();
                txt_city = city.getText().toString();
                txt_phone = phone.getText().toString();
                txt_email = email.getText().toString();
                txt_password = password.getText().toString();
                txt_confirm_password = confirm_password.getText().toString();

                if (txt_username.isEmpty()){
                    Toast.makeText(Register.this, "enter username", Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                    return;
                }
                if (txt_wilaya.isEmpty()){
                    Toast.makeText(Register.this, "enter your wilaya", Toast.LENGTH_SHORT).show();
                    wilaya.requestFocus();
                    return;
                }
                if (txt_city.isEmpty()){
                    Toast.makeText(Register.this, "enter your city", Toast.LENGTH_SHORT).show();
                    city.requestFocus();
                    return;
                }
                if (txt_phone.isEmpty()){
                    Toast.makeText(Register.this, "enter your phone", Toast.LENGTH_SHORT).show();
                    phone.requestFocus();
                    return;
                }
                if (txt_email.isEmpty()){
                    Toast.makeText(Register.this, "enter your email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()){
                    Toast.makeText(Register.this, "enter a valid email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (txt_password.isEmpty()){
                    Toast.makeText(Register.this, "enter your password", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }
                if (txt_password.length()<6){
                    Toast.makeText(getApplicationContext(), "Minimum length of Password should be 6", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }
                if (txt_confirm_password.isEmpty()){
                    Toast.makeText(Register.this, "confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!txt_confirm_password.equals(txt_password)){
                    Toast.makeText(Register.this, "check your password", Toast.LENGTH_SHORT).show();
                    confirm_password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Register_2();
            }
        });

    }

    private void Register_2(){

        auth.createUserWithEmailAndPassword(txt_email,txt_password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.getText().toString());
                            hashMap.put("phone", phone.getText().toString());
                            hashMap.put("email", email.getText().toString());
                            hashMap.put("wilaya", wilaya.getText().toString());
                            hashMap.put("city", city.getText().toString());
                            hashMap.put("password", password.getText().toString());
                            hashMap.put("imageURL", "default");
                            hashMap.put("description", "nothing added yet");

                            firestore.collection("DeliveryGuy").document(userid).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Intent intent = new Intent(Register.this, HomeWithMaps.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                                        startActivity(intent);
                                        finish();

                                    }else {

                                        Toast.makeText(Register.this, "ERROR", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }else {
                            Toast.makeText(Register.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
