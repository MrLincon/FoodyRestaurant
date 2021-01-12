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

public class OfferFeedAdapter extends FirestorePagingAdapter<Feed, OfferFeedAdapter.FeedHolder> {

    private OnItemClickListener listener;
    private Context mContext;
    SwipeRefreshLayout mswipeRefreshLayout;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public OfferFeedAdapter(@NonNull FirestorePagingOptions<Feed> options, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        this.mswipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull final FeedHolder holder, int position, @NonNull Feed model) {
        final String post_id = getItem(position).getId();
        final String user_id = firebaseAuth.getCurrentUser().getUid();

        holder.Name.setText(model.getName());
        holder.Restaurant.setText("@"+model.getRestaurant());

        //Like Features
        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("post_id", post_id);
                firebaseFirestore.collection("Offer").document(post_id).collection("Likes").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likes = new HashMap<>();
                            likes.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Offer/" + post_id + "/Likes").document(user_id).set(likes);

                        } else {

                            firebaseFirestore.collection("Offer").document(post_id).collection("Likes").document(user_id).delete();

                        }
                    }
                });

            }
        });

        //Update Like icon
        firebaseFirestore.collection("Offer").document(post_id).collection("Likes").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.Like.setImageResource(R.drawable.ic_heart_selected);

                } else {

                    holder.Like.setImageResource(R.drawable.ic_heart);

                }

            }
        });

        //Get Likes Count
        firebaseFirestore.collection("Offer").document(post_id).collection("Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    String count = String.valueOf(documentSnapshots.size());
                    holder.Like_count.setText(count);
                } else {
                    holder.Like_count.setText("0");
                }
            }
        });

        //Get views Count
        firebaseFirestore.collection("Offer").document(post_id).collection("Views").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    String count = String.valueOf(documentSnapshots.size());
                    holder.Views_count.setText(count);
                } else {
                    holder.Views_count.setText("0");
                }
            }
        });

        //Get comments Count
        firebaseFirestore.collection("Offer").document(post_id).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    String count = String.valueOf(documentSnapshots.size());
                    holder.Comment_count.setText(count);
                } else {
                    holder.Comment_count.setText("0");
                }
            }
        });
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
    public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_layout,
                parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new FeedHolder(view);
    }

    class FeedHolder extends RecyclerView.ViewHolder {
        TextView Name,Restaurant, Like_count,Comment_count, Views_count;
        ImageView Like,Comment;
        public FeedHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Restaurant = itemView.findViewById(R.id.restaurant);
            Like = itemView.findViewById(R.id.like);
            Comment = itemView.findViewById(R.id.comment);
            Like_count = itemView.findViewById(R.id.like_count);
            Comment_count = itemView.findViewById(R.id.comments_count);
            Views_count = itemView.findViewById(R.id.views_count);

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
