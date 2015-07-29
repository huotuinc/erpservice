package com.huobanplus.erpprovicer.huobanmall.controller;

import com.huobanplus.erpprovicer.huobanmall.common.AuthBean;
import com.huobanplus.erpprovicer.huobanmall.common.SimpleMonitor;
import com.huobanplus.erpprovicer.huobanmall.service.InventoryEvent;
import com.huobanplus.erpprovicer.huobanmall.util.Constant;
import com.huobanplus.erpprovicer.huobanmall.util.SignStrategy;
import com.huobanplus.erpservice.event.model.BaseResult;
import com.huobanplus.erpservice.event.model.InventoryInfo;
import com.huobanplus.erpservice.event.model.Monitor;
import com.huobanplus.erpservice.event.model.OrderInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 类描述：库存事件处理类
 *
 * @author aaron
 * @version V1.0
 * @since 2015年7月25日 下午4:30:43
 */
public class InventoryController {

    @Resource
    InventoryEvent inventoryEvent;

    /**
     * （批量）同步库存信息
     * {1、storageIds只包含一个库存编号时，只同步一个库存信息 2、storageIds包含多个库存编号时，批量同步库存信息}
     * storageIds包含多个库存编号时 每个库存编号以逗号隔开
     *
     * @param sign       授权签名
     * @param uCode      接入码，用于验证请求的有效性。主要用于区分店铺
     * @param secret     密钥
     * @param timeStamp  时间戳
     * @param mType      方法名，不同接口分别传入不同的方法值。
     * @param signType   加密方式，默认为MD5
     * @param erpName    erp名称
     * @param storageIds 库存编号
     * @return 同步信息结果
     */
    @ResponseBody
    @RequestMapping(value = "/syncInventory", method = RequestMethod.POST)
    public Monitor<BaseResult> syncInventory(String sign,
                                             String uCode,
                                             String secret,
                                             String timeStamp,
                                             String mType,
                                             @RequestParam(value = "signType", required = false, defaultValue = "MD5") String signType,
                                             String erpName,
                                             String storageIds) {

        AuthBean authBean = new AuthBean();
        authBean.setSign(sign);
        authBean.setmType(mType);
        authBean.setSecret(secret);
        authBean.setSignType(signType);
        authBean.setTimeStamp(timeStamp);
        authBean.setuCode(uCode);

        BaseResult baseResult;
        String signStr = SignStrategy.getInstance().buildSign(authBean).getSign();
        if (null != signStr && signStr.equals(sign)) {
            baseResult = new InventoryInfo();
            baseResult.setResultMsg("sign验证失败");
            baseResult.setResultCode(Constant.REQUEST_SING_ERROR);
            return new SimpleMonitor<BaseResult>(baseResult);
        } else {
            if (StringUtils.isEmpty(storageIds)) {
                baseResult = new BaseResult();
                baseResult.setResultMsg("传入库存编号无效");
                baseResult.setResultCode(Constant.REQUEST_INAVLID_PARAMETER);
                return new SimpleMonitor<BaseResult>(baseResult);
            } else {
                return inventoryEvent.syncInventory(authBean, storageIds);
            }
        }
    }


