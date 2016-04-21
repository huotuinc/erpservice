/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpservice.datacenter.service.logs.impl;

import com.huobanplus.erpservice.datacenter.common.ERPTypeEnum;
import com.huobanplus.erpservice.datacenter.entity.logs.OrderShipSyncLog;
import com.huobanplus.erpservice.datacenter.repository.logs.OrderShipSyncLogRepository;
import com.huobanplus.erpservice.datacenter.service.logs.OrderShipSyncLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Created by allan on 4/21/16.
 */
@Service
public class OrderShipSyncLogServiceImpl implements OrderShipSyncLogService {
    @Autowired
    private OrderShipSyncLogRepository orderShipSyncLogRepository;

    @Override
    public OrderShipSyncLog save(OrderShipSyncLog orderShipSyncLog) {
        return orderShipSyncLogRepository.save(orderShipSyncLog);
    }

    @Override
    public OrderShipSyncLog findTop(int customerId, ERPTypeEnum.ProviderType providerType) {
        return orderShipSyncLogRepository.findTopByCustomerIdAndProviderTypeOrderBySyncTime(customerId, providerType);
    }

    @Override
    public Page<OrderShipSyncLog> findAll(int pageIndex, int pageSize) {
        // TODO: 4/21/16
        return null;
    }
}