package com.oufar.emsd.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oufar.emsd.Database.DB;
import com.oufar.emsd.HomeWithMaps;
import com.oufar.emsd.ListOfStoreWithMaps;
import com.oufar.emsd.Model.Order;
import com.oufar.emsd.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter_2 extends RecyclerView.Adapter<RecyclerViewAdapter_2.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter_2";

    //vars
    private ArrayList<Order> clientList = new ArrayList<>();
    private String STORE_NAME;
    private Context mContext;

    public RecyclerViewAdapter_2(Context context, ArrayList<Order> names, String store_name) {
        clientList = names;
        STORE_NAME = store_name;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_of_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        if (!clientList.get(position).getClientImageURL().equals("default")) {

            Glide.with(mContext)
                    .asBitmap()
                    .load(clientList.get(position).getClientImageURL())
                    .into(holder.image);
        }


        holder.client.setText(clientList.get(position).getClientName());
        holder.phone.setText(clientList.get(position).getClientPhone());
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("You want to call this customer (" + clientList.get(position).getClientName() + ") ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(clientList.get(position).getClientPhone())));
                                mContext.startActivity(call);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });

        holder.number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("Number of plate ?")
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("OK", null)
                        .show();

            }
        });


        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");

                reference.child(clientList.get(position).getClientId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                            String storeId = snapshot.child("storeId").getValue().toString();
                            String delivery = snapshot.child("delivery").getValue().toString();

                            if (clientList.get(position).getStoreId().equals(storeId) &&
                                delivery.equals("on hold")){

                                String id = snapshot.child("id").getValue().toString();

                                DatabaseReference reference_ = FirebaseDatabase.getInstance().getReference("Orders");

                                reference_.child(clientList.get(position).getClientId()).child(id).child("delivery").setValue(firebaseUser.getUid());
                                reference_.child(clientList.get(position).getClientId()).child(id).child("storeName").setValue(STORE_NAME);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        final Intent intent = new Intent(mContext, HomeWithMaps.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Client_id", clientList.get(position).getId());// sending Client id
        intent.putExtra("Client_name", clientList.get(position).getClientName());// sending Client name
        intent.putExtra("Client_image", clientList.get(position).getClientImageURL());// sending Client image
        intent.putExtra("Client_lat", clientList.get(position).getLat());// sending Client lat
        intent.putExtra("Client_lng", clientList.get(position).getLng());// sending Client lng
        intent.putExtra("Store_id", clientList.get(position).getStoreId());// sending Store id
        intent.putExtra("Store_name", clientList.get(position).getStoreName());// sending Store name
        intent.putExtra("Store_image", clientList.get(position).getStoreImageURL());// sending Store image
        intent.putExtra("Store_lat", clientList.get(position).getStoreLat());// sending Store lat
        intent.putExtra("Store_lng", clientList.get(position).getStoreLng());// sending Store lng


        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView box;
        ImageView image;
        ImageView delete;
        TextView client, phone, location, number;
        LinearLayout accept;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            image = itemView.findViewById(R.id.image);
            client = itemView.findViewById(R.id.client);
            phone = itemView.findViewById(R.id.phone);
            number = itemView.findViewById(R.id.number);
            location = itemView.findViewById(R.id.location);
            accept = itemView.findViewById(R.id.accept);
        }
    }
}