    /**
     * 提交（新增）入库信息
     *
     * @param sign       授权签名
     * @param uCode      接入码，用于验证请求的有效性。主要用于区分店铺
     * @param secret     密钥
     * @param timeStamp  时间戳
     * @param mType      方法名，不同接口分别传入不同的方法值。
     * @param signType   加密方式，默认为MD5
     * @param erpName    erp名称
     * @param inStorageNo 入库单编号
     * @param freight 运费
     * @param freightAvgway 运费分摊方式1:按产品数量2：按产品重量默认为1
     * @param barCode 条形码
     * @param instorageNum 入库数量
     * @param type 入库类型；全部:1/其他入库:10/产成品入库:11/原料入库:12/盘盈入库:13/维修入库:14/差错入库:15/退货入库:3/归还入库:4/调拨入库:6/正常入库:8/采购入库:9
     * @param dateType 日期类型--入库日期in_time,制单日期create_time,审核日期examine_time
     * @param beginTime 开始时间;所查订单的开始时间,和日期类型配合使用.
     * @param endTime 结束时间;所查订单的结束时间,和日期类型配合使用.如果没输入,则默认为此时此刻.
     * @param storageName 仓库名称，用于查询指定仓库的入库单
     * @param instorageStatus 入库状态.0:未审核；1：已审核；2：已作废
     * @param importMark 导入标记:不导入,未导入,已导入,已处理,已取消
     * @param typeNo 入库类型编号
     * @param provider 供应商编码
     * @param storage 仓库
     * @param creater 制单员
     * @param createTime 制单时间
     * @param inTime 入库时间
     * @param qualityInspctor 质检员
     * @param inspctTime 质检时间
     * @param inspctResult 质检结果
     * @param examiner 审核人
     * @param examineTime 审核时间
     * @param inReason 入库原因
     * @param cost 成本价格
     * @param SourceTid 来源单号
     * @param purchaseFee 采购费用
     * @param contractMoney 合同总额
     * @param relevantTid 相关单号
     * @param rate 汇率
     * @param currency 币种
     * @param outContractTid 外部合同号
     * @param logistics 物流公司
     * @param expressTid 快递单号
     * @param freightPayer 运费承担方
     * @param remarks 入库备注
     * @param freightMode 运费均摊模式;按产品数量，按产品重量
     * @param storageNo 仓库编码
     * @param listSource 来源：不对外提供，固定默认值
     * @param otherCost 其他费用
     * @param outPactNo 外部合同单号
     * @param productItemNo 产品明细编号
     * @param locationNo 库位编号
     * @param batch 批次
     * @param expireTime 到期时间
     * @param supplieNo 返厂供应商编号：在采购管理—供应商档案里查看
     * @param YSInstorageNo 原始入库单号
     * @param freightAvg 运费均摊
     * @return 入库结果（成功、失败）
     */
    @ResponseBody
    @RequestMapping(value = "/commitInStorage", method = RequestMethod.POST)
    public Monitor<BaseResult> commitInStorage(String sign,
                                               String uCode,
                                               String secret,
                                               String timeStamp,
                                               String mType,
                                               @RequestParam(value = "signType", required = false, defaultValue = "MD5") String signType,
                                               String erpName,
                                               String inStorageNo,
                                               @RequestParam(value = "freight", required = false) double freight,
                                               @RequestParam(value = "freightAvgway", required = false) String freightAvgway,
                                               @RequestParam(value = "barCode", required = false) String barCode,
                                               @RequestParam(value = "instorageNum", required = false) int instorageNum,
                                               @RequestParam(value = "type", required = false) int type,
                                               @RequestParam(value = "dateType", required = false) String dateType,
                                               @RequestParam(value = "beginTime", required = false) long beginTime,
                                               @RequestParam(value = "endTime", required = false) long endTime,
                                               @RequestParam(value = "storageName", required = false) String storageName,
                                               @RequestParam(value = "instorageStatus", required = false) int instorageStatus,
                                               @RequestParam(value = "importMark", required = false) String importMark,
                                               @RequestParam(value = "typeNo", required = false) String typeNo,
                                               @RequestParam(value = "provider", required = false) String provider,
                                               @RequestParam(value = "storage", required = false) String storage,
                                               @RequestParam(value = "creater", required = false) String creater,
                                               @RequestParam(value = "createTime", required = false) long createTime,
                                               @RequestParam(value = "inTime", required = false) long inTime,
                                               @RequestParam(value = "qualityInspctor", required = false) String qualityInspctor,
                                               @RequestParam(value = "inspctTime", required = false) long inspctTime,
                                               @RequestParam(value = "inspctResult", required = false) String inspctResult,
                                               @RequestParam(value = "examiner", required = false) String examiner,
                                               @RequestParam(value = "examineTime", required = false) long examineTime,
                                               @RequestParam(value = "inReason", required = false) String inReason,
                                               @RequestParam(value = "cost", required = false) double cost,
                                               @RequestParam(value = "SourceTid", required = false) String SourceTid,
                                               @RequestParam(value = "purchaseFee", required = false) double purchaseFee,
                                               @RequestParam(value = "contractMoney", required = false) double contractMoney,
                                               @RequestParam(value = "relevantTid", required = false) String relevantTid,
                                               @RequestParam(value = "rate", required = false) double rate,
                                               @RequestParam(value = "currency", required = false) String currency,
                                               @RequestParam(value = "outContractTid", required = false) String outContractTid,
                                               @RequestParam(value = "logistics", required = false) String logistics,
                                               @RequestParam(value = "expressTid", required = false) String expressTid,
                                               @RequestParam(value = "freightPayer", required = false) String freightPayer,
                                               @RequestParam(value = "remarks", required = false) String remarks,
                                               @RequestParam(value = "freightMode", required = false) String freightMode,
                                               @RequestParam(value = "storageNo", required = false) String storageNo,
                                               @RequestParam(value = "listSource", required = false) String listSource,
                                               @RequestParam(value = "otherCost", required = false) double otherCost,
                                               @RequestParam(value = "outPactNo", required = false) String outPactNo,
                                               @RequestParam(value = "productItemNo", required = false) String productItemNo,
                                               @RequestParam(value = "locationNo", required = false) String locationNo,
                                               @RequestParam(value = "batch", required = false) String batch,
                                               @RequestParam(value = "expireTime", required = false) long expireTime,
                                               @RequestParam(value = "supplieNo", required = false) String supplieNo,
                                               @RequestParam(value = "YSInstorageNo", required = false) String YSInstorageNo,
                                               @RequestParam(value = "freightAvg", required = false) double freightAvg
                                               )
    {

        AuthBean authBean = new AuthBean();
        authBean.setSign(sign);
        authBean.setmType(mType);
        authBean.setSecret(secret);
        authBean.setSignType(signType);
        authBean.setTimeStamp(timeStamp);
        authBean.setuCode(uCode);

        BaseResult baseResult;
        String signStr = SignStrategy.getInstance().buildSign(authBean).getSign();
        if (null != signStr && signStr.equals(sign)) {
            baseResult = new InventoryInfo();
            baseResult.setResultMsg("sign验证失败");
            baseResult.setResultCode(Constant.REQUEST_SING_ERROR);
            return new SimpleMonitor<BaseResult>(baseResult);
        } else {

            InventoryInfo inventory = new InventoryInfo();
            inventory.setInStorageNo(inStorageNo);
            inventory.setFreight(freight);
            inventory.setFreightAvgway(freightAvgway);
            inventory.setBarCode(barCode);
            inventory.setInstorageNum(instorageNum);
            inventory.setType(type);
            inventory.setDateType(dateType);
            inventory.setBeginTime(new Date(beginTime));
            inventory.setEndTime(new Date(endTime));
            inventory.setStorageName(storageName);
            inventory.setInstorageStatus(instorageStatus);
            inventory.setImportMark(importMark);
            inventory.setTypeNo(typeNo);
            inventory.setProvider(provider);
            inventory.setStorage(storage);
            inventory.setCreater(creater);
            inventory.setCreateTime(new Date(createTime));
            inventory.setInTime(new Date(inTime));
            inventory.setQualityInspctor(qualityInspctor);
            inventory.setInspctTime(new Date(inspctTime));
            inventory.setInspctResult(inspctResult);
            inventory.setExaminer(examiner);
            inventory.setExamineTime(new Date(examineTime));
            inventory.setInReason(inReason);
            inventory.setCost(cost);
            inventory.setSourceTid(SourceTid);
            inventory.setPurchaseFee(purchaseFee);
            inventory.setContractMoney(contractMoney);
            inventory.setRelevantTid(relevantTid);
            inventory.setRate(rate);
            inventory.setCurrency(currency);
            inventory.setOutContractTid(outContractTid);
            inventory.setLogistics(logistics);
            inventory.setExpressTid(expressTid);
            inventory.setFreightPayer(freightPayer);
            inventory.setRemarks(remarks);
            inventory.setFreightMode(freightMode);
            inventory.setStorageNo(storageNo);
            inventory.setListSource(listSource);
            inventory.setOtherCost(otherCost);
            inventory.setOutPactNo(outPactNo);
            inventory.setProductItemNo(productItemNo);
            inventory.setLocationNo(locationNo);
            inventory.setBatch(batch);
            inventory.setExpireTime(new Date(expireTime));
            inventory.setSupplieNo(supplieNo);
            inventory.setYSInstorageNo(YSInstorageNo);
            inventory.setFreightAvg(freightAvg);
            return inventoryEvent.commitInStorage(authBean, inventory);
        }

    }

