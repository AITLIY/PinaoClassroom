package com.yiyin.aobosh.Interface;

import com.yiyin.aobosh.bean.LessonOrder;

/**
 * Created by ALIY on 2018/12/22 0022.
 */

public interface OrderClickInterface {

    void onOrder(LessonOrder order);
    void onCancel(LessonOrder order);
    void onPay(LessonOrder order);
    void onEvaluate(LessonOrder order);
}
