package com.example.Activities.fruitix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.Activities.fruitix.Adapter.RecyclerViewAdapter;
import com.example.Activities.fruitix.Models.Fruits;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private final String JSON_URL = "https://gist.githubusercontent.com/Cozyzxc/ba0d6693e51077599c17ec12e854c2e6/raw/515df2ba11fa99e9eb609504e7c81ad30747cf03/final%2520fruit%2520disease3";
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private List<Fruits> lstFruits;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle("Fruit Information");
        lstFruits = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        jsonrequest();



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setSelectedItemId(R.id.info);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.info:
                        return true;
                    case R.id.community:
                        startActivity(new Intent(getApplicationContext(), CommunityActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }

        });


    }

    private void jsonrequest() {
        request = new JsonArrayRequest(JSON_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Fruits fruits = new Fruits();
                        fruits.setFruit(jsonObject.getString("fruit"));
                        fruits.setFruit_disease_name(jsonObject.getString("fruit disease name"));
                        fruits.setDescription(jsonObject.getString("description"));
                        fruits.setSymptoms(jsonObject.getString("symptoms"));
                        fruits.setImg(jsonObject.getString("img"));
                        fruits.setWhat_to_do(jsonObject.getString("what to do"));
                        fruits.setBotanical_name(jsonObject.getString("botanical name"));
                        fruits.setCause(jsonObject.getString("cause"));

                        lstFruits.add(fruits);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setuprecyclerview(lstFruits);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(InfoActivity.this);
        requestQueue.add(request);
    }

    private void setuprecyclerview(List<Fruits> lstFruits) {
        RecyclerViewAdapter myadapter = new RecyclerViewAdapter(this,lstFruits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(myadapter);
    }
}