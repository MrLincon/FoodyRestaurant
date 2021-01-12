package com.idk.foodyrestaurant.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.idk.foodyrestaurant.Models.Feed;
import com.idk.foodyrestaurant.Models.FeedRecyclerDecoration;
import com.idk.foodyrestaurant.Models.MyPostAdapter;
import com.idk.foodyrestaurant.Models.MyPosts;
import com.idk.foodyrestaurant.R;
import com.idk.foodyrestaurant.Tabs.FragmentFeed;
import com.idk.foodyrestaurant.Tabs.FragmentRestaurants;


public class ProfileActivityView extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView toolbarTitle;
    private ImageView close;
    private RecyclerView recyclerView;

    private TextView name,email, time, day, foodMenu;

    private String ID, userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference document_reference;

    private MyPostAdapter adapter;

    public static final String EXTRA_ID_POST = "com.example.foody.EXTRA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbarTitle = findViewById(R.id.toolbar_title);
        close = findViewById(R.id.close);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        time = findViewById(R.id.opened_times);
        day = findViewById(R.id.opened_days);
        foodMenu = findViewById(R.id.food_menu);

        recyclerView = findViewById(R.id.my_posts_recyclerview);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerView.addItemDecoration(new FeedRecyclerDecoration(topPadding, bottomPadding));

//        For Action Bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Intent intent = getIntent();
        ID = intent.getStringExtra(FragmentRestaurants.EXTRA_ID);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        document_reference = db.collection("RestaurantDetails").document(ID);

        foodMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foodMenu = new Intent(ProfileActivityView.this,MenuActivityView.class);
                startActivity(foodMenu);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

                    toolbarTitle.setText(Name);
                    name.setText(Name);
                    email.setText(Email);
                    time.setText(Time);
                    day.setText(Day);

                } else {
                    Toast.makeText(ProfileActivityView.this, "Something wrong!", Toast.LENGTH_LONG).show();
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

    }

    private void loadMyPosts() {

        CollectionReference myPosts = db.collection("Feed");

        Query query = myPosts.whereEqualTo("user_id",ID)
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

                Intent intent = new Intent(ProfileActivityView.this, DetailsActivity.class);

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

}