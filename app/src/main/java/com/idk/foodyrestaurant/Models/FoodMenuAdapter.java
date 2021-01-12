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

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.idk.foodyrestaurant.R;

import java.util.HashMap;
import java.util.Map;

public class FoodMenuAdapter extends FirestorePagingAdapter<FoodMenu, FoodMenuAdapter.FoodMenuHolder> {

    private OnItemClickListener listener;
    private Context mContext;
    SwipeRefreshLayout mswipeRefreshLayout;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public FoodMenuAdapter(@NonNull FirestorePagingOptions<FoodMenu> options, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        this.mswipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull final FoodMenuHolder holder, int position, @NonNull FoodMenu model) {
        final String post_id = getItem(position).getId();
        final String user_id = firebaseAuth.getCurrentUser().getUid();

        holder.Name.setText(model.getName());
        holder.Price.setText("TK: "+model.getPrice());
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
                Log.d("Paging Log", "Total data loaded " + getItemCount());
                break;
            case ERROR:
                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "Error loading data");
                break;
        }
    }

    @NonNull
    @Override
    public FoodMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_layout,
                parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new FoodMenuHolder(view);
    }

    class FoodMenuHolder extends RecyclerView.ViewHolder {
        TextView Name, Price;

        public FoodMenuHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.food_name);
            Price = itemView.findViewById(R.id.food_price);

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
