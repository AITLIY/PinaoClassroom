package com.yiyin.aobosh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.bean.EvaluateBean;

import java.util.List;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class EvaluateBeanAdapter extends BaseAdapter {

    private Context mContext;

    private List<EvaluateBean> mList;

    public EvaluateBeanAdapter(Context context, List<EvaluateBean> list) {
        mContext = context;
        mList = list;
    }

    public void addLast(List<EvaluateBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.evaluate_item, parent,false);

            holder = new ViewHolder();

            holder.user_icon = convertView.findViewById(R.id.user_icon);
            holder.nickname_tv = convertView.findViewById(R.id.nickname_tv);
            holder.addtime_tv = convertView.findViewById(R.id.addtime_tv);
            holder.content_tv = convertView.findViewById(R.id.content_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(mList.get(position).getAvatar())
                .into(holder.user_icon);

        holder.nickname_tv.setText(mList.get(position).getNickname());
        holder.addtime_tv.setText(mList.get(position).getAddtime()+"");
        holder.content_tv.setText(mList.get(position).getContent());

        return convertView;
    }

    private class ViewHolder {

        public ImageView user_icon;         // 用户头像
        public TextView nickname_tv;        // 章节名称
        public TextView addtime_tv;         // 试听
        public TextView content_tv;         // 示范

    }
}
