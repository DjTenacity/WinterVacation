package com.lovedj.studyproject;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Comment:自定义路径属性动画
 *
 * @author :DJ鼎尔东 / 1757286697@qq.cn
 * @version : Administrator1.0
 * @date : 2018/1/24
 */
public class LoveTypeEvaluator implements TypeEvaluator<PointF> {
    //两个控制点
    private PointF p1, p2;

    public LoveTypeEvaluator(PointF p1, PointF p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public PointF evaluate(float v, PointF start, PointF end) {
        //v  [0,1]   开始点   结束点

        PointF pointF = new PointF();
        //3阶贝瑟尔曲线
        pointF.x = start.x * (1 - v) * (1 - v) * (1 - v)
                + 3 * p1.x * v * (1 - v) * (1 - v)
                + 3 * p2.x * v * v * (1 - v)
                + end.x * v * v * v;

        pointF.y = start.y * (1 - v) * (1 - v) * (1 - v)
                + 3 * p1.y * v * (1 - v) * (1 - v)
                + 3 * p2.y * v * v * (1 - v)
                + end.x * v * v * v;

        return pointF;
    }
}
