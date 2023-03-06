package com.example.househelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househelper.databinding.ShoppingListsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;


public class ListShoppingLists extends Drawer {

    ShoppingListsBinding activityShoppingListsBinding;

    ArrayList<ShoppingListModel> shoppingListModels = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityShoppingListsBinding = ShoppingListsBinding.inflate(getLayoutInflater());
        setContentView(activityShoppingListsBinding.getRoot());
        allocateActivityTitle("");


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //startDateButton.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        Timestamp startTimestamp = new Timestamp(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        //endDateButton.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        Timestamp endTimestamp = new Timestamp(cal.getTime());

        setupShoppingListModels(startTimestamp, endTimestamp);
    }

    private void retrieveHouseholdUsers(String userid, final ListShoppingLists.Callback callback) {
        final ArrayList<String> arrayUsers = new ArrayList<>();
        db.collection("users").document(userid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String householdid = document.getString("household").toString();
                            db.collection("users")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if(!(document.getString("household") == null)){
                                                        if(householdid.equals(document.getString("household").toString())){
                                                            arrayUsers.add(document.getId());
                                                        }
                                                    }
                                                }
                                                callback.onResult(arrayUsers);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getUsername(String arrayUser, final ListShoppingLists.CallbackUsername callback) {
        db.collection("users").document(arrayUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String username = task.getResult().getString("username");
                            callback.onResult(username);
                        }
                    }
                });
    }



    interface Callback {
        void onResult(ArrayList<String> arrayUsers);
    }

    interface CallbackUsername {
        void onResult(String username);
    }

    private void setupShoppingListModels(final Timestamp startTimestamp, final Timestamp endTimestamp){
        shoppingListModels.clear();
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");

        retrieveHouseholdUsers(userid_value, new ListShoppingLists.Callback() {
            @Override
            public void onResult(ArrayList<String> arrayUsers) {
                db.collection("shopping_lists")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String user = document.getString("user");
                                        for (String arrayUser : arrayUsers) {
                                            if (arrayUser.equals(user)) {
                                                getUsername(user, new ListShoppingLists.CallbackUsername() {
                                                    @Override
                                                    public void onResult(String username) {
                                                        Timestamp time = document.getTimestamp("date");
                                                        Calendar cal = Calendar.getInstance();
                                                        cal.setTime(time.toDate());

                                                        Calendar current = Calendar.getInstance();
                                                        if (time.toDate().compareTo(startTimestamp.toDate()) >= 0 && time.toDate().compareTo(endTimestamp.toDate()) <= 0) {
                                                            shoppingListModels.add(new ShoppingListModel(username, document.getTimestamp("date"), document.getBoolean("is_private"), document.getString("name")));
                                                        }

                                                        RecyclerView recyclerView = findViewById(R.id.shopping_lists);


                                                        ShoppingList_RecyclerViewAdapter adapterlist = new ShoppingList_RecyclerViewAdapter(ListShoppingLists.this, shoppingListModels);
                                                        adapterlist.setOnItemClickListener(new ShoppingList_RecyclerViewAdapter.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(ShoppingListModel item) {
                                                                Intent intent = new Intent(getApplicationContext(), EditShoppingList.class);
                                                                intent.putExtra("shopping_list_id", document.getId());
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                        recyclerView.setAdapter(adapterlist);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(ListShoppingLists.this));
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }
}
