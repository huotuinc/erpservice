/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huobanplus.erpservice.datacenter.entity;

import com.huobanplus.erpservice.datacenter.common.ERPTypeEnum;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by liual on 2015-10-28.
 */
@Data
@Entity
@Table(name = "ERP_BaseConfig")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ERPBaseConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer customerId;
    @Column(updatable = false)
    private String appKey;
    private String token;
    private String secretKey;
    /**
     * 是否开通erp，1表示开通
     */
    private int isOpen;

    /**
     * 是否开启库存同步
     * 1：开通
     * 0：不开通
     */
    private int isSyncInventory;

    /**
     * 是否开启发货同步
     * 1：开通
     * 0：不开通
     */
    private int isSyncDelivery = 1;

    /**
     * 是否开启渠道订单同步
     * 1：开通
     * 0：不开通
     */
    private int isSyncChannelOrder;

    private ERPTypeEnum.UserType erpUserType;
}
