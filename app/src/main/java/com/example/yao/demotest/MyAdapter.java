package com.example.yao.demotest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Yao on 2016/12/4.
 */

public class MyAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private Context context;
    private List<DataBean> meipaiBeen;
    private OnRecyclerViewItemClickListener mOnItemClickListener=null;

    public MyAdapter(Context context, List<DataBean> beans){
        super();
        this.context = context;
        this.meipaiBeen = beans;
    }
    public static interface OnRecyclerViewItemClickListener{
        void onItemClick(View view,DataBean dataBean);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        MyViewHolder holder = null;
        view = LayoutInflater.from(context).inflate(R.layout.item_meipai,null);
        holder = new MyViewHolder(view);

        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        Glide.with(context).load(meipaiBeen.get(position).getCover_pic()).diskCacheStrategy(DiskCacheStrategy.ALL).into(myViewHolder.img);
        Glide.with(context).load(meipaiBeen.get(position).getAvatar()).into(myViewHolder.cir);
        myViewHolder.tv.setText(meipaiBeen.get(position).getCaption());
        myViewHolder.itemView.setTag(meipaiBeen.get(position));
    }

    @Override
    public int getItemCount() {
        return meipaiBeen.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, (DataBean) v.getTag());
        }
    }
    public void setmOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView tv;
        private CircleImageView cir;
        private LinearLayout lin;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_meipai_img);
            tv = (TextView) itemView.findViewById(R.id.item_meipai_tv);
            cir = (CircleImageView) itemView.findViewById(R.id.item_meipai_cir);

            ViewGroup.LayoutParams params = img.getLayoutParams();
            params.height=(int)(200+Math.random()*400);
            img.setLayoutParams(params);
        }
    }
}

