package com.mumuwoyou.mycount.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mumuwoyou.mycount.app.adapter.PlaceAdapter;
import com.mumuwoyou.mycount.app.adapter.StockAdapter;
import com.mumuwoyou.mycount.app.dbmodel.DetailModel;
import com.mumuwoyou.mycount.app.dbmodel.PlaceModel;
import com.mumuwoyou.mycount.app.dbmodel.StockModel;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class PlaceActivity extends AppCompatActivity {

    private EditText place_value;
    private ListView lv_place;
    private PlaceAdapter adapter;
    private ArrayList<PlaceModel> placeModels;

    private PlaceModel place_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        lv_place = findViewById(R.id.lv_place);
        placeModels = new ArrayList<>();
        adapter = new PlaceAdapter(placeModels);
        lv_place.setAdapter(adapter);

        Button addButton = (Button)findViewById(R.id.add);
        Button deleteButton = (Button)findViewById(R.id.delete);
        place_value = (EditText)findViewById(R.id.place_value);
        addButton.setOnClickListener(ButtonListener);
        deleteButton.setOnClickListener(ButtonListener);

        lv_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for(int i=0;i<parent.getCount();i++) {
                    View v = parent.getChildAt(i);
                    if (position == i) {
                        v.setBackgroundResource(R.color.yellow);//点击选择变色
                        place_select = (PlaceModel)parent.getAdapter().getItem(position);
                    }
                    else {
                        v.setBackgroundResource(R.color.white);
                    }
                }
            }
        });

        refreshData();
    }

    private void refreshData() {
        List<PlaceModel> allplace = LitePal.findAll(PlaceModel.class);

        if (allplace.size() > 0) {
            placeModels.clear();
            placeModels.addAll(allplace);
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }

    }

    Button.OnClickListener ButtonListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.add:
                    PlaceModel place = new PlaceModel();
                    place.setPlace(place_value.getText().toString());
                    place.save();
                    refreshData();
                    break;
                case R.id.delete:   //删除选择的数据。
                    if (place_select !=null) {
                        place_select.delete();
                    }
                    refreshData();
                    break;

            }

        }
    };
}