    /**
     * （批量）获取入库存信息
     * {1、inStorageIds只包含一个入库编号时，只获取一个入库信息 2、inStorageIds包含多个入库编号时，批量获取入库信息}
     * inStorageIds包含多个库存编号时 每个入库编号以逗号隔开
     *
     * @param sign       授权签名
     * @param uCode      接入码，用于验证请求的有效性。主要用于区分店铺
     * @param secret     密钥
     * @param timeStamp  时间戳
     * @param mType      方法名，不同接口分别传入不同的方法值。
     * @param signType   加密方式，默认为MD5
     * @param erpName    erp名称
     * @param inStorageIds 入库编号
     * @return 返回入库信息
     */
    @ResponseBody
    @RequestMapping(value = "/obtainInStorage", method = RequestMethod.POST)
    public Monitor<InventoryInfo> obtainInStorage(String sign,
                                             String uCode,
                                             String secret,
                                             String timeStamp,
                                             String mType,
                                             @RequestParam(value = "signType", required = false, defaultValue = "MD5") String signType,
                                             String erpName,
                                             String inStorageIds) {

        AuthBean authBean = new AuthBean();
        authBean.setSign(sign);
        authBean.setmType(mType);
        authBean.setSecret(secret);
        authBean.setSignType(signType);
        authBean.setTimeStamp(timeStamp);
        authBean.setuCode(uCode);
        InventoryInfo inventoryInfo;

        String signStr = SignStrategy.getInstance().buildSign(authBean).getSign();
        if (null != signStr && signStr.equals(sign)) {
            inventoryInfo = new InventoryInfo();
            inventoryInfo.setResultMsg("sign验证失败");
            inventoryInfo.setResultCode(Constant.REQUEST_SING_ERROR);
            return new SimpleMonitor<InventoryInfo>(inventoryInfo);
        } else {
            if (StringUtils.isEmpty(inStorageIds)) {
                inventoryInfo = new InventoryInfo();
                inventoryInfo.setResultMsg("传入入库编号无效");
                inventoryInfo.setResultCode(Constant.REQUEST_INAVLID_PARAMETER);
                return new SimpleMonitor<InventoryInfo>(inventoryInfo);
            } else {
                return inventoryEvent.obtainInStorage(authBean, inStorageIds);
            }
        }
    }

