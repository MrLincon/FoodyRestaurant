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

public class CommentAdapter extends FirestorePagingAdapter<Comment, CommentAdapter.CommentHolder> {

    private OnItemClickListener listener;
    private Context mContext;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CommentAdapter(@NonNull FirestorePagingOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CommentHolder holder, int position, @NonNull Comment model) {
        final String post_id = getItem(position).getId();
        final String user_id = firebaseAuth.getCurrentUser().getUid();

        holder.Name.setText(model.getName());
        holder.Comment.setText(model.getComment());
        Glide.with(mContext).load(model.getUserImageUrl()).into(holder.User_Image);

        //Like Features
        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("post_id", post_id);
                Log.d("user_id", user_id);
                firebaseFirestore.collection("Comments").document(post_id).collection("Likes").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likes = new HashMap<>();
                            likes.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Comments").document(post_id).collection("Likes").document(user_id).set(likes);

                        } else {

                            firebaseFirestore.collection("Comments").document(post_id).collection("Likes").document(user_id).delete();

                        }
                    }
                });

            }
        });

        //Update Like icon
        firebaseFirestore.collection("Comments").document(post_id).collection("Likes").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.Like.setImageResource(R.drawable.ic_like_selected);

                } else {

                    holder.Like.setImageResource(R.drawable.ic_like);

                }

            }
        });

        //Get Likes Count
        firebaseFirestore.collection("Comments").document(post_id).collection("Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
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
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,
                parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        return new CommentHolder(view);
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        TextView Name,Comment, Like_count;
        ImageView Like, User_Image;
        public CommentHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Comment = itemView.findViewById(R.id.comment);
            User_Image = itemView.findViewById(R.id.profile);
            Like = itemView.findViewById(R.id.like);
            Like_count = itemView.findViewById(R.id.like_count);

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
