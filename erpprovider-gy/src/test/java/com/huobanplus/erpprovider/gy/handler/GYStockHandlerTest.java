package com.huobanplus.erpprovider.gy.handler;

import com.huobanplus.erpprovider.gy.GYTestBase;
import com.huobanplus.erpprovider.gy.common.GYSysData;
import com.huobanplus.erpprovider.gy.search.GYStockSearch;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wuxiongliu on 2016/6/23.
 */
public class GYStockHandlerTest extends GYTestBase {

    @Autowired
    private GYStockHandler gyStockHandler;

    private GYSysData mockGySysData;

    @Before
    public void setUp(){
        mockGySysData = new GYSysData();

        mockGySysData.setRequestUrl("https://demo.guanyierp.com/erpapi/rest/erp_open");
        mockGySysData.setAppKey("143158");
        mockGySysData.setSessionkey("48df26a5f53d47f2bf7085376ba39164");
        mockGySysData.setSecret("a4384907606e435bbf594c420760d29a");
        mockGySysData.setWarehouseCode("kucangbao");
    }

    @Test
    public void testStockQuery(){
        GYStockSearch gyStockSearch = new GYStockSearch();
        EventResult eventResult = gyStockHandler.stockQuery(gyStockSearch,mockGySysData);
        System.out.println(eventResult.getData());
        System.out.println(eventResult.getResultMsg());
        System.out.println(eventResult.getResultCode());

    }
}
