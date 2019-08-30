package com.mumuwoyou.mycount.app.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mumuwoyou.mycount.app.R;
import com.mumuwoyou.mycount.app.dbmodel.StockModel;

import java.util.List;

public class StockAdapter extends BaseAdapter {

    private List<StockModel> stockModels;

    public StockAdapter(List<StockModel> stockModels) {
        this.stockModels = stockModels;
    }

    @Override
    public int getCount() {
        return stockModels.size();
    }

    @Override
    public StockModel getItem(int position) {
        return stockModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stockModels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StockModel stock = stockModels.get(position);
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock,parent,false);
            holder=new ViewHolder();
            holder.tv_code=convertView.findViewById(R.id.tv_code);
            holder.tv_name=convertView.findViewById(R.id.tv_name);
            holder.tv_barcode=convertView.findViewById(R.id.tv_barcode);
            holder.tv_count=convertView.findViewById(R.id.tv_stock);
            holder.tv_sum=convertView.findViewById(R.id.tv_sum);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();

        }

        holder.tv_code.setText(stock.getCode());
        holder.tv_name.setText(stock.getName());
        holder.tv_barcode.setText(stock.getBarcode());
        holder.tv_count.setText(Integer.toString(stock.getCount()));
        holder.tv_sum.setText(Integer.toString(stock.getSum()));
        if (stock.getSum() < stock.getCount())//欠库存为红色，正库存位绿色
        {
            holder.tv_sum.setTextColor(Color.rgb(255,0, 0));
        }else
        {
            holder.tv_sum.setTextColor(Color.rgb(0,255, 0));
        }
        // 设置隔行颜色
        //
        if (position % 2 != 0) {
            convertView.setBackgroundResource(R.color.yellow);
        } else {
            convertView.setBackgroundResource(R.color.white);
        }

        return convertView;
    }


    static class ViewHolder{
        TextView tv_code;
        TextView tv_name;
        TextView tv_barcode;
        TextView tv_count;
        TextView  tv_sum;

    }
}
