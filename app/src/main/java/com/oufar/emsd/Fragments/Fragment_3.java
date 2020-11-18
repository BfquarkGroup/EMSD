package com.oufar.emsd.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.QRCode;
import com.oufar.emsd.HomeWithMaps;
import com.oufar.emsd.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Fragment_3 extends Fragment {

    private static final int IMAGE_REQUEST = 1;

    private CircleImageView profile_picture, language_picture;
    private ProgressBar progressBar;
    private TextView change_profile_picture, language_txt;
    private EditText username, wilaya, city, phone, description;
    private RelativeLayout popUp;
    private CardView box, QRCode_holder;
    private ImageView close, QRCode;
    private RadioGroup radioGroup;
    private RadioButton radioButton, Arabic, English, French;
    private FloatingActionButton confirm;
    private Button btn_confirm;

    private Uri imageUri;
    private String imageURL;

    private HomeWithMaps home;

    private HashMap<String, Object> map = new HashMap<>();
    private DatabaseReference qreference;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    private StorageReference storageReference;
    private StorageTask uploadTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_3, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        //reference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
        //reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        home = (HomeWithMaps ) getActivity();

        change_profile_picture = view.findViewById(R.id.change_profile_picture);
        profile_picture = view.findViewById(R.id.profile_picture);
        progressBar = view.findViewById(R.id.progressBar);
        username = view.findViewById(R.id.username);
        wilaya = view.findViewById(R.id.wilaya);
        city = view.findViewById(R.id.city);
        phone = view.findViewById(R.id.phone);
        description = view.findViewById(R.id.description);
        language_picture = view.findViewById(R.id.language_picture);
        language_txt = view.findViewById(R.id.language_txt);
        popUp = view.findViewById(R.id.popUp);
        box = view.findViewById(R.id.box);
        QRCode_holder = view.findViewById(R.id.QRCode_holder);
        close = view.findViewById(R.id.close);
        QRCode = view.findViewById(R.id.QRCode);
        radioGroup = view.findViewById(R.id.radioGroup);
        Arabic = view.findViewById(R.id.Arabic);
        English = view.findViewById(R.id.English);
        French = view.findViewById(R.id.French);
        confirm = view.findViewById(R.id.confirm);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        //progressBar.setVisibility(View.VISIBLE);

        downloadInfo();

        change_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        language_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.VISIBLE);
                box.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
            }
        });

        language_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.VISIBLE);
                box.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
            }
        });

        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.GONE);
                box.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                QRCode_holder.setVisibility(View.GONE);
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.GONE);
                box.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Arabic.isChecked()){

                    Toast.makeText(getContext(), "Arabic", Toast.LENGTH_SHORT).show();
                }else if (English.isChecked()){

                    Toast.makeText(getContext(), "English", Toast.LENGTH_SHORT).show();
                }else if (French.isChecked()){

                    Toast.makeText(getContext(), "French", Toast.LENGTH_SHORT).show();
                }

                popUp.setVisibility(View.GONE);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((username.getText().toString().isEmpty() || username.getText().toString().equals(home.Username()))   &&
                        (wilaya.getText().toString().isEmpty() || wilaya.getText().toString().equals(home.Wilaya()))  &&
                        (city.getText().toString().isEmpty() || city.getText().toString().equals(home.City()))  &&
                        (phone.getText().toString().isEmpty() || phone.getText().toString().equals(home.Phone()))  &&
                        (description.getText().toString().isEmpty() || description.getText().toString().equals(home.Description())))
                {

                    return;
                }

                uploadInfo();
            }
        });

        profile_picture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popUp.setVisibility(View.VISIBLE);
                        QRCode_holder.setVisibility(View.VISIBLE);

                        String data = firebaseUser.getUid();
                        if(data.isEmpty()){

                            Toast.makeText(getContext(), "check your network", Toast.LENGTH_SHORT).show();
                            //qrvalue.setError("Value Required.");
                        }else {
                            QRGEncoder qrgEncoder = new QRGEncoder(data,null, QRGContents.Type.TEXT,500);
                            try {
                                Bitmap qrBits = qrgEncoder.encodeAsBitmap();
                                QRCode.setImageBitmap(qrBits);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        }




                    }
                }
        );


        return view;
    }

    private void uploadInfo() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (!username.getText().toString().isEmpty() || !username.getText().toString().equals(home.Username())){

            map.put("username", username.getText().toString());
            //username.setText(edit_username.getText().toString());
        }
        if (!phone.getText().toString().isEmpty() || !phone.getText().toString().equals(home.Phone())){

            map.put("phone", phone.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!wilaya.getText().toString().isEmpty() || !wilaya.getText().toString().equals(home.Wilaya())){

            map.put("wilaya", wilaya.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!city.getText().toString().isEmpty() || !city.getText().toString().equals(home.City())){

            map.put("city", city.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!description.getText().toString().isEmpty() || !description.getText().toString().equals(home.Description())){

            map.put("description", description.getText().toString());
            description.setHint(description.getText().toString());
        }

        firestore.collection("DeliveryGuy").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    progressDialog.dismiss();

                    //updatePage();
                }else {

                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    String current_image;

    public void uploadImage(){

        current_image = imageURL;

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        //reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(firebaseUser.getUid());

                        map.put("imageURL", mUri);

                        /*reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    deleteOldImage();

                                    progressDialog.dismiss();

                                }else {

                                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });*/

                        firestore.collection("DeliveryGuy").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    deleteOldImage();

                                    progressDialog.dismiss();

                                }else {

                                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            }
                        });


                    }else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOldImage() {

        if (!current_image.equals("default")) {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference_2 = firebaseStorage.getReferenceFromUrl(current_image);

            storageReference_2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully

                    //Toast.makeText(getContext(), "old image has been deleted", Toast.LENGTH_SHORT).show();

                    imageURL = home.ImageURL();

                    if (imageURL.equals("default")) {

                        profile_picture.setImageResource(R.drawable.delivery);
                    } else {
                        Glide.with(getContext()).load(imageURL).into(profile_picture);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!

                    Toast.makeText(getContext(), "an error occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            imageURL = home.ImageURL();

            if (imageURL.equals("default")) {

                profile_picture.setImageResource(R.drawable.wallpaper1);
            } else {
                Glide.with(getContext()).load(imageURL).into(profile_picture);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
                /*
                 we will add a method named confirm and uploadImage(); gonna be included in it so we can reduce the number of writ on firebase

                 and this method contain the edited username and address and ..
                */
            }

        }

    }

    public void downloadInfo(){

        username.setText(home.Username());
        phone.setText(home.Phone());
        wilaya.setText(home.Wilaya());
        city.setText(home.City());
        description.setText(home.Description());
        imageURL = home.ImageURL();

        if (imageURL.equals("default")){

            profile_picture.setImageResource(R.drawable.wallpaper1);
            progressBar.setVisibility(View.GONE);
        }else {

            Glide.with(getContext()).load(imageURL).into(profile_picture);
        }
    }
}
