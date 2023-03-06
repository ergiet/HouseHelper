package com.example.househelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.househelper.databinding.ActivityAddShoppingListBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class AddShoppingList extends Drawer {

    ActivityAddShoppingListBinding addShoppingListBinding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addShoppingListBinding = ActivityAddShoppingListBinding.inflate(getLayoutInflater());
        setContentView(addShoppingListBinding.getRoot());
        allocateActivityTitle("");

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userid_value = preferences.getString("userid", "");
//        TextView tvwelcome =(TextView) findViewById(R.id.welcome);
//        tvwelcome.setText(getString(R.string.hello) + " " + username_value);

        boolean[] isPrivate = {false};
        CheckBox checkBox = findViewById(R.id.private_shopping_list_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPrivate[0] = isChecked;
            }
        });

        TextView tvproduct =(TextView) findViewById(R.id.shopping_list_product);
        TextView tvname =(TextView) findViewById(R.id.shopping_list_name);
        final String[] shopping_list_id = {""};
        MaterialButton add_shopping_list_product_btn =(MaterialButton) findViewById(R.id.add_shopping_list_product_btn);
        add_shopping_list_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String product = tvproduct.getText().toString();
                String name = tvname.getText().toString();

                if(!product.equals("")){
                    if(shopping_list_id[0] != null && shopping_list_id[0].equals("")){
                        //Create new shopping list
                        checkBox.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Create shopping list.", Toast.LENGTH_SHORT).show();
                        Map<String, Object> product_data = new HashMap<>();
                        product_data.put("is_bought", false);

                        Map<String, Object> prepared_product = new HashMap<>();
                        prepared_product.put(product, product_data);

                        Map<String, Object> shopping_list_data = new HashMap<>();
                        shopping_list_data.put("name", name);
                        shopping_list_data.put("date", FieldValue.serverTimestamp());
                        shopping_list_data.put("is_private", isPrivate[0]);
                        shopping_list_data.put("products", prepared_product);
                        shopping_list_data.put("user", userid_value);

                        db.collection("shopping_lists")
                                .add(shopping_list_data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        shopping_list_id[0] = documentReference.getId();
                                        Toast.makeText(getApplicationContext(), "Dodano produkt.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Something failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        //Edit existing shopping_list
                        Map<String, Object> product_data = new HashMap<>();
                        product_data.put("is_bought", false);

                        Map<String, Object> prepared_product = new HashMap<>();
                        prepared_product.put(product, product_data);

                        Map<String, Object> prepared_product_in_map = new HashMap<>();
                        prepared_product_in_map.put("products", prepared_product);

                        db.collection("shopping_lists").document(shopping_list_id[0])
                                .set(prepared_product_in_map, SetOptions.merge());
                    }
                }else{
                    //If product field is left empty
                    Toast.makeText(getApplicationContext(), "Product field can't be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}