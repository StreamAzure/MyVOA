package com.stream.myvoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stream.myvoa.bean.LRCObject;
import com.stream.myvoa.bean.VOAObject;
import com.stream.myvoa.utils.LRCLoader;
import com.stream.myvoa.utils.MySQLHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter mAdapter;
    private List<VOAObject> mVoaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ItemAdapter(mVoaList);
        recyclerView.setAdapter(mAdapter);

    }

    public void loadData(){
        try {
//            MySQLHelper.test();
            this.mVoaList = MySQLHelper.getVOAList();
        }catch (SQLException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final List<VOAObject> voaList;

        public ItemAdapter(List<VOAObject> voaList) {
            this.voaList = voaList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;
            switch (viewType) {
                default:
                    itemView = LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.item_layout, parent, false);
            }
            return new ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ItemHolder viewHolder = (ItemHolder) holder;
            VOAObject voaObject = voaList.get(position);
            viewHolder.title.setText(voaObject.getTitle());

            viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                    intent.putExtra("voa", (Parcelable) voaObject);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.voaList.size();
        }

        class ItemHolder extends RecyclerView.ViewHolder{
            View rootView;
            TextView title;
            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                rootView = itemView.findViewById(R.id.item_view);
                title = itemView.findViewById(R.id.item_tv_title);
            }
        }
    }
}