package com.yonyou.diamondrank.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.activities.ImageBrowseActivity;
import com.yonyou.diamondrank.utils.ImageLoadProxy;
import com.yonyou.diamondrank.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者：lanzhm on 2016/7/19 15:10
 * 邮箱：18770915807@163.com
 */
public class GridRecyclerAdapter extends RecyclerView.Adapter{

    private static final String TAG = "GridRecyclerAdapter";

    private List<Map<String,String>> models;

    private DisplayImageOptions options;

    private Context context;
    public GridRecyclerAdapter(List<Map<String,String>> models, Context context){
        this.models = models;
        this.context = context;
        options=ImageLoadProxy.getOptions4PictureList(R.drawable.icon_gray);
    }

    public void updateDatas(List<Map<String,String>> datas, boolean isClear) {
        if (isClear) {
            this.models.clear();
        }
        if (!datas.isEmpty()){
            this.models.addAll(datas);
            notifyDataSetChanged();
        }
    }
//
//    public void clear() {
//        this.models.getData().clear();
//        notifyDataSetChanged();
//    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView picture;
        private TextView userName,job,num;


        public ViewHolder(final View itemView) {
            super(itemView);

            picture = (ImageView) itemView.findViewById(R.id.item_picture);
            userName = (TextView) itemView.findViewById(R.id.item_username);
            job = (TextView) itemView.findViewById(R.id.item_job);
            num = (TextView) itemView.findViewById(R.id.item_num);

        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public ImageView getPicture(){
            picture.setScaleType(ImageView.ScaleType.FIT_XY);
            return picture;
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getJob() {
            return job;
        }

        public TextView getNum() {
            return num;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_main,null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        //TODO 增加对imageloader的优化处理
        ImageLoadProxy.displayImage(Utils.getUrl(context,models.get(position).get("imgPath")),vh.getPicture(),options);

//        ImageLoader.getInstance().displayImage(models.get(position).get("imgPath"),vh.getPicture());
        vh.getUserName().setText(models.get(position).get("name"));
        vh.getJob().setText(models.get(position).get("positionName"));
//        因为getIntegral()的返回值为int,所以使用setText()一直报错，需要将int转成String类型。
        int num=(int)Double.parseDouble(String.valueOf(models.get(position).get("integral")));
        vh.getNum().setText(String.valueOf(num));

        //如果设置了回调，就设置点击事件
        if (mOnItemClickListener != null){
            vh.getPicture().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(vh.itemView,position);
                }
            });

        }
        vh.getPicture().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent intent = new Intent(context, ImageBrowseActivity.class);
                intent.putStringArrayListExtra("images",loadImage());
                intent.putExtra("position",position);
                context.startActivity(intent);
                return true;
            }
        });
    }
    public ArrayList<String> loadImage() {

        ArrayList<String> images = new ArrayList<>();

        for (int i=0;i<models.size();i++){
            images.add(Utils.getUrl(context,models.get(i).get("imgPath")));
        }
        return images;
    }

    @Override
    public int getItemCount() {
        if (null!=models){
            return models.size();
        }else {
            return 0;
        }
    }

    /** * ItemClick的回调接口 */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
