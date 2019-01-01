package com.yiyin.aobosh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.LessonOrder;
import com.yiyin.aobosh.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class VideoBeanAdapter extends BaseAdapter {

    private Context mContext;
    private int mId;
    private List<VideoBean.ListBean> mList;

    public VideoBeanAdapter(Context context, List<VideoBean.ListBean> list) {
        mContext = context;
        mList = list;
    }

    public void setID(int id) {
        mId = id;
    }

    public void addLast(List<VideoBean.ListBean> list) {
        mList.addAll(list);
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
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent,false);

            holder = new ViewHolder();

            holder.chaapte_item = convertView.findViewById(R.id.chaapte_item);
            holder.play_staus = convertView.findViewById(R.id.play_staus);
            holder.chapter_title = convertView.findViewById(R.id.chapter_title);
            holder.try_listen_tv = convertView.findViewById(R.id.try_listen_tv);
            holder.example_tv = convertView.findViewById(R.id.example_tv);
            holder.teach_tv = convertView.findViewById(R.id.teach_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.chapter_title.setText(mList.get(position).getTitle());

        if (mList.get(position).getId()==mId) {
            holder.play_staus.setImageResource(R.drawable.icon_tab_play_green);
            holder.chapter_title.setTextColor(mContext.getResources().getColor(R.color.green2));
        } else {
            holder.play_staus.setImageResource(R.drawable.icon_tab_play);
            holder.chapter_title.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        if (mList.get(position).getIs_free()==1) {
            holder.try_listen_tv.setVisibility(View.VISIBLE);
        } else {
            holder.try_listen_tv.setVisibility(View.GONE);
        }

        if (mList.get(position).getSuffix()==1) {
            holder.example_tv.setVisibility(View.VISIBLE);
            holder.teach_tv.setVisibility(View.GONE);
        }

        if (mList.get(position).getSuffix()==2) {
            holder.example_tv.setVisibility(View.GONE);
            holder.teach_tv.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private class ViewHolder {

        public LinearLayout chaapte_item; // 章节item
        public ImageView play_staus;      // 图标
        public TextView chapter_title;    // 章节名称
        public TextView try_listen_tv;    // 试听
        public TextView example_tv;       // 示范
        public TextView teach_tv;         // 教学
    }
}
