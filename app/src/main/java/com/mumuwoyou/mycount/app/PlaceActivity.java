package com.mumuwoyou.mycount.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
        Button defaultButton = (Button)findViewById(R.id.setDefault);
        place_value = (EditText)findViewById(R.id.place_value);
        addButton.setOnClickListener(ButtonListener);
        deleteButton.setOnClickListener(ButtonListener);
        defaultButton.setOnClickListener(ButtonListener);

        lv_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for(int i=0;i<parent.getCount();i++) {
                    View v = parent.getChildAt(i);
                    if (position == i) {
                        v.setBackgroundResource(R.color.colorPrimary);//点击选择变色
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

    private Context getContext(){
        return this;
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
                    if (place_value.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(), "储位不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
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

                case R.id.setDefault: //设为默认。
                    List<PlaceModel> allplace = LitePal.findAll(PlaceModel.class);
                    if (place_select != null){
                        for(int i =0; i < allplace.size(); i++)
                        {
                            allplace.get(i).setIsdefault("");
                            allplace.get(i).save();
                        }
                        place_select.setIsdefault("默认");
                        place_select.save();

                    }
                    refreshData();
                    break;

            }

        }
    };
}
