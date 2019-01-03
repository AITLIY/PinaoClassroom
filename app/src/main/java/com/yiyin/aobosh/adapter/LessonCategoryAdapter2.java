package com.yiyin.aobosh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.Interface.CateIdSearchInterface;


import com.yiyin.aobosh.R;
import com.yiyin.aobosh.utils.PxUtils;
import com.yiyin.aobosh.bean.LessonCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALIY on 2018/12/20 0020.
 */

public class LessonCategoryAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LessonCategory.SonlistBean> mList = new ArrayList<>();
    private Context mContext;
    private CateIdSearchInterface mCateIdSearch;

    public LessonCategoryAdapter2(Context context, List<LessonCategory> lists, CateIdSearchInterface cateIdSearch) {

        LessonCategory.SonlistBean bean = new LessonCategory.SonlistBean();
        bean.setIco("");
        bean.setIco2(R.drawable.icon_btn_catgory_all);
        bean.setName("全部分类");
        bean.setId(0);
        bean.setType(1);
        mList.add(bean);

        for (LessonCategory category : lists) {

            LessonCategory.SonlistBean bean1 = new LessonCategory.SonlistBean();
            bean1.setId(category.getId());
            bean1.setName(category.getName());
            bean1.setIco(category.getIco());
            bean1.setType(1);
            mList.add(bean1);

            for (int i=0; i<category.getSonlist().size(); i++) {
                LessonCategory.SonlistBean bean2 = category.getSonlist().get(i);
                bean2.setType(2);
                bean2.setPosition(i);
                mList.add(bean2);
            }
        }

        mContext = context;
        mCateIdSearch = cateIdSearch;
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder {

        public LinearLayout mItem;
        public ImageView mIco;
        public TextView mTitle;

        public ViewHolder1(View view) {
            super(view);

            mItem = view.findViewById(R.id.list_item);
            mIco = view.findViewById(R.id.category_img);
            mTitle = view.findViewById(R.id.category_tv);
        }
    }
    public static class ViewHolder2 extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public View mLine;

        public ViewHolder2(View view) {
            super(view);

            mTitle = view.findViewById(R.id.category_title);
            mLine = view.findViewById(R.id.category_line);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;

        if (viewType==1) {
            v = LayoutInflater.from(mContext).inflate(R.layout.lesson_category_item2a, parent, false);
            viewHolder = new ViewHolder1(v);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.lesson_category_item2b, parent, false);
            viewHolder = new ViewHolder2(v);
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == 1) {
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            StaggeredGridLayoutManager.LayoutParams staggeredGridLayoutManager=new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtils.dip2px(mContext,50));
            staggeredGridLayoutManager.setFullSpan(true);
            viewHolder1.mItem.setLayoutParams(staggeredGridLayoutManager);

            viewHolder1.mTitle.setText(mList.get(position).getName());

            if ("".equals(mList.get(position).getIco())) {
                Glide.with(mContext)
                        .load(mList.get(position).getIco2())
                        .into(((ViewHolder1) holder).mIco);

            } else {
                Glide.with(mContext)
                        .load(mList.get(position).getIco())
                        .into(((ViewHolder1) holder).mIco);
            }

        } else {
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.mTitle.setText(mList.get(position).getName());

            if (mList.get(position).getPosition()%4==3) {
                viewHolder2.mLine.setVisibility(View.INVISIBLE);
            } else {
                viewHolder2.mLine.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCateIdSearch.onSearch(mList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = mList.get(position).getType();
        LogUtils.i("LessonCategoryAdapter2 getType: " + type+" position " + position);
        return type;
    }


}

