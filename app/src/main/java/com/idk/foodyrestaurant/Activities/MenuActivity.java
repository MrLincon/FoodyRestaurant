package com.idk.foodyrestaurant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.idk.foodyrestaurant.Models.Feed;
import com.idk.foodyrestaurant.Models.FeedAdapter;
import com.idk.foodyrestaurant.Models.FoodMenu;
import com.idk.foodyrestaurant.Models.FoodMenuAdapter;
import com.idk.foodyrestaurant.R;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView back;
    private RecyclerView menuRecyclerview;
    private FloatingActionButton menuFab;
    SwipeRefreshLayout swipeRefreshLayout;

    Dialog popup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference document_reference, doc_ref;
    private String userID;

    private FoodMenuAdapter adapter;
    private CollectionReference menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = findViewById(R.id.toolbar_simple);
        toolbarTitle = findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        menuRecyclerview = findViewById(R.id.menu_recyclerview);
        menuFab = findViewById(R.id.menu_fab);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        toolbarTitle.setText("Menu");

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();

        menu = db.collection("RestaurantDetails").document(userID).collection("Menu");

        //Load food menu
        loadData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        popup = new Dialog(this);

        menuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setContentView(R.layout.add_menu_popup);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView save = popup.findViewById(R.id.save);
                ImageView close = popup.findViewById(R.id.close_popup);
                final EditText food_name = popup.findViewById(R.id.food_name);
                final EditText food_price = popup.findViewById(R.id.food_price);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        doc_ref = db.collection("RestaurantDetails").document(userID).collection("Menu").document();

                        String foodName = food_name.getText().toString().trim();
                        String foodPrice = food_price.getText().toString().trim();

                        if (!foodName.isEmpty() && !foodPrice.isEmpty()) {

                            final String id = doc_ref.getId();
                            Toast.makeText(MenuActivity.this, id, Toast.LENGTH_SHORT).show();
                            Map<String, Object> userMap = new HashMap<>();

                            userMap.put("name", foodName);
                            userMap.put("price", "TK: " + foodPrice);
                            userMap.put("user_id", userID);
                            userMap.put("id", id);
                            userMap.put("timestamp", FieldValue.serverTimestamp());
                            doc_ref.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MenuActivity.this, "Adding..", Toast.LENGTH_LONG).show();
                                    popup.dismiss();
                                    adapter.refresh();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(MenuActivity.this, "You must fill all the fields!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                popup.show();
            }
        });


    }

    private void loadData() {
        Query query = menu.orderBy("timestamp", Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<FoodMenu> options = new FirestorePagingOptions.Builder<FoodMenu>()
                .setQuery(query, config, FoodMenu.class)
                .build();

        adapter = new FoodMenuAdapter(options, swipeRefreshLayout);
        menuRecyclerview.setHasFixedSize(true);
        menuRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        menuRecyclerview.setAdapter(adapter);
        adapter.startListening();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });

        adapter.setOnItemClickListener(new FoodMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                FoodMenu foodMenu = documentSnapshot.toObject(FoodMenu.class);
                String Name = foodMenu.getName();
                String Price = foodMenu.getPrice();
                String id = documentSnapshot.getId();

                popup.setContentView(R.layout.add_menu_popup);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ImageView save = popup.findViewById(R.id.save);
                ImageView close = popup.findViewById(R.id.close_popup);
                EditText food_name = popup.findViewById(R.id.food_name);
                EditText food_price = popup.findViewById(R.id.food_price);

                food_name.setText(Name);
                food_price.setText(Price);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = food_name.getText().toString().trim();
                        String price = food_price.getText().toString().trim();

                        document_reference = db.collection("RestaurantDetails").document(userID).collection("Menu").document(id);

                        Map<String, Object> userMap = new HashMap<>();

                        userMap.put("name", name);
                        userMap.put("price", price);
                        document_reference.update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MenuActivity.this, "Updating..", Toast.LENGTH_LONG).show();
                                popup.dismiss();
                                adapter.refresh();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                popup.show();

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}