package com.yonyou.diamondrank.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.utils.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libo on 2017/2/10.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_GROUP = 1;
    private List<String> mData = new ArrayList<>();
    private Context mContext;
    private User user;
    private boolean isTitle(int pos){
        if(mData.get(pos).startsWith("this is title:")) {
            return true;
        }
        return false;
    }

    public RecyclerViewAdapter(Context mContext, List<String> mData,User user) {
        this.mContext = mContext;
        this.mData = mData;
        this.user = user;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from (mContext);
        switch ( viewType ) {
            case TYPE_IMAGE:
                ViewGroup vImage = ( ViewGroup ) mInflater.inflate ( R.layout.item_image_m, parent, false );
                ImageViewHolder vhImage = new ImageViewHolder ( vImage );
                return vhImage;
            case TYPE_GROUP:
                ViewGroup vGroup = ( ViewGroup ) mInflater.inflate ( R.layout.item_text_m, parent, false );
                GroupViewHolder vhGroup = new GroupViewHolder ( vGroup );
                return vhGroup;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch ( holder.getItemViewType () ) {
            case TYPE_IMAGE:
                ImageViewHolder imageViewHolder = ( ImageViewHolder ) holder;

                cellForRow(imageViewHolder,position);

                break;
            case TYPE_GROUP:
                GroupViewHolder groupViewHolder = ( GroupViewHolder ) holder;
                break;
        }
    }

    private void cellForRow(final ImageViewHolder holder, final int position){
        if (position == 0){
            holder.mTitle.setText("修改登录名");
            holder.mValue.setText(user.getLoginName());
        }else if (position == 1){
            holder.mTitle.setText("修改用户名");
            holder.mValue.setText(user.getName());
        }else if (position == 3){
            holder.mTitle.setText("修改职位信息");
            holder.mValue.setText(user.getDepartmentName()+" - "+user.getPosition());
        }else if (position == 5){
            holder.mTitle.setText("修改手机号");

        }else if (position == 6){
            holder.mTitle.setText("更换邮箱");

        }else if (position == 8){
            holder.mTitle.setText("修改密码");

        }else if (position == 9){
            holder.mTitle.setText("添加部门");

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(holder.itemView,position);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (user.getRoles().equals("2")){
            return 10;
        }else {
            return 9;
        }

    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if (position ==2 || position == 4 || position == 7){

            viewType = TYPE_GROUP;
        }else {
            viewType = TYPE_IMAGE;

        }

//        if (!isTitle(position) ) {
//            viewType = TYPE_IMAGE;
//        } else {
//            viewType = TYPE_GROUP;
//        }
        return viewType;
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        public GroupViewHolder ( View itemView ) {
            super(itemView);
            mTitle= (TextView) itemView.findViewById(R.id.text);
        }

    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mValue;
        public ImageViewHolder ( View itemView ) {
            super(itemView );
            mTitle= (TextView) itemView.findViewById(R.id.text);
            mValue= (TextView) itemView.findViewById(R.id.value);
        }
    }

    /** * ItemClick的回调接口 */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private RecyclerViewAdapter.OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(RecyclerViewAdapter.OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
