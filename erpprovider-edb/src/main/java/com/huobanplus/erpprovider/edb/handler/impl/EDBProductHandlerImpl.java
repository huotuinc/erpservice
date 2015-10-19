package com.huobanplus.erpprovider.edb.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobanplus.erpprovider.edb.bean.EDBSysData;
import com.huobanplus.erpprovider.edb.handler.BaseHandler;
import com.huobanplus.erpprovider.edb.handler.EDBProductHandler;
import com.huobanplus.erpprovider.edb.support.SimpleMonitor;
import com.huobanplus.erpprovider.edb.util.Constant;
import com.huobanplus.erpservice.common.util.HttpUtil;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.model.ERPInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import com.huobanplus.erpservice.eventhandler.model.Monitor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by allan on 2015/7/28.
 */
@Component
public class EDBProductHandlerImpl extends BaseHandler implements EDBProductHandler {
    @Override
    public EventResult getProInventoryInfo(ERPInfo erpInfo) throws IOException {
        EDBSysData sysData = new ObjectMapper().readValue(erpInfo.getSysDataJson(), EDBSysData.class);
        Map<String, String> requestData = getSysRequestData(Constant.GET_PRO_INFO, sysData);
        Map<String, String> signMap = new TreeMap<>(requestData);
        requestData.put("sign", getSign(signMap, sysData));

        String responseData = HttpUtil.getInstance().doGet(Constant.REQUEST_URI, requestData);
        if (responseData == null) {
            return EventResult.resultWith(EventResultEnum.ERROR, responseData);
        }
        return EventResult.resultWith(EventResultEnum.SUCCESS, responseData);
    }
}
