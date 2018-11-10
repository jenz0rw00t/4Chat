package com.example.grupp4.a4chat;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;

    private ArrayList<Drawable> drawableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();

        drawableList = new ArrayList<>();
        initiateDrawables();

        for (int i = 0; i < 25; i++) {
            ListItem listItem = new ListItem(
                    drawableList,
                    "Username",
                    "This is the message that needs to be heard by everyone! It's so important that no one have ever made a more important message in history, maybe ever."
            );
            listItems.add(listItem);
        }

        adapter = new Adapter(listItems,this);
        recyclerView.setAdapter(adapter);
    }

    private void initiateDrawables() {
        drawableList.add(getResources().getDrawable(R.drawable.avatar1));
        drawableList.add(getResources().getDrawable(R.drawable.avatar2));
        drawableList.add(getResources().getDrawable(R.drawable.avatar3));
        drawableList.add(getResources().getDrawable(R.drawable.avatar4));
    }
}
