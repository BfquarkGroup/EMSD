package com.oufar.emsd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oufar.emsd.ListOfStore;
import com.oufar.emsd.ListOfStoreWithMaps;
import com.oufar.emsd.Model.Store;
import com.oufar.emsd.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private ArrayList<Store> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<Store> names, ArrayList<String> imageUrls) {
        mNames = names;
        mImageUrls = imageUrls;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mNames.get(position).getImageURL())
                .into(holder.image);

        if (mNames.get(position).getStatus().equals("open")){

            holder.close.setVisibility(View.GONE);
            holder.open.setVisibility(View.VISIBLE);
        }else if (mNames.get(position).getStatus().equals("close")){

            holder.open.setVisibility(View.GONE);
            holder.close.setVisibility(View.VISIBLE);

        }

        final Intent intent = new Intent(mContext, ListOfStoreWithMaps.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Store_id", mNames.get(position).getId());// sending Store id
        intent.putExtra("Store_name", mNames.get(position).getUsername());// sending Store name
        intent.putExtra("Store_image", mNames.get(position).getImageURL());// sending Store image
        intent.putExtra("Store_lat", mNames.get(position).getLatitude());// sending Store lat
        intent.putExtra("Store_lng", mNames.get(position).getLongitude());// sending Store lng
        intent.putExtra("Store_status", mNames.get(position).getStatus());// sending Store status

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(intent);
            }
        });

        holder.username.setText(mNames.get(position).getUsername());
        holder.phone.setText(mNames.get(position).getPhone());
        holder.address.setText(mNames.get(position).getAddress());
        holder.description.setText(mNames.get(position).getDescription());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image, chat;
        TextView username, phone, address, description;
        RelativeLayout relativeLayout;
        LinearLayout open, close;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            description = itemView.findViewById(R.id.description);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            open = itemView.findViewById(R.id.open);
            close = itemView.findViewById(R.id.close);
            chat = itemView.findViewById(R.id.chat);
        }
    }
}
