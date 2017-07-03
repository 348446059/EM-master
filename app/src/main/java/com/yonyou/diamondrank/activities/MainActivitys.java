package com.yonyou.diamondrank.activities;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AlertDialog;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;


import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.adapters.PositionAdapter;
import com.yonyou.diamondrank.bean.DepartmentBean;
import com.yonyou.diamondrank.entity.ItemBean;
import com.yonyou.diamondrank.fragment.OneFragment;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.model.SerializableListMap;
import com.yonyou.diamondrank.utils.ImageLoadProxy;
import com.yonyou.diamondrank.utils.SpUtils;
import com.yonyou.diamondrank.utils.SpaceItemDecoration;
import com.yonyou.diamondrank.utils.User;
import com.yonyou.diamondrank.utils.Utils;

import com.yonyou.diamondrank.widget.DragLayout;


import com.joanzapata.android.QuickAdapter;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;


/**
 * Created by libo on 17/1/18.
 */
public class MainActivitys extends BaseActivity{
    private DragLayout dl;
    private RecyclerView lv;
    private ImageView ivIcon,user_header_img;
    private QuickAdapter<ItemBean> quickAdapter;
    private Button detail_btn;
    private LinearLayout setting_btn,unRegister_btn;
    private OneFragment main_info_fragment;
    private User user;
    private TextView user_name,position_name;
    private PositionAdapter adapter;
    private ArrayList<DepartmentBean> departmentBeans;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setStatusBar();
        initDragLayout();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = (User) SpUtils.readObject(this, AppConstants.USER);
        uploadUI();
    }

    private void initDragLayout() {
        dl = (DragLayout) findViewById(R.id.dl);
        dl.setDragListener(new DragLayout.DragListener() {
            //界面打开的时候
            @Override
            public void onOpen() {
            }
            //界面关闭的时候
            @Override
            public void onClose() {
            }

            //界面滑动的时候
            @Override
            public void onDrag(float percent) {
                ViewHelper.setAlpha(ivIcon, 1 - percent);
            }
        });
    }

    private void uploadUI(){
        user_name.setText(user.getName());
        position_name.setText(user.getDepartmentName()+" - "+user.getPosition());
        ImageLoadProxy.displayHeadIcon(Utils.getUrl(this,user.getImgPath()),ivIcon);
        ImageLoadProxy.displayHeadIcon(Utils.getUrl(this,user.getImgPath()),user_header_img);
    }

    private void initView() {
        user = (User) SpUtils.readObject(MainActivitys.this,AppConstants.USER);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        user_header_img = (ImageView) findViewById(R.id.iv_bottom);
        detail_btn =(Button) findViewById(R.id.detail_btn);
        main_info_fragment = (OneFragment) this.getSupportFragmentManager().findFragmentById(R.id.main_info_fragment);
        main_info_fragment.setDepID(user.getDeptId());

        user_name = (TextView) findViewById(R.id.user_name);
        position_name = (TextView) findViewById(R.id.user_position);
        lv = (RecyclerView) findViewById(R.id.lv);


        setting_btn = (LinearLayout) findViewById(R.id.set_btn);
        unRegister_btn = (LinearLayout) findViewById(R.id.cancel_btn);
          uploadUI();

         layout = (LinearLayout) findViewById(R.id.linear_layout);

        if (!user.getRoles().equals("2")){
            lv.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.INVISIBLE);
        }
        //详情
        detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivitys.this,VoteDetailActivity.class);
                SerializableListMap listMap = new SerializableListMap();
                listMap.setStartTime(main_info_fragment.startTime);
                listMap.setEndTime(main_info_fragment.endTime);
                listMap.setListMap(main_info_fragment.mUserList);
                intent.putExtra("mUserListMap",listMap);
                startActivity(intent);
            }
        });

        //设置
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dl.close();
                Intent intent = new Intent(MainActivitys.this,SettingActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

        //注销
        unRegister_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivitys.this);
                builder.setTitle("提示");
                builder.setMessage("是否现在注销?");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SpUtils.clearData(MainActivitys.this,AppConstants.USER);
                        startActivity(new Intent(MainActivitys.this,LoginActivity.class));
                        MainActivitys.this.finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 dl.open();
            }
        });

        Utils.getPositionInfo(this,new Utils.MyCallBack() {
            @Override
            public void success(final ArrayList<DepartmentBean> listBean) {
                departmentBeans = listBean;
                adapter = new PositionAdapter(departmentBeans,MainActivitys.this);
                lv.setLayoutManager(new LinearLayoutManager(MainActivitys.this));
                int spacingInPixels = MainActivitys.this.getResources().getDimensionPixelOffset(R.dimen.left_item_space);
                lv.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
                lv.setAdapter(adapter);
                adapter.setmOnItemClickListener(new PositionAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        dl.close();
                    main_info_fragment.setDepID(String.valueOf(departmentBeans.get(position).getId()));
                    }
                });
            }

            @Override
            public void failed(Exception e) {

            }
        });
    }
    // 时间能愈合所有的伤口，如果你的伤口还没愈合，请给时间一点儿时间
    // time would heal almost wounds, if your wounds have not been healed up, please wait for a short while.
    // 不管过去有多困难，你都可以重新开始。
    // No matter how hard the past is, you can always begin again.
    // 有些时候，人们哭泣兵不是因为软弱，而是因为他们坚持了太久。
    // Sometimes, people cry not because they  are weak, it's because they have been strong for too long
    // 最好的装饰是微笑，最好的饰品是谦虚，最好的衣服是自信。
    // The best make-up is smile, the best jewelry is modesty,the best clothing is confident.
    // 世界最好的感受，就是发现心在微笑。

    // The best feeling in the world is when you know your heart is smiling.
    // 痛苦的人不行动，行动的人不痛苦。
    // people who act don't agonize, people who agonize don't act.
    // 不完美也是一种美，疯狂也是一种天分，荒唐总好过无聊
    // Imperfection is beauty, madness is genius, it's better to be absolutely ridiculous than absolutely boring.
    // 不要对未来过分担忧，只为清楚的仙子啊奋发图强。
    // Don't worry about for the ambiguous future, just make the effort for the explicit present.
    // 如过你不愿意学习和成长，没人能帮你，如果你下定决心学习和成长，没人能阻止你。
    // if you are not willing to learn and grow, no one can help you. if you are determined to learn and grow, no one can stop you.
    // 人总是比了解自己的，因此他的成就，将再一次给她以惊喜。
    // Man is more than that he can know of himself, consequently,his accomplishment,time and again surprise to him.

    // 知识给你力量，但是性格给你别人的尊重。
    // Knowledge will give you power, but character respect.
    // 不要对相信你的人撒谎，不要相信对你撒谎的人。
    // don't trust people who lie to you. don't lie to people who trust you.
    // 我们飞的越高，在不能飞的人眼中，我们越渺小。
    // The higher we soar, the smaller we appear to those who cant not fly.
    // 快乐并不意味着完美，那意味着你把眼光超越不完美。
    // Happiness does't mean perfect, it's mean that you'v decided look beyond imperfections.
    // 终有一天，你的负担将成为你的礼物，你所遭受的苦难将指明你的道路。
    // The day near when your burden become your gift, your suffering will light up your path.
    // 只有热枕，伟大的热枕，才能将灵魂提升到最高境界。
    // Only passion, great passion, can elevate the soul to great things.
    // 能让妈妈开心的方式就是有滋味的吃掉她所作的食物。
    // the way to your mother's heart is eat her food with relish.

    // 未来有几个名字，对弱者来说，叫不可能，对胆小的人来说，叫未知，对深思熟虑并且勇敢的人来说，叫理想。
    // The future has several names,for the weak,it is the impossible, for the fainthearted,it is unknown, for the thoughtful and valiant,it is idea.
    // 可疑的朋友比确信的敌人更可怕。
    // A doubtful friend is worst than a certain enemy.
    // 做你自己，说你想说的，介意的人不重要，重要的人不介意。
    // Be what you want and say what you feel, those who mind don't matter, those who matter don't mind.
    // 不要嘲笑一个年轻人的行为，他只是尝试寻找他自己。
    // Don't laugh a young for his affectations, he is try to one face after another to find himself.
    // 如过你不能飞，那就跑，跑不了，那就走，走不了，那就趴，不管你做什么，你都必须向前。
    // if you can't fly,then run, if your can't run,then walk, if you can't walk, then crawl, whatever you do, you have to keep moving forward.
    // 我真的想把这世界上美好的东西都给你，却发现最美好的东西就是你。
    // I want to give you all the best in the world , but find to the best in the world is you.
    // 真正的男人只想两件事，冒险和游戏。
    // The true man wants two things, danger and play.

    // 当天才不努力时，努力可以打败天才。
    // Work hard beats genius when the genius fails to hard work.
    // 一也许是最孤独的数字了，有时候只有孤独的人才玩的起。
    // one may be the loneliest number, sometimes only the lonely can play.
    // Can a man still be brave if he's afraid ? that is only time a man can be brave.
    // 快乐很简单，但做到简单很困难。
    // it's very simple to be happy, but it's very difficulty to be simple.
    // 这是我的一个小秘密，一个非常简单的秘密。一个人只有用心才能看到真实，事情的真相只用眼睛是看不到的。
    // and now here is my secret, a very simple secret. that is  only with the heart a man can see rightly, but what is essentail is invisible to the eyes.
    //        我们越是了解我们的激情，我们越是很难被它控制。
    // The more we know about our passion,the less they control us.
    //        那些听不见音乐的人认为那些跳舞的人疯了。
    // Those who seen dancing were thought to be insance by those who can't hear the music.

    // 不要因为一次的失败，就放弃原先决议要达到的目的。
    // Don't ,for one repulse,give up the purpose that you resolved to effect
    // 每年农历五月初五端午节，又称龙舟节
    // Chinese Dragon Boat Festival sis also know as DuanWu festival, which falls to  the fifth day of the fifth lunar month every year.

    // 要生存就要改变，要改变就要成长，要成长就要不断的自我创新。
    // To existence is to change,to change is to mature,to mature is to go on creating oneself endlessly.
    // 所有的人曾今都是小孩，但是很少人知道。
    // All the people is once child, but few of them can know it.
    // 过去的选择成就了现在的我。
    // I am who I am today,because the choice i made yesterday.
    // 自由并不是让你想做什么就做什么，而是教你不想做什么就可以不用做什么。
    // Freedom is not letting you do whatever you wanna do, but teaching you not to do the things you don't wanna do.
    // 任何人如果真诚的想帮助别人，那他同时也帮组了自己，这是人生中最优美的补偿。
    // It's the most beautiful compensation of life,that no man can sincerely try to help another without helping themselves.
    // 昨天以成为历史，明天很神秘，今天是上帝赐予的礼物，所有你能做的就是抓住它。
    // Yesterday is history,tomorrow is mystery, today is a gift, all you can do is grasp it.
    // 诺不轻信，故人不负我，诺不轻许，故我不负人。
    // don't count on other rashly, so they have no chance to betray you. don't make rash promise,in case you regret.
    // Dare and the world always yields, it beats you sometimes, dare it again and again and the world will succumb.
    // 生活并不缺乏美，而是缺乏发现美的眼睛。
    // The life don't lack of beauty,but lack of the eyes to find beauty.
    // 有点瑕疵没关系，那样才显得更真实。
    // it's okay to have flaws, which make you real.

    // 没有人，除了我们自己能够贬低我们自己。
    // no one ,but ourselves,can degrade us.
    // 想想你现在拥有的幸福，那应该很多，而不是过去的不幸，那每个人或多或少都有点儿。
    // Reflect upon your blessings, of which every man has many, not on the past misfortunes,of which all men has some.
    // 任何事情都要尝试一下，因为你不知道什么人或什么事将会改变你的命运。
    // Give everything a shot, because you never know who or what  is going to change your life.
    // 爱始于微笑，浓于亲吻，逝于眼泪。
    // Love begins with a smile, grows with a kiss, ends with a tear.
    // 你没必要把自己弄的骨瘦如材为了显得漂亮，开开心心的接纳自己的身材，那才是漂亮的原因。
    // you don't have to be skinny to be pretty,just have to be happy with your body,that what actually makes you real pretty.
    // 我不在意父亲是什么样的人，我只在意记忆中父亲是什么样的人。
    // it doesn't matter who my father was, it matters who i remember he was.
    // 不要让这三件事控制你，过去，别人和金钱。
    // don't let you controlled by those three things,the past, people and money.
    // 人总是需要困难，因为他们是享受成功的必须。
    // Man  always needs difficulty, because they are necessary to enjoy success.
    // 优雅是一种永不褪色的美。
    // Elegance is beauty that never fades。
    // 逃避困难是一场永远赢不了的比赛。
    // Running away from problems is a race you can never win.
    // 你必须在没有人相信你的时候相信自己。
    // you have to believe yourself when no one else dose,
}
