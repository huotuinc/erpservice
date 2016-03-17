/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.edb.handler.impl;

import com.huobanplus.erpprovider.edb.bean.EDBSysData;
import com.huobanplus.erpprovider.edb.handler.BaseHandler;
import com.huobanplus.erpprovider.edb.handler.EDBProductHandler;
import com.huobanplus.erpprovider.edb.util.EDBConstant;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by allan on 2015/7/28.
 */
@Component
public class EDBProductHandlerImpl extends BaseHandler implements EDBProductHandler {
    @Override
    public EventResult getProInventoryInfo(EDBSysData sysData, int pageIndex) throws IOException {
        Map<String, Object> requestData = getSysRequestData(EDBConstant.GET_PRO_INFO, sysData);
        requestData.put("page_size", EDBConstant.PAGE_SIZE);

        Map<String, Object> signMap = new TreeMap<>(requestData);
        requestData.put("sign", getSign(signMap, sysData));

//        String responseData = HttpUtil.getInstance().doGet(EDBConstant.REQUEST_URI, requestData);
//        if (responseData == null) {
//            return EventResult.resultWith(EventResultEnum.ERROR, responseData);
//        }
//        return EventResult.resultWith(EventResultEnum.SUCCESS, responseData);
        return null;
    }
}
