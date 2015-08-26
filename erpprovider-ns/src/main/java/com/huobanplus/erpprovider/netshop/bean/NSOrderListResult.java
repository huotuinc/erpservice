package com.huobanplus.erpprovider.netshop.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * 订单列表返回数据
 * Created by liual on 2015-08-25.
 */
@JacksonXmlRootElement(localName = "Order")
public class NSOrderListResult {
    @JacksonXmlProperty(localName = "OrderNO")
    private List<String> orderNo;
    @JacksonXmlProperty(localName = "OrderCount")
    private String orderCount;
    @JacksonXmlProperty(localName = "Page")
    private String page;
    @JacksonXmlProperty(localName = "Result")
    private String result;
    @JacksonXmlProperty(localName = "Cause")
    private String cause;

    public List<String> getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(List<String> orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}