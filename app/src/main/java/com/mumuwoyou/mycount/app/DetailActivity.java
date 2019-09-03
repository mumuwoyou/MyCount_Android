package com.mumuwoyou.mycount.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.king.zxing.CaptureActivity;
import com.mc0239.ComboBox;
import com.mc0239.ComboBoxAdapter;
import com.mumuwoyou.mycount.app.adapter.DetailAdapter;
import com.mumuwoyou.mycount.app.adapter.StockAdapter;
import com.mumuwoyou.mycount.app.dbmodel.DetailModel;
import com.mumuwoyou.mycount.app.dbmodel.GoodsModel;
import com.mumuwoyou.mycount.app.dbmodel.PlaceModel;
import com.mumuwoyou.mycount.app.dbmodel.StockModel;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private List<GoodsModel> goodsList;
    private List<StockModel> stockList;
    private List<PlaceModel> placeList;
    private List<DetailModel> detailList;
    private List<DetailModel> detail_have;

    private TextView sum_value;

    private ListView lv_detail;
    private ArrayList<DetailModel> detailModels;
    private DetailAdapter adapter;

    private DetailModel detail;
    private DetailModel detail_select;

    private AlertDialog.Builder builder;
    ComboBox comboBox;
    EditText countEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        Button addButton = (Button)findViewById(R.id.add);
//        Button deleteButton = (Button)findViewById(R.id.delete);
        countEdit = (EditText)findViewById(R.id.count_value);
//        addButton.setOnClickListener(ButtonListener);
//        deleteButton.setOnClickListener(ButtonListener);

        initData();
        initView();
    }
    //初始化数据
    private void initData() {
        Intent intent = getIntent();
        String code = intent.getStringExtra("code");
        goodsList = LitePal.where("code = ?",code).find(GoodsModel.class);
        stockList = LitePal.where("code = ?", code).find(StockModel.class);
        detailList = LitePal.where("stockmodel_id = ?", Integer.toString(stockList.get(0).getId())).find(DetailModel.class);
        placeList = LitePal.findAll(PlaceModel.class);
    }
    //初始化界面
    private  void initView()
    {   if (stockList.size() > 0) {
            StockModel stock = stockList.get(0);
            TextView code_value = findViewById(R.id.code_value);
            code_value.setText(stock.getCode());
            TextView name_value = findViewById(R.id.name_value);
            name_value.setText(stock.getName());
            TextView barcode_value = findViewById(R.id.barcode_value);
            barcode_value.setText(stock.getBarcode());
            TextView stock_value = findViewById(R.id.stock_value);
            stock_value.setText(Integer.toString(stock.getCount()));
            sum_value = findViewById(R.id.sum_value);
            sum_value.setText(Integer.toString(stock.getSum()));
        }
        if (goodsList.size() > 0) {
            GoodsModel goods = goodsList.get(0);
            TextView unit_value = findViewById(R.id.unit_value);
            unit_value.setText(goods.getUnit());
            TextView content_value = findViewById(R.id.content_value);
            content_value.setText(goods.getContent());
            TextView price_value = findViewById(R.id.price_value);
            price_value.setText(Float.toString(goods.getPrice()));
        }
        comboBox = (ComboBox)this.findViewById(R.id.place_combo);
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        for(int i =0; i < placeList.size(); i++)
       {
            list.add(new Pair<>(placeList.get(i).getPlace(),Integer.toString(placeList.get(i).getId())));
       }
       ComboBoxAdapter adapter1 = new ComboBoxAdapter(this,list,"");
       comboBox.setAdapter(adapter1);
       Sum();
       lv_detail = findViewById(R.id.lv_detail);
       detailModels = new ArrayList<>();
       detailModels.addAll(detailList);
       adapter = new DetailAdapter(detailModels);
       lv_detail.setAdapter(adapter);
       adapter.notifyDataSetChanged();
       lv_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for(int i=0;i<parent.getCount();i++) {
                    View v = parent.getChildAt(i);
                    if (position == i) {
                        v.setBackgroundResource(R.color.colorPrimary);//点击选择变色
                        detail_select = (DetailModel)parent.getAdapter().getItem(position);
                    }
                    else {
                        v.setBackgroundResource(R.color.white);
                    }
                }
            }
       });
    }

    //计算盘点合计
    private void Sum()
    {
        int result = LitePal.where("stockmodel_id = ?", Integer.toString(stockList.get(0).getId())).sum(DetailModel.class,"count",int.class);
        sum_value.setText(Integer.toString(result));
        StockModel stock = stockList.get(0);
        stock.setSum(result);
        stock.update(stock.getId());//更新库存表
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add:
                detail = new DetailModel();
                detail.setPlace(comboBox.getSelectedEntry());
                detail.setCount(Integer.parseInt(countEdit.getText().toString()));
                detail.setStockmodel(stockList.get(0));
                detail_have = LitePal//是否有同样储位的数据存在。
                        .where("stockmodel_id = ? and place = ?",
                                Integer.toString(stockList.get(0).getId()),
                                comboBox.getSelectedEntry())
                        .find(DetailModel.class);

                if (detail_have.size() > 0) {//有则更新，无则添加
                    //弹出更新对话框
                    builder=new AlertDialog.Builder(this);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle(R.string.update);
                    builder.setMessage("确定要更新"+comboBox.getSelectedEntry()+"的库存吗？");

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            detail.update(detail_have.get(0).getId());
                            detailList = LitePal
                                    .where("stockmodel_id = ?",
                                            Integer.toString(stockList.get(0).getId()))
                                    .find(DetailModel.class);
                            Sum();
                            detailModels.clear();
                            detailModels.addAll(detailList);
                            adapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    builder.setCancelable(true);
                    AlertDialog dialog=builder.create();
                    dialog.show();

                }else
                {
                    detail.save();
                    Sum();
                    detailModels.add(detail);
                    adapter.notifyDataSetChanged();
                }


                break;
            case R.id.delete:   //删除选择的数据。
                if (detail_select !=null)
                {
                    detail_select.delete();
                    detailList = LitePal
                            .where("stockmodel_id = ?",
                                    Integer.toString(stockList.get(0).getId()))
                            .find(DetailModel.class);
                    Sum();
                    detailModels.clear();
                    detailModels.addAll(detailList);
                    adapter.notifyDataSetChanged();
                }
                break;

        }

    }

    //按钮的点击处理


}
