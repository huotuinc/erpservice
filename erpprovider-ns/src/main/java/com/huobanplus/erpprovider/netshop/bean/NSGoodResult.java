package com.huobanplus.erpprovider.netshop.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Created by liual on 2015-08-26.
 */
@JacksonXmlRootElement(localName = "Goods")
public class NSGoodResult {
    @JacksonXmlProperty(localName = "Ware")
    private List<NSGoodDetailResult> detailResults;
    private int Result;
    private int TotalCount;
    private String Cause;

    public List<NSGoodDetailResult> getDetailResults() {
        return detailResults;
    }

    public void setDetailResults(List<NSGoodDetailResult> detailResults) {
        this.detailResults = detailResults;
    }

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public String getCause() {
        return Cause;
    }

    public void setCause(String cause) {
        Cause = cause;
    }
}