    /**
     * 提交（新增）出库信息
     * @param sign 授权签名
     * @param uCode 接入码，用于验证请求的有效性。主要用于区分店铺
     * @param secret 密钥
     * @param timeStamp 时间戳
     * @param mType 方法名，不同接口分别传入不同的方法值。
     * @param signType 加密方式，默认为MD5
     * @param erpName erp名称
     * @param outStorageNo 出库单编号
     * @param freight 运费
     * @param freightAvgway 运费分摊方式1:按产品数量2：按产品重量默认为1
     * @param barCode 条形码
     * @param outstorageNum 出库数量
     * @param dateType 日期类型--入库日期in_time,制单日期create_time,审核日期examine_time
     * @param beginTime 开始时间;所查订单的开始时间,和日期类型配合使用.
     * @param endTime 结束时间;所查订单的结束时间,和日期类型配合使用.如果没输入,则默认为此时此刻.
     * @param storageName 仓库名称，用于查询指定仓库的入库单
     * @param importMark 导入标记:不导入,未导入,已导入,已处理,已取消
     * @param provider 供应商编码
     * @param storage 仓库
     * @param creater  制单员
     * @param createTime 制单时间
     * @param qualityInspctor  质检员
     * @param inspctTime 质检时间
     * @param inspctResult 质检结果
     * @param examiner 审核人
     * @param examineTime 审核时间
     * @param cost 成本价格
     * @param SourceTid 来源单号
     * @param purchaseFee 采购费用
     * @param contractMoney 合同总额
     * @param relevantTid 相关单号
     * @param rate 汇率
     * @param currency 币种
     * @param outContractTid 外部合同号
     * @param logistics 物流公司
     * @param expressTid  快递单号
     * @param freightPayer  运费承担方
     * @param freightMode  运费均摊模式;按产品数量，按产品重量
     * @param storageNo 仓库编码
     * @param listSource 来源：不对外提供，固定默认值
     * @param otherCost 其他费用
     * @param outPactNo 外部合同单号
     * @param productItemNo 产品明细编号
     * @param locationNo 库位编号
     * @param batch 批次
     * @param expireTime 到期时间
     * @param supplieNo 返厂供应商编号：在采购管理—供应商档案里查看
     * @param freightAvg 运费均摊
     * @param outstorageType 出库类型（可在档案管理-仓库档案-出库类型设置中查看）
     * @param outstorageTime  出库时间
     * @param outStorageRemark  出库备注
     * @param outstoragePrice  出库价
     * @param outstorageStatus  出库状态
     * @param outStoreTypeName  出库类型名称
     * @return 出库结果（成功、失败）
     */
    @ResponseBody
    @RequestMapping(value = "/commitOutStorage", method = RequestMethod.POST)
    public Monitor<BaseResult> commitOutStorage(String sign,
                                               String uCode,
                                               String secret,
                                               String timeStamp,
                                               String mType,
                                               @RequestParam(value = "signType", required = false, defaultValue = "MD5") String signType,
                                               String erpName,
                                               String outStorageNo,
                                               @RequestParam(value = "freight", required = false) double freight,
                                               @RequestParam(value = "freightAvgway", required = false) String freightAvgway,
                                               @RequestParam(value = "barCode", required = false) String barCode,
                                               @RequestParam(value = "outstorageNum", required = false) int outstorageNum,
                                               @RequestParam(value = "dateType", required = false) String dateType,
                                               @RequestParam(value = "beginTime", required = false) long beginTime,
                                               @RequestParam(value = "endTime", required = false) long endTime,
                                               @RequestParam(value = "storageName", required = false) String storageName,
                                               @RequestParam(value = "importMark", required = false) String importMark,
                                               @RequestParam(value = "provider", required = false) String provider,
                                               @RequestParam(value = "storage", required = false) String storage,
                                               @RequestParam(value = "creater", required = false) String creater,
                                               @RequestParam(value = "createTime", required = false) long createTime,
                                               @RequestParam(value = "qualityInspctor", required = false) String qualityInspctor,
                                               @RequestParam(value = "inspctTime", required = false) long inspctTime,
                                               @RequestParam(value = "inspctResult", required = false) String inspctResult,
                                               @RequestParam(value = "examiner", required = false) String examiner,
                                               @RequestParam(value = "examineTime", required = false) long examineTime,
                                               @RequestParam(value = "cost", required = false) double cost,
                                               @RequestParam(value = "SourceTid", required = false) String SourceTid,
                                               @RequestParam(value = "purchaseFee", required = false) double purchaseFee,
                                               @RequestParam(value = "contractMoney", required = false) double contractMoney,
                                               @RequestParam(value = "relevantTid", required = false) String relevantTid,
                                               @RequestParam(value = "rate", required = false) double rate,
                                               @RequestParam(value = "currency", required = false) String currency,
                                               @RequestParam(value = "outContractTid", required = false) String outContractTid,
                                               @RequestParam(value = "logistics", required = false) String logistics,
                                               @RequestParam(value = "expressTid", required = false) String expressTid,
                                               @RequestParam(value = "freightPayer", required = false) String freightPayer,
                                               @RequestParam(value = "freightMode", required = false) String freightMode,
                                               @RequestParam(value = "storageNo", required = false) String storageNo,
                                               @RequestParam(value = "listSource", required = false) String listSource,
                                               @RequestParam(value = "otherCost", required = false) double otherCost,
                                               @RequestParam(value = "outPactNo", required = false) String outPactNo,
                                               @RequestParam(value = "productItemNo", required = false) String productItemNo,
                                               @RequestParam(value = "locationNo", required = false) String locationNo,
                                               @RequestParam(value = "batch", required = false) String batch,
                                               @RequestParam(value = "expireTime", required = false) long expireTime,
                                               @RequestParam(value = "supplieNo", required = false) String supplieNo,
                                               @RequestParam(value = "freightAvg", required = false) double freightAvg,
                                               @RequestParam(value = "outstorageType", required = false) String outstorageType,
                                               @RequestParam(value = "outstorageTime", required = false) long outstorageTime,
                                               @RequestParam(value = "outStorageRemark", required = false) String outStorageRemark,
                                               @RequestParam(value = "outstoragePrice", required = false) double outstoragePrice,
                                               @RequestParam(value = "outstorageStatus", required = false) String outstorageStatus,
                                               @RequestParam(value = "outStoreTypeName", required = false) String outStoreTypeName
                                                )
    {

        AuthBean authBean = new AuthBean();
        authBean.setSign(sign);
        authBean.setmType(mType);
        authBean.setSecret(secret);
        authBean.setSignType(signType);
        authBean.setTimeStamp(timeStamp);
        authBean.setuCode(uCode);

        BaseResult baseResult;
        String signStr = SignStrategy.getInstance().buildSign(authBean).getSign();
        if (null != signStr && signStr.equals(sign)) {
            baseResult = new InventoryInfo();
            baseResult.setResultMsg("sign验证失败");
            baseResult.setResultCode(Constant.REQUEST_SING_ERROR);
            return new SimpleMonitor<BaseResult>(baseResult);
        } else {

            InventoryInfo inventory = new InventoryInfo();
            inventory.setOutStorageNo(outStorageNo);
            inventory.setFreight(freight);
            inventory.setFreightAvgway(freightAvgway);
            inventory.setBarCode(barCode);
            inventory.setOutstorageNum(outstorageNum);
            inventory.setDateType(dateType);
            inventory.setBeginTime(new Date(beginTime));
            inventory.setEndTime(new Date(endTime));
            inventory.setStorageName(storageName);
            inventory.setOutstorageStatus(outstorageStatus);
            inventory.setImportMark(importMark);
            inventory.setProvider(provider);
            inventory.setStorage(storage);
            inventory.setCreater(creater);
            inventory.setCreateTime(new Date(createTime));
            inventory.setQualityInspctor(qualityInspctor);
            inventory.setInspctTime(new Date(inspctTime));
            inventory.setInspctResult(inspctResult);
            inventory.setExaminer(examiner);
            inventory.setExamineTime(new Date(examineTime));
            inventory.setCost(cost);
            inventory.setSourceTid(SourceTid);
            inventory.setPurchaseFee(purchaseFee);
            inventory.setContractMoney(contractMoney);
            inventory.setRelevantTid(relevantTid);
            inventory.setRate(rate);
            inventory.setCurrency(currency);
            inventory.setOutContractTid(outContractTid);
            inventory.setLogistics(logistics);
            inventory.setExpressTid(expressTid);
            inventory.setFreightPayer(freightPayer);
            inventory.setFreightMode(freightMode);
            inventory.setStorageNo(storageNo);
            inventory.setListSource(listSource);
            inventory.setOtherCost(otherCost);
            inventory.setOutPactNo(outPactNo);
            inventory.setProductItemNo(productItemNo);
            inventory.setLocationNo(locationNo);
            inventory.setBatch(batch);
            inventory.setExpireTime(new Date(expireTime));
            inventory.setSupplieNo(supplieNo);
            inventory.setFreightAvg(freightAvg);
            inventory.setOutstorageType(outstorageType);
            inventory.setOutstorageTime(new Date(outstorageTime));
            inventory.setOutStorageRemark(outStorageRemark);
            inventory.setOutstoragePrice(outstoragePrice);
            inventory.setOutStoreTypeName(outStoreTypeName);
            return inventoryEvent.commitOutStorage(authBean, inventory);
        }

    }

