package com.azinecllc.champy.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.azinecllc.champy.R;
import com.azinecllc.champy.adapter.CardDetailAdapter;
import com.azinecllc.champy.model.SectionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor SashaKhyzhun
 * Created on 3/21/17.
 */

public class CreateChallengeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardDetailAdapter mainAdapter;
    private List<SectionModel> sections;
    private List<Integer> items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);
        // Layout Slider
        recyclerView = (RecyclerView) findViewById(R.id.main_rv);
        initItems();
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initItems() {
        SectionModel sectionModel1 = new SectionModel();
        sectionModel1.setLabel("First Section");
        items = new ArrayList<>();
        items.add(1);
        sectionModel1.setItems(items);
//        items.clear();

        SectionModel sectionModel2 = new SectionModel();
        sectionModel2.setLabel("Second Section");
        items = new ArrayList<>();
        items.add(2);
        items.add(3);
        items.add(4);
        sectionModel2.setItems(items);
//        items.clear();

        SectionModel sectionModel3 = new SectionModel();
        sectionModel3.setLabel("Third Section");
        items = new ArrayList<>();
        items.add(5);
        items.add(6);
        items.add(7);
        items.add(8);
        items.add(9);
        items.add(10);
        items.add(11);
        sectionModel3.setItems(items);
//        items.clear();

        SectionModel sectionModel4 = new SectionModel();
        sectionModel4.setLabel("Fourth Section");
        items = new ArrayList<>();
        items.add(12);
        items.add(13);
        items.add(14);
        items.add(15);
        items.add(16);
        items.add(17);
        items.add(18);
        items.add(19);
        items.add(20);
        items.add(21);
        sectionModel4.setItems(items);
//        items.clear();

        sections = new ArrayList<>();
        sections.add(sectionModel1);
        sections.add(sectionModel2);
        sections.add(sectionModel3);
        sections.add(sectionModel4);
    }

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mainAdapter = new CardDetailAdapter(this, sections);
        recyclerView.setAdapter(mainAdapter);
    }

}
