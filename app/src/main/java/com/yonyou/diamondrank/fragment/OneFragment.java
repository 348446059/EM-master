package com.yonyou.diamondrank.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.adapters.GridRecyclerAdapter;
import com.yonyou.diamondrank.callbacks.CommonCallback;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.presenter.MainPresenter;
import com.yonyou.diamondrank.utils.DatePickerUtil;
import com.yonyou.diamondrank.utils.DateUtil;
import com.yonyou.diamondrank.utils.DialogUtil;
import com.yonyou.diamondrank.utils.SpUtils;
import com.yonyou.diamondrank.utils.ToastUtils;
import com.yonyou.diamondrank.utils.User;
import com.yonyou.diamondrank.utils.Utils;
import com.yonyou.diamondrank.views.DividerGridItemDecoration;
import com.yonyou.diamondrank.views.MainView;
import com.yonyou.diamondrank.views.PopView;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sevenheaven.segmentcontrol.SegmentControl;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class OneFragment  extends Fragment implements SwipeRefreshLayout.OnRefreshListener,MainView{
    private View mView;
    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    public List<Map<String,String>> mUserList = new ArrayList<Map<String,String>>();
    private GridRecyclerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    public String depID; //部门ID
    private int lastVisibleItem=0;
    private SimpleDateFormat dateFormater=new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");

    private ImageButton personPic;
    private Button detailInfo;
    private SegmentControl mSegmentHorzontal;

    public String startTime;
    public String endTime;
    private List<String> images;
    private MainPresenter presenter;

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DialogUtil.CHOOSE_TYPE: {
//                    ToastUtils.makeShortText("从日期对话框传来的值"+msg.obj,MainActivity.this);
                    switch (Integer.valueOf(msg.obj.toString())) {
                        case 0: {
                            startTime=dateFormater.format(DateUtil.getCurrentWeekDayStartTime());
                            endTime=dateFormater.format(DateUtil.getCurrentWeekDayEndTime());
                            break;
                        }
                        case 1: {
                            startTime=dateFormater.format(DateUtil.getCurrentMonthStartTime());
                            endTime=dateFormater.format(DateUtil.getCurrentMonthEndTime());
                            break;
                        }
                        case 2: {
                            startTime=dateFormater.format(DateUtil.getCurrentQuarterStartTime());
                            endTime=dateFormater.format(DateUtil.getCurrentQuarterEndTime());
                            break;
                        }
                        case 3: {
                            startTime=dateFormater.format(DateUtil.getCurrentYearStartTime());
                            endTime=dateFormater.format(DateUtil.getCurrentYearEndTime());
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }
                case PopView.POPVIEW_VOTE:{
                    if (String.valueOf(msg.obj).equals("0")){
                        ToastUtils.makeShortText("投票成功！",getActivity());
                    }else{
                        ToastUtils.makeShortText("投票出错！",getActivity());
                    }
                    loadData();
                    break;
                }
                default:
                    break;
            }
        }
    };
    public void initPresenter(){
        presenter = new MainPresenter(this);
        presenter.loadImage();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView==null){
            mView=inflater.inflate(R.layout.activity_fragment,container,false);
            initViews();

        }
        return mView;
    }



    @Override
    public void onRefresh() {
        loadData();
        Toast.makeText(getActivity(),"最新数据更新完成",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    protected void loadData() {
        if (null==startTime||null==endTime){
            startTime=dateFormater.format(DateUtil.getCurrentYearStartTime());
            endTime=dateFormater.format(DateUtil.getCurrentYearEndTime());
        }
        OkHttpUtils
                .get()//
                .addParams("startTime",startTime)
                .addParams("endTime",endTime)
                .addParams("deptId",depID)
                .url(Utils.getUrl(getContext(),AppConstants.INDEX_LIST_URL))//
                .build()//
                .execute(new CommonCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e(TAG, "------*------onError：error="+e.getMessage());
                        ToastUtils.makeShortText("网络连接出错，请检查网络连接！",getActivity());
                        mSwipeRefreshWidget.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(Map<String, Object> stringObjectMap) {
                        mUserList = (ArrayList<Map<String,String>>)stringObjectMap.get("data");
                        setAdapter();
                        mSwipeRefreshWidget.setRefreshing(false);
                    }
                });
    }
    protected void initViews()
    {
           initPresenter();
//        personPic.setOnClickListener(this);
//        detailInfo.setOnClickListener(this);
        mSwipeRefreshWidget = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.c1, R.color.c2,
                R.color.c3);
        mSwipeRefreshWidget.setOnRefreshListener(this);

        mLayoutManager = new GridLayoutManager(getActivity(),3);

        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeRefreshWidget.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.id_recyclerview);
//        线性布局
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        网格布局
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapter = new GridRecyclerAdapter(mDatas));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity(),10));

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }

        });




        mSegmentHorzontal = (SegmentControl) mView.findViewById(R.id.segment_control);
        mSegmentHorzontal.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                Log.i(TAG, "onSegmentControlClick: index = " + index);
                switch (index){
                    case 0:
//                        ToastUtils.makeShortText("You clicked person_picture item",MainActivity.this);
                        DialogUtil.showCompleteDialog(getActivity(),handler,false);
                        break;
                    case 1:
//                        ToastUtils.makeShortText("You clicked 范围 item",MainActivity.this);
                        DatePickerUtil.showDataPicker(getActivity(),"开始日期",startTimeListener);

                        break;
                    default:
                        break;
                }

            }
        });

    }

    public void setDepID(String depID){
        this.depID = depID;
        loadData();
    }

    private void setAdapter(){
        if(mAdapter==null){
            mAdapter = new GridRecyclerAdapter(mUserList,this.getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.updateDatas(mUserList,true);
        }
        final User user = (User) SpUtils.readObject(getActivity(),AppConstants.USER);

        mAdapter.setmOnItemClickListener(new GridRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                ToastUtils.makeShortText("You clicked "+(position+1)+" item",MainActivity.this);
                Map<String,String> datas=new HashMap<String, String>();
                String recorderId= user.getId();
                String reveiverId=String.valueOf(mUserList.get(position).get("id"));

                if (recorderId.equals(reveiverId)){
                    ToastUtils.makeShortText("你不能给自己投钻！",getActivity());
                    return;
                }
                datas.put("recorderId",recorderId );
                datas.put("reveiverId", reveiverId);
                Log.d(TAG, "------*------MainActivity recorderId="+SpUtils.getString(getActivity(),AppConstants.RECORDER_ID));
                Log.d(TAG, "------*------MainActivity reveiverId="+String.valueOf(mUserList.get(position).get("id")));
                PopView pp = new PopView(getActivity(),R.style.pop_window_dialog,false,datas,handler);
                pp.show();
            }
        });
    }
    private SlideDateTimeListener startTimeListener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
//            ToastUtils.makeShortText(dateFormater.format(date),MainActivity.this);
            startTime=dateFormater.format(date);
            DatePickerUtil.showDataPicker(getActivity(),"结束日期",endTimeListener);
        }

        @Override
        public void onDateTimeCancel() {
            startTime=null;
        }
    };

    private SlideDateTimeListener endTimeListener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
//            ToastUtils.makeShortText(dateFormater.format(date),MainActivity.this);
            endTime=dateFormater.format(date);
            loadData();
        }

        @Override
        public void onDateTimeCancel() {
            startTime=null;
            endTime=null;
        }
    };

    @Override
    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public void initRecycler() {

    }
}