    /**
     * （批量）获取出库信息
     * {1、outStorageIds只包含一个入库编号时，只获取一个出库信息 2、outStorageIds包含多个出库编号时，批量获取出库信息}
     * outStorageIds包含多个出库编号时 每个出库编号以逗号隔开
     *
     * @param sign       授权签名
     * @param uCode      接入码，用于验证请求的有效性。主要用于区分店铺
     * @param secret     密钥
     * @param timeStamp  时间戳
     * @param mType      方法名，不同接口分别传入不同的方法值。
     * @param signType   加密方式，默认为MD5
     * @param erpName    erp名称
     * @param outStorageIds 出库编号
     * @return 返回入库信息
     */
    @ResponseBody
    @RequestMapping(value = "/obtainOutStorage", method = RequestMethod.POST)
    public Monitor<InventoryInfo> obtainOutStorage(String sign,
                                                  String uCode,
                                                  String secret,
                                                  String timeStamp,
                                                  String mType,
                                                  @RequestParam(value = "signType", required = false, defaultValue = "MD5") String signType,
                                                  String erpName,
                                                  String outStorageIds) {

        AuthBean authBean = new AuthBean();
        authBean.setSign(sign);
        authBean.setmType(mType);
        authBean.setSecret(secret);
        authBean.setSignType(signType);
        authBean.setTimeStamp(timeStamp);
        authBean.setuCode(uCode);
        InventoryInfo inventoryInfo;

        String signStr = SignStrategy.getInstance().buildSign(authBean).getSign();
        if (null != signStr && signStr.equals(sign)) {
            inventoryInfo = new InventoryInfo();
            inventoryInfo.setResultMsg("sign验证失败");
            inventoryInfo.setResultCode(Constant.REQUEST_SING_ERROR);
            return new SimpleMonitor<InventoryInfo>(inventoryInfo);
        } else {
            if (StringUtils.isEmpty(outStorageIds)) {
                inventoryInfo = new InventoryInfo();
                inventoryInfo.setResultMsg("传入出库编号无效");
                inventoryInfo.setResultCode(Constant.REQUEST_INAVLID_PARAMETER);
                return new SimpleMonitor<InventoryInfo>(inventoryInfo);
            } else {
                return inventoryEvent.obtainOutStorage(authBean, outStorageIds);
            }
        }
    }

