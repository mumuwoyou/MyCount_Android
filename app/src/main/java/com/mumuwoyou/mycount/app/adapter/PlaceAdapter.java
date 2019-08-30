package com.mumuwoyou.mycount.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mumuwoyou.mycount.app.R;
import com.mumuwoyou.mycount.app.dbmodel.DetailModel;
import com.mumuwoyou.mycount.app.dbmodel.PlaceModel;

import java.util.List;

public class PlaceAdapter extends BaseAdapter {

    private List<PlaceModel> placeModels;

    public PlaceAdapter(List<PlaceModel> placeModels) {
        this.placeModels = placeModels;
    }

    @Override
    public int getCount() {
        return placeModels.size();
    }

    @Override
    public PlaceModel getItem(int position) {
        return placeModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return placeModels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlaceModel detail = placeModels.get(position);
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place,parent,false);
            holder=new ViewHolder();
            holder.tv_place=convertView.findViewById(R.id.tv_place);
            holder.tv_id=convertView.findViewById(R.id.tv_id);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();

        }

        holder.tv_place.setText(detail.getPlace());
        holder.tv_id.setText(Integer.toString(detail.getId()));


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
        TextView tv_id;
    }
}
