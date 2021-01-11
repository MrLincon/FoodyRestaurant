package com.idk.foodyrestaurant.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.idk.foodyrestaurant.Models.Comment;
import com.idk.foodyrestaurant.Models.CommentAdapter;
import com.idk.foodyrestaurant.Models.FeedRecyclerDecoration;
import com.idk.foodyrestaurant.Models.MyPostAdapter;
import com.idk.foodyrestaurant.Models.MyPosts;
import com.idk.foodyrestaurant.R;
import com.idk.foodyrestaurant.Tabs.FragmentFeed;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AppBarLayout appBarLayout;
    private ImageView back, edit, delete;
    private TextView name, restaurant, details;
    private String Details;
    private String ID, userID;

    private RecyclerView recyclerView;

    private EditText comment;
    private ImageView send;

    Dialog editPost;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference document_reference, document_ref, doc_ref;

    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        recyclerView = findViewById(R.id.comment_recyclerview);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerView.addItemDecoration(new FeedRecyclerDecoration(topPadding, bottomPadding));

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        appBarLayout = findViewById(R.id.appBarLayout);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        name = findViewById(R.id.tv_name);
        restaurant = findViewById(R.id.tv_restaurant);
        details = findViewById(R.id.tv_details);

        comment = findViewById(R.id.comment);
        send = findViewById(R.id.send);

        editPost = new Dialog(this);

        final Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        ID = intent.getStringExtra(FragmentFeed.EXTRA_ID);

        db = FirebaseFirestore.getInstance();
        document_reference = db.collection("Feed").document(ID);
        document_ref = db.collection("Comments").document();
        doc_ref = db.collection("RestaurantDetails").document(userID);

        loadData();
        loadComments();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userComment();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost.setContentView(R.layout.popup_edit_post);
                editPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final EditText et_details = editPost.findViewById(R.id.et_details);
                et_details.setText(Details);
                LinearLayout save = editPost.findViewById(R.id.save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        document_reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        document_reference.update("details", et_details.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(DetailsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                                editPost.dismiss();
                                                loadData();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
                LinearLayout cancel = editPost.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editPost.dismiss();
                    }
                });

                editPost.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                builder.setTitle("Are you sure?")
                        .setMessage("If you delete this, this post will no longer be shown in the feed!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                document_reference.delete();
                                finish();
                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    toolbarTitle.setVisibility(View.VISIBLE);
                } else if (verticalOffset == 0) {
                    toolbarTitle.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadComments() {
        CollectionReference comments = db.collection("Comments");

        Query query = comments.whereEqualTo("post_id", ID);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<Comment> options = new FirestorePagingOptions.Builder<Comment>()
                .setQuery(query, config, Comment.class)
                .build();

        adapter = new CommentAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private void userComment() {
        final String id = document_ref.getId();
        String Comment = comment.getText().toString().trim();


        doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    String name = documentSnapshot.getString("name");

                    Map<String, Object> userMap = new HashMap<>();

                    userMap.put("name", name);
                    userMap.put("comment", Comment);
                    userMap.put("user_id", userID);
                    userMap.put("post_id", ID);
                    userMap.put("id", id);
                    userMap.put("timestamp", FieldValue.serverTimestamp());
                    document_ref.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Log", "ID: " + id);
                            comment.setText("");
                            Toast.makeText(DetailsActivity.this, "Adding..", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    String Name = documentSnapshot.getString("name");
                    String Restaurant = documentSnapshot.getString("restaurant");
                    Details = documentSnapshot.getString("details");
                    String User_ID = documentSnapshot.getString("user_id");

                    if (User_ID.equals(userID)) {
                        delete.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                    } else {
                        delete.setVisibility(View.GONE);
                        edit.setVisibility(View.GONE);
                    }

                    name.setText(Name);
                    restaurant.setText("@" + Restaurant);
                    details.setText(Details);

                    toolbarTitle.setText("@" + Restaurant);

                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}