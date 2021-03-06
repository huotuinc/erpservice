/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.edb.bean;


import lombok.Getter;
import lombok.Setter;

/**
 * EDB的系统数据
 * Created by allan on 2015/8/4.
 */
@Getter
@Setter
public class EDBSysData {
    private String requestUrl;
    private String dbHost;
    private String appKey;
    private String appSecret;
    private String token;
    private String format;
    private String v;
    private String slencry;
    private String ip;
    private String shopId;
    private String storageId;
    private String express;
    private String beginTime;
    /**
     * 指定库存同步的仓库编号集，逗号分隔
     */
    private String storageIds;
}
