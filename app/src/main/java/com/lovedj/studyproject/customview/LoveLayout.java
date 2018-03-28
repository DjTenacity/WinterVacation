package com.lovedj.studyproject.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lovedj.studyproject.R;

import java.util.Random;

/**
 * Comment:
 *
 * @author :DJ鼎尔东 / 1757286697@qq.cn
 * @version : Administrator1.0
 * @date : 2018/1/24
 */
public class LoveLayout extends RelativeLayout {

    private Random random;
    private int[] mImageRes;
    //空间的宽高
    private int mWidth, mHeight;
    private int mDrawableWidth, mDrawableHeight;

    private Interpolator[] mInterpolator;


    public LoveLayout(Context context) {
        this(context, null);
    }

    public LoveLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        random = new Random();
        mImageRes = new int[]{R.mipmap.psb};

        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.psb);
        mDrawableWidth = drawable.getIntrinsicWidth();
        mDrawableHeight = drawable.getIntrinsicHeight();

        mInterpolator = new Interpolator[]{new DecelerateInterpolator()
                , new AccelerateDecelerateInterpolator()
                , new LinearInterpolator()
                ,new AccelerateInterpolator()};
    }


    public void addLove() {
        //添加一个ImageView在底部
        final ImageView loveIv = new ImageView(getContext());

        //给一个图片资源
        loveIv.setImageResource(mImageRes[random.nextInt(mImageRes.length - 1)]);

        //添加到底部中心? LayoutParams
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(CENTER_HORIZONTAL);

        loveIv.setLayoutParams(params);

        addView(loveIv);

        AnimatorSet animator = getAnimator(loveIv);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //移除
                removeView(loveIv);
            }
        });

        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取控件的宽高
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);


    }

    public AnimatorSet getAnimator(ImageView loveIv) {

        AnimatorSet allAnimatior = new AnimatorSet();
        AnimatorSet innerAnimator = new AnimatorSet();

        //添加效果  放大和透明度
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(loveIv, "alpha", 0.3f, 1.0f);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(loveIv, "scaleX", 0.3f, 1.0f);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(loveIv, "scaleY", 0.3f, 1.0f);

        innerAnimator.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator);
        innerAnimator.setDuration(350);
        innerAnimator.start();


        //位移动画
        allAnimatior.playSequentially(innerAnimator, getBezierAnimator(loveIv));

        return allAnimatior;
    }

    private Animator getBezierAnimator(final ImageView iv) {
        PointF start = new PointF(mWidth / 2 - mDrawableWidth / 2, mHeight - mDrawableHeight);

        //确保p2点 的y值 一定要大于p1 点的y值
        PointF p1 = getPintF(1);
        //new PointF(random.nextInt(mWidth),random.nextInt(mHeight/2));
        PointF p2 = getPintF(2);
        ;//new PointF(random.nextInt(mWidth),random.nextInt(mHeight)+mHeight/2);
        PointF end = new PointF(random.nextInt(mWidth - mDrawableWidth), 0);


        LoveTypeEvaluator typeEvaluator = new LoveTypeEvaluator(p1, p2);


        //ofFloat 第一个参数     LoveTypeEvaluator第二个参数 start  第3个参数 end
        ValueAnimator bezierAnimator = ValueAnimator.ofObject(typeEvaluator, start, end);
        bezierAnimator.setDuration(3000);

        //加入一些插值器
        bezierAnimator.setInterpolator(mInterpolator[random.nextInt(mInterpolator.length-1)]);
        bezierAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PointF pointF = (PointF) valueAnimator.getAnimatedValue();

                iv.setX(pointF.x);
                iv.setY(pointF.y);

                float t = valueAnimator.getAnimatedFraction();
                iv.setAlpha(1 - t + 0.2f);

            }
        });
        return bezierAnimator;
    }


    private PointF getPintF(int index) {
        return new PointF(random.nextInt(mWidth - mDrawableWidth / 2), random.nextInt(mHeight / 2) + (index - 1) * (mHeight / 2));
    }
}
