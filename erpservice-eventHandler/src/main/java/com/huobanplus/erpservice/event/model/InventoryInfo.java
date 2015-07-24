package com.huobanplus.erpservice.event.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/7/16.
 */
public class InventoryInfo extends BaseBean {

    //出库单号
    private String outStorageNo;
    //运费
    private String freight;
    //运费分摊方式1:按产品数量2：按产品重量默认为1
    private String freightAvgway;
    //入库单号
    private String inStorageNo;
    //条形码
    private String barCode;
    //入库数量
    private String instorageNum;
    //出库数量
    private String outstorageNum;
    //入库类型；全部:1/其他入库:10/产成品入库:11/原料入库:12/盘盈入库:13/维修入库:14/差错入库:15/退货入库:3/归还入库:4/调拨入库:6/正常入库:8/采购入库:9
    private int type;
    //日期类型--入库日期in_time,制单日期create_time,审核日期examine_time
    private String dateType;
    //开始时间;所查订单的开始时间,和日期类型配合使用.
    private String beginTime;
    //结束时间;所查订单的结束时间,和日期类型配合使用.如果没输入,则默认为此时此刻.
    private String endTime;
    //仓库名称，用于查询指定仓库的入库单
    private String storageName;
    //入库状态.0:未审核；1：已审核；2：已作废
    private int instorageStatus;
    //页大小，如果不填，则默认为200.每页最多返回记录数为200.
    private int pageSize;
    //当前页，如果不填，默认为1
    private int pageNo;
    //导入标记:不导入,未导入,已导入,已处理,已取消
    private String importMark;
    //入库类型编号
    private String typeNo;
    //供应商编码
    private String provider;
    //仓库
    private String storage;
    //制单员
    private String creater;
    //制单时间
    private String createTime;
    //入库时间
    private String inTime;
    //质检员
    private String qualityInspctor;
    //质检时间
    private String inspctTime;
    //质检结果
    private String inspctResult;
    //审核人
    private String examiner;
    //审核时间
    private String examineTime;
    //发货单号
    private String sendTid;
    //入库原因
    private String inReason;
    //成本价格
    private String cost;
    //来源单号
    private String SourceTid;
    //采购费用
    private String purchaseFee;
    //合同总额
    private String contractMoney;
    //相关单号
    private String relevantTid;
    //汇率
    private String rate;
    //币种
    private String currency;
    //外部合同号
    private String outContractTid;
    //物流公司
    private String logistics;
    //快递单号
    private String expressTid;
    //运费承担方
    private String freightPayer;
    //入库备注
    private String remarks;
    //运费均摊模式;按产品数量，按产品重量
    private String freightMode;
    //总记录数
    private String totalCount;
    //子节点
    private InventoryInfo[] instoreTid;
    //仓库编码
    private String storageNo;
    //来源：不对外提供，固定默认值
    private String listSource;
    //其他费用
    private String otherCost;
    //外部合同单号
    private String outPactNo;
    //产品明细编号
    private String productItemNo;
    //库位编号
    private String locationNo;
    //批次
    private String batch;
    //到期时间
    private String expireTime;
    //出库类型（可在档案管理-仓库档案-出库类型设置中查看）
    private String outstorageType;
    //出库时间
    private String outstorageTime;
    //返厂供应商编号：在采购管理—供应商档案里查看
    private String supplieNo;
    //原始入库单号
    private String YSInstorageNo;
    //出库备注
    private String outStorageRemark;
    //出库价
    private String outstoragePrice;
    //运费均摊
    private String freightAvg;
    //出库状态
    private String outstorageStatus;
    //出库类型名称
    private String outStoreTypeName;

    public String getOutStorageNo() {
        return outStorageNo;
    }

