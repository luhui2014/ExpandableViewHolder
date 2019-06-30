package com.example.expandableview.recycleritemanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * recyclerView item view 的展开动画
 * <p>
 * 注意：getExpandView 的view 初始透明度为0
 * <p>
 * 例如：UserConcernHolder
 * 1.holder implements ExpandableViewHoldersUtil.Expandable
 * 2.linkRootView.setAlpha(0);
 */
public class ExpandableViewHoldersUtil {
    public static long animalDuration = 300;
    public static long alphaDuration = 100;
    private static ArrayList<String> explanedList; //缓存的数据
    private boolean needExplanedOnlyOne = false;

    private static ExpandableViewHoldersUtil holdersUtil;

    /**
     * 获取单例
     */
    public static ExpandableViewHoldersUtil getInstance() {
        if (holdersUtil == null) {
            holdersUtil = new ExpandableViewHoldersUtil();
        }

        return holdersUtil;
    }

    public ExpandableViewHoldersUtil init() {
        explanedList = new ArrayList<>();

        return this;
    }

    /**
     * 点击第二个会收缩前一个 ，remove object
     */
    public void setNeedExplanedOnlyOne(boolean needExplanedOnlyOne) {
        this.needExplanedOnlyOne = needExplanedOnlyOne;
    }

    public static boolean isExpaned(int index){
        return explanedList.contains(index + "");
    }

    private void addPositionInExpaned(int pos) {
        if (!explanedList.contains(pos + "")) {
            explanedList.add(pos + "");
        }
    }

    private void deletePositionInExpaned(int pos) {
        //remove Object 直接写int，会变成index,造成数组越界
        explanedList.remove(pos + "");
    }

    public void resetExpanedList() {
        explanedList.clear();
    }

    public KeepOneHolder getKeepOneHolder() {
        return new KeepOneHolder<>();
    }

    //自定义列表中icon 的旋转的动画
    public void rotateExpandIcon(final ImageView imageView, float from, float to) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageView.setRotation((float) valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    //参数介绍：1、holder对象 2、展开部分的View，由holder.getExpandView()方法获取 3、animate参数为true，则有动画效果
    private void openHolder(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate) {
        if (animate) {
            expandView.setVisibility(View.VISIBLE);
            //改变高度的动画
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder);

            //扩展的动画，透明度动画开始
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(expandView, View.ALPHA, 1);
            alphaAnimator.setDuration(animalDuration + alphaDuration);
            alphaAnimator.addListener(new ViewHolderAnimator.ViewHolderAnimatorListener(holder));

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator, alphaAnimator);
            animatorSet.start();

        } else { //为false时直接显示
            expandView.setVisibility(View.VISIBLE);
            expandView.setAlpha(1);
        }
    }

    //类似于打开的方法
    private void closeHolder(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate) {
        if (animate) {

            expandView.setVisibility(View.GONE);
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder);
            expandView.setVisibility(View.VISIBLE);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    expandView.setVisibility(View.GONE);
                    expandView.setAlpha(0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    expandView.setVisibility(View.GONE);
                    expandView.setAlpha(0);
                }
            });
            animator.start();
        } else {
            expandView.setVisibility(View.GONE);
            expandView.setAlpha(0);
        }
    }


    //获取展开部分的View
    public interface Expandable {
        View getExpandView();

        void doCustomAnim(boolean isOpen);
    }

    //-1表示所有item是关闭状态，opend为pos值的表示pos位置的item为展开的状态
    private int opened = -1;

    @SuppressWarnings("deprecation")
    public class KeepOneHolder<VH extends RecyclerView.ViewHolder & Expandable> {
        int preOpen;

        /**
         * 此方法是在Adapter的onBindViewHolder()方法中调用
         *
         * @param holder holder对象
         * @param pos    下标
         */
        public void bind(VH holder, int pos) {
            if (explanedList.contains(pos + "")) {
                ExpandableViewHoldersUtil.getInstance().openHolder(holder, holder.getExpandView(), false);
            } else {
                ExpandableViewHoldersUtil.getInstance().closeHolder(holder, holder.getExpandView(), false);
            }
        }

        /**
         * 响应ViewHolder的点击事件
         *
         * @param holder holder对象
         */
        @SuppressWarnings("unchecked")
        public void toggle(VH holder) {
            int position = holder.getPosition();
            if (explanedList.contains(position + "")) {
                opened = -1;
                deletePositionInExpaned(position);

                holder.doCustomAnim(true);
                ExpandableViewHoldersUtil.getInstance().closeHolder(holder, holder.getExpandView(), true);
            } else {
                preOpen = opened;
                opened = position;

                addPositionInExpaned(position);
                holder.doCustomAnim(false);
                ExpandableViewHoldersUtil.getInstance().openHolder(holder, holder.getExpandView(), true);

                //是否要关闭上一个
                if (needExplanedOnlyOne && preOpen != position) {
                    final VH oldHolder = (VH) ((RecyclerView) holder.itemView.getParent()).findViewHolderForPosition(preOpen);
                    if (oldHolder != null) {
                        Log.e("KeepOneHolder", "oldHolder != null");
                        ExpandableViewHoldersUtil.getInstance().closeHolder(oldHolder, oldHolder.getExpandView(), true);
                        deletePositionInExpaned(preOpen);
                    }
                }
            }
        }
    }

}
