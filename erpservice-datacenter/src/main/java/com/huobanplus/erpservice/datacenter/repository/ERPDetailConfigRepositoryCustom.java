/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huobanplus.erpservice.datacenter.repository;

import com.huobanplus.erpservice.datacenter.common.ERPTypeEnum;
import com.huobanplus.erpservice.datacenter.entity.ERPDetailConfigEntity;
import com.huobanplus.erpservice.datacenter.entity.ERPSysDataInfo;

import java.util.List;

/**
 * Created by liual on 2015-10-29.
 */
public interface ERPDetailConfigRepositoryCustom {
    /**
     * 根据系统信息得到商户erp配置信息
     *
     * @param sysDataInfos
     * @return
     */
    ERPDetailConfigEntity findBySysData(List<ERPSysDataInfo> sysDataInfos, ERPTypeEnum.ProviderType providerType, ERPTypeEnum.UserType erpUserType);
}