    /**
     * 更新入库信息
     *
     * @param sign       授权签名
     * @param uCode      接入码，用于验证请求的有效性。主要用于区分店铺
     * @param secret     密钥
     * @param timeStamp  时间戳
     * @param mType      方法名，不同接口分别传入不同的方法值。
     * @param signType   加密方式，默认为MD5
     * @param erpName    erp名称
     * @param inStorageNo 入库单编号
     * @param freight 运费
     * @param freightAvgway 运费分摊方式1:按产品数量2：按产品重量默认为1
     * @param barCode 条形码
     * @param instorageNum 入库数量
     * @param type 入库类型；全部:1/其他入库:10/产成品入库:11/原料入库:12/盘盈入库:13/维修入库:14/差错入库:15/退货入库:3/归还入库:4/调拨入库:6/正常入库:8/采购入库:9
     * @param dateType 日期类型--入库日期in_time,制单日期create_time,审核日期examine_time
     * @param beginTime 开始时间;所查订单的开始时间,和日期类型配合使用.
     * @param endTime 结束时间;所查订单的结束时间,和日期类型配合使用.如果没输入,则默认为此时此刻.
     * @param storageName 仓库名称，用于查询指定仓库的入库单
     * @param instorageStatus 入库状态.0:未审核；1：已审核；2：已作废
     * @param importMark 导入标记:不导入,未导入,已导入,已处理,已取消
     * @param typeNo 入库类型编号
     * @param provider 供应商编码
     * @param storage 仓库
     * @param creater 制单员
     * @param createTime 制单时间
     * @param inTime 入库时间
     * @param qualityInspctor 质检员
     * @param inspctTime 质检时间
     * @param inspctResult 质检结果
     * @param examiner 审核人
     * @param examineTime 审核时间
     * @param inReason 入库原因
     * @param cost 成本价格
     * @param SourceTid 来源单号
     * @param purchaseFee 采购费用
     * @param contractMoney 合同总额
     * @param relevantTid 相关单号
     * @param rate 汇率
     * @param currency 币种
     * @param outContractTid 外部合同号
     * @param logistics 物流公司
     * @param expressTid 快递单号
     * @param freightPayer 运费承担方
     * @param remarks 入库备注
     * @param freightMode 运费均摊模式;按产品数量，按产品重量
     * @param storageNo 仓库编码
     * @param listSource 来源：不对外提供，固定默认值
     * @param otherCost 其他费用
     * @param outPactNo 外部合同单号
     * @param productItemNo 产品明细编号
     * @param locationNo 库位编号
     * @param batch 批次
     * @param expireTime 到期时间
     * @param supplieNo 返厂供应商编号：在采购管理—供应商档案里查看
     * @param YSInstorageNo 原始入库单号
     * @param freightAvg 运费均摊
     * @return 入库结果（成功、失败）
     */
    @ResponseBody
    @RequestMapping(value = "/modifyInStorage", method = RequestMethod.POST)
    public Monitor<BaseResult> modifyInStorage(String sign,
                                               String uCode,
                                               String secret,
                                               String timeStamp,
                                               String mType,
                                               @RequestParam(value = "signType", required = false, defaultValue = "MD5") String signType,
                                               String erpName,
                                               String inStorageNo,
                                               @RequestParam(value = "freight", required = false) double freight,
                                               @RequestParam(value = "freightAvgway", required = false) String freightAvgway,
                                               @RequestParam(value = "barCode", required = false) String barCode,
                                               @RequestParam(value = "instorageNum", required = false) int instorageNum,
                                               @RequestParam(value = "type", required = false) int type,
                                               @RequestParam(value = "dateType", required = false) String dateType,
                                               @RequestParam(value = "beginTime", required = false) long beginTime,
                                               @RequestParam(value = "endTime", required = false) long endTime,
                                               @RequestParam(value = "storageName", required = false) String storageName,
                                               @RequestParam(value = "instorageStatus", required = false) int instorageStatus,
                                               @RequestParam(value = "importMark", required = false) String importMark,
                                               @RequestParam(value = "typeNo", required = false) String typeNo,
                                               @RequestParam(value = "provider", required = false) String provider,
                                               @RequestParam(value = "storage", required = false) String storage,
                                               @RequestParam(value = "creater", required = false) String creater,
                                               @RequestParam(value = "createTime", required = false) long createTime,
                                               @RequestParam(value = "inTime", required = false) long inTime,
                                               @RequestParam(value = "qualityInspctor", required = false) String qualityInspctor,
                                               @RequestParam(value = "inspctTime", required = false) long inspctTime,
                                               @RequestParam(value = "inspctResult", required = false) String inspctResult,
                                               @RequestParam(value = "examiner", required = false) String examiner,
                                               @RequestParam(value = "examineTime", required = false) long examineTime,
                                               @RequestParam(value = "inReason", required = false) String inReason,
                                               @RequestParam(value = "cost", required = false) double cost,
                                               @RequestParam(value = "SourceTid", required = false) String SourceTid,
                                               @RequestParam(value = "purchaseFee", required = false) double purchaseFee,
                                               @RequestParam(value = "contractMoney", required = false) double contractMoney,
                                               @RequestParam(value = "relevantTid", required = false) String relevantTid,
                                               @RequestParam(value = "rate", required = false) double rate,
                                               @RequestParam(value = "currency", required = false) String currency,
                                               @RequestParam(value = "outContractTid", required = false) String outContractTid,
                                               @RequestParam(value = "logistics", required = false) String logistics,
                                               @RequestParam(value = "expressTid", required = false) String expressTid,
                                               @RequestParam(value = "freightPayer", required = false) String freightPayer,
                                               @RequestParam(value = "remarks", required = false) String remarks,
                                               @RequestParam(value = "freightMode", required = false) String freightMode,
                                               @RequestParam(value = "storageNo", required = false) String storageNo,
                                               @RequestParam(value = "listSource", required = false) String listSource,
                                               @RequestParam(value = "otherCost", required = false) double otherCost,
                                               @RequestParam(value = "outPactNo", required = false) String outPactNo,
                                               @RequestParam(value = "productItemNo", required = false) String productItemNo,
                                               @RequestParam(value = "locationNo", required = false) String locationNo,
                                               @RequestParam(value = "batch", required = false) String batch,
                                               @RequestParam(value = "expireTime", required = false) long expireTime,
                                               @RequestParam(value = "supplieNo", required = false) String supplieNo,
                                               @RequestParam(value = "YSInstorageNo", required = false) String YSInstorageNo,
                                               @RequestParam(value = "freightAvg", required = false) double freightAvg
    )
    {

        AuthBean authBean = new AuthBean();
        authBean.setSign(sign);
        authBean.setmType(mType);
        authBean.setSecret(secret);
        authBean.setSignType(signType);
        authBean.setTimeStamp(timeStamp);
        authBean.setuCode(uCode);

        BaseResult baseResult;
        String signStr = SignStrategy.getInstance().buildSign(authBean).getSign();
        if (null != signStr && signStr.equals(sign)) {
            baseResult = new InventoryInfo();
            baseResult.setResultMsg("sign验证失败");
            baseResult.setResultCode(Constant.REQUEST_SING_ERROR);
            return new SimpleMonitor<BaseResult>(baseResult);
        } else {

            InventoryInfo inventory = new InventoryInfo();
            inventory.setInStorageNo(inStorageNo);
            inventory.setFreight(freight);
            inventory.setFreightAvgway(freightAvgway);
            inventory.setBarCode(barCode);
            inventory.setInstorageNum(instorageNum);
            inventory.setType(type);
            inventory.setDateType(dateType);
            inventory.setBeginTime(new Date(beginTime));
            inventory.setEndTime(new Date(endTime));
            inventory.setStorageName(storageName);
            inventory.setInstorageStatus(instorageStatus);
            inventory.setImportMark(importMark);
            inventory.setTypeNo(typeNo);
            inventory.setProvider(provider);
            inventory.setStorage(storage);
            inventory.setCreater(creater);
            inventory.setCreateTime(new Date(createTime));
            inventory.setInTime(new Date(inTime));
            inventory.setQualityInspctor(qualityInspctor);
            inventory.setInspctTime(new Date(inspctTime));
            inventory.setInspctResult(inspctResult);
            inventory.setExaminer(examiner);
            inventory.setExamineTime(new Date(examineTime));
            inventory.setInReason(inReason);
            inventory.setCost(cost);
            inventory.setSourceTid(SourceTid);
            inventory.setPurchaseFee(purchaseFee);
            inventory.setContractMoney(contractMoney);
            inventory.setRelevantTid(relevantTid);
            inventory.setRate(rate);
            inventory.setCurrency(currency);
            inventory.setOutContractTid(outContractTid);
            inventory.setLogistics(logistics);
            inventory.setExpressTid(expressTid);
            inventory.setFreightPayer(freightPayer);
            inventory.setRemarks(remarks);
            inventory.setFreightMode(freightMode);
            inventory.setStorageNo(storageNo);
            inventory.setListSource(listSource);
            inventory.setOtherCost(otherCost);
            inventory.setOutPactNo(outPactNo);
            inventory.setProductItemNo(productItemNo);
            inventory.setLocationNo(locationNo);
            inventory.setBatch(batch);
            inventory.setExpireTime(new Date(expireTime));
            inventory.setSupplieNo(supplieNo);
            inventory.setYSInstorageNo(YSInstorageNo);
            inventory.setFreightAvg(freightAvg);
            return inventoryEvent.modifyInStorage(authBean, inventory);
        }

    }

