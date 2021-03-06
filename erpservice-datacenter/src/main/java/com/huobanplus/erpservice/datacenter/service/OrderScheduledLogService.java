/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpservice.datacenter.service;

import com.huobanplus.erpservice.datacenter.entity.OrderScheduledLog;

/**
 * Created by allan on 12/23/15.
 */
public interface OrderScheduledLogService {
    OrderScheduledLog save(OrderScheduledLog scheduledLog);

    OrderScheduledLog findFirst(int customerId);
}
