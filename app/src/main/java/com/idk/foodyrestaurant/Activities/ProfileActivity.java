package com.idk.foodyrestaurant.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.idk.foodyrestaurant.Models.Feed;
import com.idk.foodyrestaurant.Models.FeedRecyclerDecoration;
import com.idk.foodyrestaurant.Models.MyPostAdapter;
import com.idk.foodyrestaurant.Models.MyPosts;
import com.idk.foodyrestaurant.R;


public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView toolbarTitle, save;
    private ImageView close, profile;
    private CardView editProfile;
    private RecyclerView recyclerView;

    private TextView name, email, time, day, res_menu;

    private FirebaseAuth mAuth;
    private String userID;

    private FirebaseFirestore db;
    private DocumentReference document_reference;

    private MyPostAdapter adapter;

    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    public static final String EXTRA_ID_POST = "com.example.foody.EXTRA_ID";
    public static final String EXTRA_ID = "com.example.foody.EXTRA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbarTitle = findViewById(R.id.toolbar_title);
        save = findViewById(R.id.save);
        close = findViewById(R.id.close);
        profile =  findViewById(R.id.profile);
        editProfile = findViewById(R.id.edit_profile);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        time = findViewById(R.id.opened_times);
        day = findViewById(R.id.opened_days);

        res_menu = findViewById(R.id.res_menu);

        recyclerView = findViewById(R.id.my_posts_recyclerview);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerView.addItemDecoration(new FeedRecyclerDecoration(topPadding, bottomPadding));

//        For Action Bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();
        document_reference = db.collection("RestaurantDetails").document(userID);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        res_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(ProfileActivity.this, MenuActivity.class);
                startActivity(menuIntent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_profile = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(edit_profile);
            }
        });


        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    String Name = documentSnapshot.getString("name");
                    String Email = documentSnapshot.getString("email");
                    String Time = documentSnapshot.getString("time");
                    String Day = documentSnapshot.getString("day");
                    String UserImageUrl = documentSnapshot.getString("userImageUrl");

                    toolbarTitle.setText(Name);
                    name.setText(Name);
                    email.setText(Email);
                    time.setText(Time);
                    day.setText(Day);
                    Glide.with(getApplicationContext()).load(UserImageUrl).into(profile);

                } else {
                    Toast.makeText(ProfileActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        loadMyPosts();


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


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        save.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(profile);
        }
    }

    private void uploadFile() {
        save.setVisibility(View.INVISIBLE);
        if (mImageUri != null) {
            final String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            final StorageReference fileReference = mStorageRef.child(currentTimeMillis);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                            mStorageRef.child(currentTimeMillis).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String img_url = String.valueOf(uri);

                                    document_reference.update("userImageUrl", img_url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, " e.getMessage()", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMyPosts() {

        CollectionReference myPosts = db.collection("Feed");

        Query query = myPosts.whereEqualTo("user_id", userID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<MyPosts> options = new FirestorePagingOptions.Builder<MyPosts>()
                .setQuery(query, config, MyPosts.class)
                .build();

        adapter = new MyPostAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        adapter.setOnItemClickListener(new MyPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                Feed feed = documentSnapshot.toObject(Feed.class);
                String id = documentSnapshot.getId();

                Intent intent = new Intent(ProfileActivity.this, DetailsActivity.class);

                String name = feed.getName();
//                String restaurant = feed.getRestaurant();
//                String details = feed.getDetails();

                intent.putExtra(EXTRA_ID_POST, id);
//                intent.putExtra(EXTRA_NAME, name);
//                intent.putExtra(EXTRA_RESTAURANT, name);
//                intent.putExtra(EXTRA_DETAILS, details);

                startActivity(intent);
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

    @Override
    public void finish() {
        super.finish();
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(i);
    }
}