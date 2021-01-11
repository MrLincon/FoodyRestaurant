package com.idk.foodyrestaurant.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.idk.foodyrestaurant.R;

public class MyPostAdapter extends FirestorePagingAdapter<MyPosts, MyPostAdapter.MyPostHolder> {

    private OnItemClickListener listener;
    private Context mContext;
    SwipeRefreshLayout mswipeRefreshLayout;

    public MyPostAdapter(@NonNull FirestorePagingOptions<MyPosts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyPostHolder holder, int position, @NonNull MyPosts model) {
        holder.Name.setText(model.getName());
        holder.Restaurant.setText("@"+model.getRestaurant());
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state) {

            case LOADING_INITIAL:
//                mswipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log (Profile)", "Loading Initial data");
                break;
            case LOADING_MORE:
//                mswipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log (Profile)", "Loading next page");
                break;
            case FINISHED:
//                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log (Profile)", "All data loaded");
                break;
            case LOADED:
//                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log (Profile)", "Total data loaded "+getItemCount());
                break;
            case ERROR:
//                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log (Profile)", "Error loading data");
                break;
        }
    }

    @NonNull
    @Override
    public MyPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_layout,
                parent, false);

        return new MyPostHolder(view);
    }

    class MyPostHolder extends RecyclerView.ViewHolder {
        TextView Name,Restaurant;
        public MyPostHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Restaurant = itemView.findViewById(R.id.restaurant);

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
