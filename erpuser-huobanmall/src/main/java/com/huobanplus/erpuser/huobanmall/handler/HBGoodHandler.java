/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpuser.huobanmall.handler;

import com.huobanplus.erpservice.datacenter.model.ProInventoryInfo;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;

import java.util.List;

/**
 * Created by allan on 8/3/16.
 */
public interface HBGoodHandler {
    /**
     * 同步库存
     *
     * @param proInventoryInfoList
     * @param erpUserInfo
     * @return
     */
    EventResult syncProInventory(List<ProInventoryInfo> proInventoryInfoList, ERPUserInfo erpUserInfo);
}
