/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpservice.common.ienum;

/**
 * erp订单状态
 * Created by allan on 4/15/16.
 */
public enum OrderSyncStatus1 implements ICommonEnum {
    WAITING_FOR_PUSHING(0, "待推送"),
    PUSHING_SUCCESS(1, "已推送待发货"),
    DELIVERYED(2, "发货已同步"),
    CANCELD(3, "已取消");
    private int code;
    private String name;

    OrderSyncStatus1(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
