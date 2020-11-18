package com.oufar.emsd.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oufar.emsd.Database.DB;
import com.oufar.emsd.Model.Menu;
import com.oufar.emsd.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter_Scan extends RecyclerView.Adapter<RecyclerViewAdapter_Scan.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter_Scan";

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private String key;

    private DB db;

    private int i = 0;

    private HashMap<String, String> hashMap;


    private ArrayList<Menu> mNames = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter_Scan(Context context, ArrayList<Menu> names) {
        mNames = names;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_menu, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");


        reference = FirebaseDatabase.getInstance().getReference("Orders").child(mNames.get(position).getClientId());

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        Glide.with(mContext)
                .asBitmap()
                .load(mNames.get(position).getImageURL())
                .into(holder.image);

        holder.plate.setText(mNames.get(position).getPlate());
        holder.price.setText(mNames.get(position).getPrice());
        holder.description.setText(mNames.get(position).getDescription());
        holder.counter_txt.setText(mNames.get(position).getNumber());

        holder.finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("This plate ("+mNames.get(position).getPlate()+") is delivered ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String id = mNames.get(position).getId()+"";

                                reference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                        }else {

                                            Toast.makeText(mContext, "click again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image;
        TextView plate, price, description, counter_txt;
        LinearLayout counter_, finish;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            image = itemView.findViewById(R.id.image);
            plate = itemView.findViewById(R.id.plate);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            finish = itemView.findViewById(R.id.finish);
            counter_ = itemView.findViewById(R.id.counter);
            counter_txt = itemView.findViewById(R.id.counter_txt);
        }
    }
}