    /**
     * 提交（新增）出库信息
     * @param sign 授权签名
     * @param uCode 接入码，用于验证请求的有效性。主要用于区分店铺
     * @param secret 密钥
     * @param timeStamp 时间戳
     * @param mType 方法名，不同接口分别传入不同的方法值。
     * @param signType 加密方式，默认为MD5
     * @param erpName erp名称
     * @param outStorageNo 出库单编号
     * @param freight 运费
     * @param freightAvgway 运费分摊方式1:按产品数量2：按产品重量默认为1
     * @param barCode 条形码
     * @param outstorageNum 出库数量
     * @param dateType 日期类型--入库日期in_time,制单日期create_time,审核日期examine_time
     * @param beginTime 开始时间;所查订单的开始时间,和日期类型配合使用.
     * @param endTime 结束时间;所查订单的结束时间,和日期类型配合使用.如果没输入,则默认为此时此刻.
     * @param storageName 仓库名称，用于查询指定仓库的入库单
     * @param importMark 导入标记:不导入,未导入,已导入,已处理,已取消
     * @param provider 供应商编码
     * @param storage 仓库
     * @param creater  制单员
     * @param createTime 制单时间
     * @param qualityInspctor  质检员
     * @param inspctTime 质检时间
     * @param inspctResult 质检结果
     * @param examiner 审核人
     * @param examineTime 审核时间
     * @param cost 成本价格
     * @param SourceTid 来源单号
     * @param purchaseFee 采购费用
     * @param contractMoney 合同总额
     * @param relevantTid 相关单号
     * @param rate 汇率
     * @param currency 币种
     * @param outContractTid 外部合同号
     * @param logistics 物流公司
     * @param expressTid  快递单号
     * @param freightPayer  运费承担方
     * @param freightMode  运费均摊模式;按产品数量，按产品重量
     * @param storageNo 仓库编码
     * @param listSource 来源：不对外提供，固定默认值
     * @param otherCost 其他费用
     * @param outPactNo 外部合同单号
     * @param productItemNo 产品明细编号
     * @param locationNo 库位编号
     * @param batch 批次
     * @param expireTime 到期时间
     * @param supplieNo 返厂供应商编号：在采购管理—供应商档案里查看
     * @param freightAvg 运费均摊
     * @param outstorageType 出库类型（可在档案管理-仓库档案-出库类型设置中查看）
     * @param outstorageTime  出库时间
     * @param outStorageRemark  出库备注
     * @param outstoragePrice  出库价
     * @param outstorageStatus  出库状态
     * @param outStoreTypeName  出库类型名称
     * @return 出库结果（成功、失败）
     */
    @ResponseBody
    @RequestMapping(value = "/modifyOutStorage", method = RequestMethod.POST)
    public Monitor<BaseResult> modifyOutStorage(String sign,
                                                String uCode,
                                                String secret,
                                                String timeStamp,
                                                String mType,
                                                @RequestParam(value = "signType", required = false, defaultValue = "MD5") String signType,
                                                String erpName,
                                                String outStorageNo,
                                                @RequestParam(value = "freight", required = false) double freight,
                                                @RequestParam(value = "freightAvgway", required = false) String freightAvgway,
                                                @RequestParam(value = "barCode", required = false) String barCode,
                                                @RequestParam(value = "outstorageNum", required = false) int outstorageNum,
                                                @RequestParam(value = "dateType", required = false) String dateType,
                                                @RequestParam(value = "beginTime", required = false) long beginTime,
                                                @RequestParam(value = "endTime", required = false) long endTime,
                                                @RequestParam(value = "storageName", required = false) String storageName,
                                                @RequestParam(value = "importMark", required = false) String importMark,
                                                @RequestParam(value = "provider", required = false) String provider,
                                                @RequestParam(value = "storage", required = false) String storage,
                                                @RequestParam(value = "creater", required = false) String creater,
                                                @RequestParam(value = "createTime", required = false) long createTime,
                                                @RequestParam(value = "qualityInspctor", required = false) String qualityInspctor,
                                                @RequestParam(value = "inspctTime", required = false) long inspctTime,
                                                @RequestParam(value = "inspctResult", required = false) String inspctResult,
                                                @RequestParam(value = "examiner", required = false) String examiner,
                                                @RequestParam(value = "examineTime", required = false) long examineTime,
                                                @RequestParam(value = "cost", required = false) double cost,
                                                @RequestParam(value = "SourceTid", required = false) String SourceTid,
                                                @RequestParam(value = "purchaseFee", required = false) double purchaseFee,
                                                @RequestParam(value = "contractMoney", required = false) double contractMoney,
                                                @RequestParam(value = "relevantTid", required = false) String relevantTid,
                                                @RequestParam(value = "rate", required = false) double rate,
                                                @RequestParam(value = "currency", required = false) String currency,
                                                @RequestParam(value = "outContractTid", required = false) String outContractTid,
                                                @RequestParam(value = "logistics", required = false) String logistics,
                                                @RequestParam(value = "expressTid", required = false) String expressTid,
                                                @RequestParam(value = "freightPayer", required = false) String freightPayer,
                                                @RequestParam(value = "freightMode", required = false) String freightMode,
                                                @RequestParam(value = "storageNo", required = false) String storageNo,
                                                @RequestParam(value = "listSource", required = false) String listSource,
                                                @RequestParam(value = "otherCost", required = false) double otherCost,
                                                @RequestParam(value = "outPactNo", required = false) String outPactNo,
                                                @RequestParam(value = "productItemNo", required = false) String productItemNo,
                                                @RequestParam(value = "locationNo", required = false) String locationNo,
                                                @RequestParam(value = "batch", required = false) String batch,
                                                @RequestParam(value = "expireTime", required = false) long expireTime,
                                                @RequestParam(value = "supplieNo", required = false) String supplieNo,
                                                @RequestParam(value = "freightAvg", required = false) double freightAvg,
                                                @RequestParam(value = "outstorageType", required = false) String outstorageType,
                                                @RequestParam(value = "outstorageTime", required = false) long outstorageTime,
                                                @RequestParam(value = "outStorageRemark", required = false) String outStorageRemark,
                                                @RequestParam(value = "outstoragePrice", required = false) double outstoragePrice,
                                                @RequestParam(value = "outstorageStatus", required = false) String outstorageStatus,
                                                @RequestParam(value = "outStoreTypeName", required = false) String outStoreTypeName
    )
    {

        AuthBean authBean = new AuthBean();
        authBean.setSign(sign);
        authBean.setmType(mType);
        authBean.setSecret(secret);
        authBean.setSignType(signType);
        authBean.setTimeStamp(timeStamp);
        authBean.setuCode(uCode);

        BaseResult baseResult;
        String signStr = SignStrategy.getInstance().buildSign(authBean).getSign();
        if (null != signStr && signStr.equals(sign)) {
            baseResult = new InventoryInfo();
            baseResult.setResultMsg("sign验证失败");
            baseResult.setResultCode(Constant.REQUEST_SING_ERROR);
            return new SimpleMonitor<BaseResult>(baseResult);
        } else {

            InventoryInfo inventory = new InventoryInfo();
            inventory.setOutStorageNo(outStorageNo);
            inventory.setFreight(freight);
            inventory.setFreightAvgway(freightAvgway);
            inventory.setBarCode(barCode);
            inventory.setOutstorageNum(outstorageNum);
            inventory.setDateType(dateType);
            inventory.setBeginTime(new Date(beginTime));
            inventory.setEndTime(new Date(endTime));
            inventory.setStorageName(storageName);
            inventory.setOutstorageStatus(outstorageStatus);
            inventory.setImportMark(importMark);
            inventory.setProvider(provider);
            inventory.setStorage(storage);
            inventory.setCreater(creater);
            inventory.setCreateTime(new Date(createTime));
            inventory.setQualityInspctor(qualityInspctor);
            inventory.setInspctTime(new Date(inspctTime));
            inventory.setInspctResult(inspctResult);
            inventory.setExaminer(examiner);
            inventory.setExamineTime(new Date(examineTime));
            inventory.setCost(cost);
            inventory.setSourceTid(SourceTid);
            inventory.setPurchaseFee(purchaseFee);
            inventory.setContractMoney(contractMoney);
            inventory.setRelevantTid(relevantTid);
            inventory.setRate(rate);
            inventory.setCurrency(currency);
            inventory.setOutContractTid(outContractTid);
            inventory.setLogistics(logistics);
            inventory.setExpressTid(expressTid);
            inventory.setFreightPayer(freightPayer);
            inventory.setFreightMode(freightMode);
            inventory.setStorageNo(storageNo);
            inventory.setListSource(listSource);
            inventory.setOtherCost(otherCost);
            inventory.setOutPactNo(outPactNo);
            inventory.setProductItemNo(productItemNo);
            inventory.setLocationNo(locationNo);
            inventory.setBatch(batch);
            inventory.setExpireTime(new Date(expireTime));
            inventory.setSupplieNo(supplieNo);
            inventory.setFreightAvg(freightAvg);
            inventory.setOutstorageType(outstorageType);
            inventory.setOutstorageTime(new Date(outstorageTime));
            inventory.setOutStorageRemark(outStorageRemark);
            inventory.setOutstoragePrice(outstoragePrice);
            inventory.setOutStoreTypeName(outStoreTypeName);
            return inventoryEvent.modifyOutStorage(authBean, inventory);
        }

    }

}
