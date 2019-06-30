package com.example.expandableview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expandableview.recycleritemanim.ExpandableViewHoldersUtil;

import java.util.ArrayList;


public class ExPandableViewActivity extends AppCompatActivity {
    private ExpandableViewHoldersUtil.KeepOneHolder<ViewHolder> keepOne;
    private ArrayList<String> stringArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recycler_view);
        ExpandableViewHoldersUtil.getInstance().init().setNeedExplanedOnlyOne(false);

        initView();
    }

    private void initData() {
        //数据就不造了，xml里面直接显示好了
        stringArrayList = new ArrayList<>();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.id_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //清空记录展开还是关闭的缓存数据
        ExpandableViewHoldersUtil.getInstance().resetExpanedList();
        recyclerView.setAdapter(new MyAdapter());

    }


    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        public MyAdapter() {
            super();
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(ExPandableViewActivity.this).inflate(R.layout.item_user_concern_layout, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

            viewHolder.tvTitle.setText("中美经贸磋商 po=" + position);

            keepOne.bind(viewHolder, position);

            viewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if(ExpandableViewHoldersUtil.isExpaned(position)){
//                        viewHolder.contentTv.setMaxLines(3);
//                    }else {
//                        viewHolder.contentTv.setMaxLines(100);
//                    }
                    keepOne.toggle(viewHolder);
                }
            });

            viewHolder.lvArrorwBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    keepOne.toggle(viewHolder);
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements ExpandableViewHoldersUtil.Expandable {
        TextView tvTitle;
        ImageView arrowImage;
        LinearLayout lvArrorwBtn;
        LinearLayout lvLinearlayout;
        TextView contentTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.item_user_concern_title);
            lvLinearlayout = itemView.findViewById(R.id.item_user_concern_link_layout);
            lvArrorwBtn = itemView.findViewById(R.id.item_user_concern_arrow);
            arrowImage = itemView.findViewById(R.id.item_user_concern_arrow_image);
            contentTv = itemView.findViewById(R.id.item_user_concern_link_text);

            keepOne = ExpandableViewHoldersUtil.getInstance().getKeepOneHolder();

            lvLinearlayout.setVisibility(View.GONE);
            lvLinearlayout.setAlpha(0);
        }

        @Override
        public View getExpandView() {
            return lvLinearlayout;
        }

        @Override
        public void doCustomAnim(boolean isOpen) {
            if (isOpen) {
                ExpandableViewHoldersUtil.getInstance().rotateExpandIcon(arrowImage, 180, 0);
            } else {
                ExpandableViewHoldersUtil.getInstance().rotateExpandIcon(arrowImage, 0, 180);
            }
        }
    }

    public static void showActivity(Context context) {
        Intent intent = new Intent(context, ExPandableViewActivity.class);
        context.startActivity(intent);
    }

}
