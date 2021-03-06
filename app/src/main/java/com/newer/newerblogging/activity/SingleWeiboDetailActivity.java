package com.newer.newerblogging.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.newer.newerblogging.R;
import com.newer.newerblogging.adapter.CommentsAdapter;
import com.newer.newerblogging.adapter.GridViewAdapter;
import com.newer.newerblogging.base.BaseActivity;
import com.newer.newerblogging.bean.comment.AllComments;
import com.newer.newerblogging.bean.comment.Comment;
import com.newer.newerblogging.bean.microblog.SingleMicroblog;
import com.newer.newerblogging.bean.microblog.User;
import com.newer.newerblogging.utils.BlogInterfaceConfig;
import com.newer.newerblogging.utils.Config;
import com.newer.newerblogging.utils.GlideForPicFromNet;
import com.newer.newerblogging.utils.NetConnectionUtil;
import com.newer.newerblogging.utils.Utils;
import com.newer.newerblogging.view.HeadPicView;
import com.newer.newerblogging.view.PicGridView;

import java.util.ArrayList;

import butterknife.Bind;

public class SingleWeiboDetailActivity extends BaseActivity {

    /**
     * 头像
     */
    @Bind(R.id.hpv_micro_header)
    HeadPicView hpvMicroHeader;
    /**
     * 用户名
     */
    @Bind(R.id.tv_micro_username)
    TextView tvMicroUsername;
    /**
     * 时间
     */
    @Bind(R.id.tv_micro_time)
    TextView tvMicroTime;
    /**
     * 隐藏
     */
    @Bind(R.id.iv_micro_more)
    ImageView ivMicroMore;
    /**
     * 微博内容
     */
    @Bind(R.id.tv_micro_content)
    TextView tvMicroContent;
    /**
     * 图片
     */
    @Bind(R.id.pgv_micro_pics)
    PicGridView pgvMicroPics;
    /**
     * 隐藏
     */
    @Bind(R.id.layout_micro_bottom)
    RelativeLayout layoutMicroBottom;

    @Bind(R.id.ptrlv_detail_content)
    RecyclerView recyclerView;

    /**
     * 微博对象
     */
    private static SingleMicroblog mSingleMicroblog;
    ArrayList<Comment> mComments;
    CommentsAdapter mCommentAdapter;
    String id_command = BlogInterfaceConfig.MAX_MICRO_NUM;

    @Override
    public int getLayoutResource() {
        return R.layout.activity_single_weibo_detail;
    }

    @Override
    public void initListener() {
        /*
         * 当滑动到最底端的时候，自动刷新数据
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                    if (lastPosition == layoutManager.getItemCount() - 1) {
                        refresh();
                    }
                }
            }
        });

        hpvMicroHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleWeiboDetailActivity.this, UserHomeActivity.class);
                intent.putExtra(Config.EXTRA_USER_ID,mSingleMicroblog.getUser().getIdstr());
                SingleWeiboDetailActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Config.EXTRA_MICROBLOG_BUNDLE);
        mSingleMicroblog = bundle.getParcelable(Config.EXTRA_MICROBLOG);

        User user = mSingleMicroblog.getUser();

        //隐藏不重要的两个部分
        ivMicroMore.setVisibility(View.GONE);
        layoutMicroBottom.setVisibility(View.GONE);

        //头像
        GlideForPicFromNet.netGetHeadWithUrl(this, user.getProfile_image_url(),
                80, 80, new GlideForPicFromNet.HeadCallback() {
                    @Override
                    public void doCallbackData(Bitmap bitmap) {
                        hpvMicroHeader.setImageBitmap(bitmap);
                    }
                });
        //用户名
        tvMicroUsername.setText(user.getScreen_name());
        //时间
        tvMicroTime.setText(Utils.gmtToLocalTime(mSingleMicroblog.getCreated_at()));
        //微博内容
        tvMicroContent.setText(mSingleMicroblog.getText());
        //图片
        GridViewAdapter gvAdapter = new GridViewAdapter(this, mSingleMicroblog.getPic_urls());
        pgvMicroPics.setAdapter(gvAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mComments = new ArrayList<>();
        mCommentAdapter = new CommentsAdapter(this, mComments,mSingleMicroblog.getIdstr());
        recyclerView.setAdapter(mCommentAdapter);

        refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        id_command = BlogInterfaceConfig.MAX_MICRO_NUM;
    }

    private void refresh() {
        NetConnectionUtil.netToShowComments(this, mSingleMicroblog.getIdstr(),
                id_command, 20, 1, 0, new NetConnectionUtil.NetCallback() {
                    @Override
                    public void doSuccess(String data) {
                        ArrayList<Comment> list = new Gson().fromJson(data, AllComments.class).getComments();
                        if(list != null && list.size() != 0){
                            mComments.addAll(mComments.size() == 0 ? 0 : mComments.size() - 1,
                                    list);
                            id_command = String.valueOf(Long.valueOf(list.get(list.size() - 1).getIdstr()) - 1);
                            mCommentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void doFail(String message) {

                    }
                });
    }
}


