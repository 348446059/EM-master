package com.yonyou.diamondrank.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.bean.DepartmentBean;

import java.util.ArrayList;

/**
 * Created by libo on 2017/1/25.
 */

public class PositionAdapter extends RecyclerView.Adapter {

    private ArrayList<DepartmentBean> arrayList;
    private Context context;
    private ArrayList<ViewHolder> holders = new ArrayList<>();
    public PositionAdapter(ArrayList<DepartmentBean> arrayLis, Context context){
        this.arrayList = arrayLis;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_layout,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
         view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
     final ViewHolder vh = (ViewHolder)holder;
          holders.add(vh);
        vh.imageView.setBackgroundColor(context.getResources().getColor(R.color.black));
        vh.textView.setText(arrayList.get(position).getDepartmentName());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ViewHolder vh:holders) {
                    vh.imageView.setBackgroundColor(context.getResources().getColor(R.color.black));
                }
                vh.imageView.setBackgroundColor(context.getResources().getColor(R.color.btn_bkgrd_blue_new) );
                mOnItemClickListener.onItemClick(vh.itemView,position);

            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
           imageView = (ImageView) itemView.findViewById(R.id.item_img);
            textView = (TextView) itemView.findViewById(R.id.item_tv);

        }
    }
    /** * ItemClick的回调接口 */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private PositionAdapter.OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(PositionAdapter.OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
