package com.maplefall.wind.mg.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;

public class AnimationUtil {

    private final int mAnimateTime = 600;

    /**
     * 淡入淡出动画
     * @param isIn true 为淡入，false 为淡出
     * @return 返回动画显示效果参数
     */
    public AnimationSet fadeInOrOut(boolean isIn) {
        AnimationSet aniSet = new AnimationSet(true);
        AlphaAnimation alpAni = null;
        if(isIn) {
            alpAni = new AlphaAnimation(0, 1);
        } else {
            alpAni = new AlphaAnimation(1, 0);
        }
        alpAni.setDuration(mAnimateTime);
        aniSet.addAnimation(alpAni);
        return aniSet;
    }
}