    public void setOutStorageNo(String outStorageNo) {
        this.outStorageNo = outStorageNo;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getFreightAvgway() {
        return freightAvgway;
    }

    public void setFreightAvgway(String freightAvgway) {
        this.freightAvgway = freightAvgway;
    }

    public String getInStorageNo() {
        return inStorageNo;
    }

    public void setInStorageNo(String inStorageNo) {
        this.inStorageNo = inStorageNo;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getInstorageNum() {
        return instorageNum;
    }

    public void setInstorageNum(String instorageNum) {
        this.instorageNum = instorageNum;
    }

    public String getOutstorageNum() {
        return outstorageNum;
    }

    public void setOutstorageNum(String outstorageNum) {
        this.outstorageNum = outstorageNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public int getInstorageStatus() {
        return instorageStatus;
    }

    public void setInstorageStatus(int instorageStatus) {
        this.instorageStatus = instorageStatus;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getImportMark() {
        return importMark;
    }

    public void setImportMark(String importMark) {
        this.importMark = importMark;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getQualityInspctor() {
        return qualityInspctor;
    }

    public void setQualityInspctor(String qualityInspctor) {
        this.qualityInspctor = qualityInspctor;
    }

    public String getInspctTime() {
        return inspctTime;
    }

    public void setInspctTime(String inspctTime) {
        this.inspctTime = inspctTime;
    }

    public String getInspctResult() {
        return inspctResult;
    }

    public void setInspctResult(String inspctResult) {
        this.inspctResult = inspctResult;
    }

    public String getExaminer() {
        return examiner;
    }

    public void setExaminer(String examiner) {
        this.examiner = examiner;
    }

    public String getExamineTime() {
        return examineTime;
    }

    public void setExamineTime(String examineTime) {
        this.examineTime = examineTime;
    }

    public String getSendTid() {
        return sendTid;
    }

    public void setSendTid(String sendTid) {
        this.sendTid = sendTid;
    }

    public String getInReason() {
        return inReason;
    }

    public void setInReason(String inReason) {
        this.inReason = inReason;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSourceTid() {
        return SourceTid;
    }

    public void setSourceTid(String sourceTid) {
        SourceTid = sourceTid;
    }

    public String getPurchaseFee() {
        return purchaseFee;
    }

    public void setPurchaseFee(String purchaseFee) {
        this.purchaseFee = purchaseFee;
    }

    public String getContractMoney() {
        return contractMoney;
    }

    public void setContractMoney(String contractMoney) {
        this.contractMoney = contractMoney;
    }

    public String getRelevantTid() {
        return relevantTid;
    }

    public void setRelevantTid(String relevantTid) {
        this.relevantTid = relevantTid;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOutContractTid() {
        return outContractTid;
    }

    public void setOutContractTid(String outContractTid) {
        this.outContractTid = outContractTid;
    }

    public String getLogistics() {
        return logistics;
    }

    public void setLogistics(String logistics) {
        this.logistics = logistics;
    }

    public String getExpressTid() {
        return expressTid;
    }

    public void setExpressTid(String expressTid) {
        this.expressTid = expressTid;
    }

    public String getFreightPayer() {
        return freightPayer;
    }

    public void setFreightPayer(String freightPayer) {
        this.freightPayer = freightPayer;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getFreightMode() {
        return freightMode;
    }

    public void setFreightMode(String freightMode) {
        this.freightMode = freightMode;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public InventoryInfo[] getInstoreTid() {
        return instoreTid;
    }

    public void setInstoreTid(InventoryInfo[] instoreTid) {
        this.instoreTid = instoreTid;
    }

    public String getStorageNo() {
        return storageNo;
    }

    public void setStorageNo(String storageNo) {
        this.storageNo = storageNo;
    }

    public String getListSource() {
        return listSource;
    }

    public void setListSource(String listSource) {
        this.listSource = listSource;
    }

    public String getOtherCost() {
        return otherCost;
    }

    public void setOtherCost(String otherCost) {
        this.otherCost = otherCost;
    }

    public String getOutPactNo() {
        return outPactNo;
    }

    public void setOutPactNo(String outPactNo) {
        this.outPactNo = outPactNo;
    }

    public String getProductItemNo() {
        return productItemNo;
    }

    public void setProductItemNo(String productItemNo) {
        this.productItemNo = productItemNo;
    }

    public String getLocationNo() {
        return locationNo;
    }

    public void setLocationNo(String locationNo) {
        this.locationNo = locationNo;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getOutstorageType() {
        return outstorageType;
    }

    public void setOutstorageType(String outstorageType) {
        this.outstorageType = outstorageType;
    }

    public String getOutstorageTime() {
        return outstorageTime;
    }

    public void setOutstorageTime(String outstorageTime) {
        this.outstorageTime = outstorageTime;
    }

    public String getSupplieNo() {
        return supplieNo;
    }

    public void setSupplieNo(String supplieNo) {
        this.supplieNo = supplieNo;
    }

    public String getYSInstorageNo() {
        return YSInstorageNo;
    }

    public void setYSInstorageNo(String YSInstorageNo) {
        this.YSInstorageNo = YSInstorageNo;
    }

    public String getOutStorageRemark() {
        return outStorageRemark;
    }

    public void setOutStorageRemark(String outStorageRemark) {
        this.outStorageRemark = outStorageRemark;
    }

    public String getOutstoragePrice() {
        return outstoragePrice;
    }

    public void setOutstoragePrice(String outstoragePrice) {
        this.outstoragePrice = outstoragePrice;
    }

    public String getFreightAvg() {
        return freightAvg;
    }

    public void setFreightAvg(String freightAvg) {
        this.freightAvg = freightAvg;
    }

    public String getOutstorageStatus() {
        return outstorageStatus;
    }

    public void setOutstorageStatus(String outstorageStatus) {
        this.outstorageStatus = outstorageStatus;
    }

    public String getOutStoreTypeName() {
        return outStoreTypeName;
    }

    public void setOutStoreTypeName(String outStoreTypeName) {
        this.outStoreTypeName = outStoreTypeName;
    }
}