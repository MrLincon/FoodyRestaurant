package com.idk.foodyrestaurant.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.idk.foodyrestaurant.R;

public class RestaurantAdapter extends FirestorePagingAdapter<Restaurant, RestaurantAdapter.RestaurantHolder> {

    private OnItemClickListener listener;
    private Context mContext;
    SwipeRefreshLayout mswipeRefreshLayout;

    public RestaurantAdapter(@NonNull FirestorePagingOptions<Restaurant> options, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        this.mswipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantHolder holder, int position, @NonNull Restaurant model) {
        holder.Name.setText(model.getName());
        holder.Location.setText(model.getLocation());
        holder.Day.setText(model.getDay());
        holder.Time.setText(model.getTime());
        Glide.with(mContext).load(model.getUserImageUrl()).into(holder.UserImage);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state) {

            case LOADING_INITIAL:
                mswipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log", "Loading Initial data");
                break;
            case LOADING_MORE:
                mswipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log", "Loading next page");
                break;
            case FINISHED:
                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "All data loaded");
                break;
            case LOADED:
                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "Total data loaded "+getItemCount());
                break;
            case ERROR:
                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "Error loading data");
                break;
        }
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_layout,
                parent, false);

        return new RestaurantHolder(view);
    }

    class RestaurantHolder extends RecyclerView.ViewHolder {
        TextView Name, Location, Day, Time;
        ImageView UserImage;
        public RestaurantHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Location = itemView.findViewById(R.id.location);
            UserImage = itemView.findViewById(R.id.profile);
            Day = itemView.findViewById(R.id.day_opened);
            Time = itemView.findViewById(R.id.time_opened);

            mContext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
