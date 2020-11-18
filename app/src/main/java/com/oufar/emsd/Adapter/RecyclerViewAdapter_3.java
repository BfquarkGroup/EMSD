package com.oufar.emsd.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.client.authentication.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oufar.emsd.Fragment_4;
import com.oufar.emsd.Fragments.Fragment_2;
import com.oufar.emsd.Model.Order;
import com.oufar.emsd.R;
import com.oufar.emsd.Scan;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter_3 extends RecyclerView.Adapter<RecyclerViewAdapter_3.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter_3";

    /*private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;*/

    //private DB db;

    //vars
    private ArrayList<Order> mNames = new ArrayList<>();
    private Context mContext;

    private String storeId_;
    private String clientId_;

    public RecyclerViewAdapter_3(Context context, ArrayList<Order> names) {
        mNames = names;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_selected_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //auth = FirebaseAuth.getInstance();
        //firebaseUser = auth.getCurrentUser();
        //reference = FirebaseDatabase.getInstance().getReference("Orders").child(firebaseUser.getUid());


        storeId_ = mNames.get(position).getStoreId();
        clientId_ = mNames.get(position).getClientId();

        Glide.with(mContext)
                .asBitmap()
                .load(mNames.get(position).getClientImageURL())
                .into(holder.image);


        //db = new DB(mContext);

        holder.client.setText(mNames.get(position).getClientName());
        holder.phone.setText(mNames.get(position).getClientPhone());
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("You want to call this customer (" + mNames.get(position).getClientName() + ") ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(mNames.get(position).getClientPhone())));
                                mContext.startActivity(call);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });
        holder.store.setText(mNames.get(position).getStoreName());
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, mNames.get(position).getStoreName()+"\n"+mNames.get(position).getLat()+"\n"+
                                              mNames.get(position).getLng(), Toast.LENGTH_SHORT).show();

            }
        });
        holder.regret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("Cancel delivery process ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");

                                reference.child(mNames.get(position).getClientId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                                            String storeId = snapshot.child("storeId").getValue().toString();
                                            String delivery = snapshot.child("delivery").getValue().toString();

                                            if (delivery.equals(firebaseUser.getUid())){

                                                Toast.makeText(mContext, "regret", Toast.LENGTH_SHORT).show();

                                                String id = snapshot.child("id").getValue().toString();

                                                DatabaseReference reference_ = FirebaseDatabase.getInstance().getReference("Orders");

                                                reference_.child(mNames.get(position).getClientId()).child(id).child("delivery").setValue("on hold");
                                                reference_.child(mNames.get(position).getClientId()).child(id).child("storeName").setValue("on hold");

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });
        holder.finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(mContext, "nothing yet", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, Scan.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Client_id", mNames.get(position).getId());// sending Client id
                intent.putExtra("Client_name", mNames.get(position).getClientName());// sending Client name
                intent.putExtra("Client_image", mNames.get(position).getClientImageURL());// sending Client image
                mContext.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image, clock;
        TextView client, phone, location, store;
        LinearLayout regret, finish;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            client = itemView.findViewById(R.id.client);
            phone = itemView.findViewById(R.id.phone);
            store = itemView.findViewById(R.id.store);
            location = itemView.findViewById(R.id.location);
            regret = itemView.findViewById(R.id.regret);
            finish = itemView.findViewById(R.id.finish);
        }
    }
}