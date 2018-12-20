package com.yiyin.aobosh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.LessonCategory;

import java.util.List;

/**
 * Created by ALIY on 2018/12/16 0016.
 */

public class LessonCategoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<LessonCategory> mList;

    public LessonCategoryAdapter(Context context, List<LessonCategory> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lesson_category_item, parent,false);

            holder = new ViewHolder();
            holder.categoryIco = ((ImageView) convertView.findViewById(R.id.category_img));
            holder.categoryText = (TextView) convertView.findViewById(R.id.category_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(mList.get(position).getIco())
                .into(holder.categoryIco);
        holder.categoryText.setText(mList.get(position).getName());

        return convertView;
    }

    private class ViewHolder {

        public ImageView categoryIco; //图标
        public TextView categoryText; //类名

    }
}
