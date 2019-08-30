package com.mumuwoyou.mycount.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mumuwoyou.mycount.app.R;
import com.mumuwoyou.mycount.app.dbmodel.DetailModel;

import java.util.List;

public class DetailAdapter extends BaseAdapter {

    private List<DetailModel> detailModels;

    public DetailAdapter(List<DetailModel> stockModels) {
        this.detailModels = stockModels;
    }

    @Override
    public int getCount() {
        return detailModels.size();
    }

    @Override
    public DetailModel getItem(int position) {
        return detailModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return detailModels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailModel detail = detailModels.get(position);
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
            holder=new ViewHolder();
            holder.tv_place=convertView.findViewById(R.id.tv_place);
            holder.tv_count=convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();

        }

        holder.tv_place.setText(detail.getPlace());
        holder.tv_count.setText(Integer.toString(detail.getCount()));


        // 设置隔行颜色
        //
//        if (position % 2 != 0) {
//            convertView.setBackgroundResource(R.color.yellow);
//        } else {
//            convertView.setBackgroundResource(R.color.white);
//        }

        return convertView;
    }


    static class ViewHolder{
        TextView tv_place;
        TextView tv_count;
    }
}
