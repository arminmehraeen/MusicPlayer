package com.armin.mehraein.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MusicList extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView ;
    ImageView refresh ;
    ArrayList<Music> arrayList = new ArrayList<>();
    RecycleAdapter recycleAdapter ;
    TextView txt_load ;
    String URl = "https://api.mocki.io/v1/34fbe09b" ;
    static final Integer WRITE_EXST = 1212;
    static final Integer READ_EXST = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getInformation();
        refresh.setOnClickListener(this);
        if (ActivityCompat.checkSelfPermission(this
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXST){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "PERMISSION_DENAY", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycleView);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //LinearLayoutManager horizontalLayoutManager
        //        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        refresh = findViewById(R.id.refresh);
        recycleAdapter = new RecycleAdapter(arrayList,this);
        recyclerView.setAdapter(recycleAdapter);
        txt_load = findViewById(R.id.txt_load);
    }

    private void getInformation(){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URl
                , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArrayRequest = response.getJSONArray("data");
                    for (int i=0 ; i<jsonArrayRequest.length() ; i++){
                        JSONObject object = jsonArrayRequest.getJSONObject(i);
                        int id = object.getInt("id");
                        String name = object.getString("name");
                        String artist = object.getString("artist");
                        String picture = object.getString("picture");
                        String link = object.getString("link");
                        Music music = new Music();
                        music.setId(id);
                        music.setName(name);
                        music.setPicture(picture);
                        music.setArtist(artist);
                        music.setLink(link);
                        arrayList.add(music);
                    }
                    txt_load.setVisibility(View.GONE);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refresh){
            refresh.animate().rotationBy(360).setDuration(2000);
            arrayList.clear();
            getInformation();
        }
    }

}
