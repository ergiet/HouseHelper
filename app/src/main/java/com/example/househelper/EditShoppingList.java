package com.example.househelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.househelper.databinding.EditShoppingListBinding;
import com.example.househelper.databinding.ShoppingListsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class EditShoppingList extends Drawer {

    EditShoppingListBinding activityEditShoppingListBinding;

    ArrayList<ProductModel> productModels = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditShoppingListBinding = EditShoppingListBinding.inflate(getLayoutInflater());
        setContentView(activityEditShoppingListBinding.getRoot());
        allocateActivityTitle("");
        setupProductListModels();
        //Toast.makeText(getApplicationContext(), "Shopping list ID: " + getIntent().getStringExtra("shopping_list_id"), Toast.LENGTH_SHORT).show();
    }
    private void setupProductListModels(){
        productModels.clear();
        String shopping_list_id = getIntent().getStringExtra("shopping_list_id");
        TextView tvshopping_list_name =(TextView) findViewById(R.id.shopping_list_name);

        db.collection("shopping_lists").document(shopping_list_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String shopping_list_name = document.getString("name");
                                tvshopping_list_name.setText(shopping_list_name);

                                Map<String, Object> products = (Map<String, Object>) document.get("products");
                                for (Map.Entry<String, Object> entry : products.entrySet()) {
                                    String productName = entry.getKey();
                                    Map<String, Object> productDetails = (Map<String, Object>) entry.getValue();
                                    boolean isBought = (boolean) productDetails.get("is_bought");
                                    //Toast.makeText(getApplicationContext(), "Product: " + productName + " " + "Bought?: " + isBought, Toast.LENGTH_SHORT).show();
                                    productModels.add(new ProductModel(productName, isBought));
                                }
                                RecyclerView recyclerView = findViewById(R.id.shopping_list);
                                EditShoppingList_RecyclerViewAdapter adapterlist = new EditShoppingList_RecyclerViewAdapter(EditShoppingList.this, productModels);

                                adapterlist.setOnItemClickListener(new EditShoppingList_RecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(ProductModel product) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference shoppingListRef = db.collection("shopping_lists").document(shopping_list_id);
                                        shoppingListRef.update("products." + product.getProduct() + ".is_bought", !product.getIs_bought());
                                    }
                                });

                                recyclerView.setAdapter(adapterlist);
                                recyclerView.setLayoutManager(new LinearLayoutManager(EditShoppingList.this));
                            }
                        }
                    }
                });
    }
}