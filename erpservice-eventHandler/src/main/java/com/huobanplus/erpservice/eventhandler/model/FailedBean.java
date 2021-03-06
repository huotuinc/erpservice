/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huobanplus.erpservice.eventhandler.model;

/**
 * 类描述：请求失败 实体
 * @author aaron
 * @since 2015年7月27日 上午10:26:48
 * @version V1.0
 */
public class FailedBean{

    /**
     * 失败原因说明
     */
    private String failedMsg;
    /**
     * 失败编码
     */
    private String failedCode;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 操作事件
     */
    private String currentEvent;
    /**
     * 操作时间
     * 时间格式：yyyy-MM-dd HH:mm:ss
     */
    private String operationTime;
    /**
     * 恢复机制
     * 根据故障评级来确定恢复机制
     * 一般为重发？
     */

    private String recoveryMechanism;
    /**
     * 故障评级
     * 表示该次失败产生灾难的危害
     */
    private String faultRating;

    public String getFailedMsg() {
        return failedMsg;
    }

    public void setFailedMsg(String failedMsg) {
        this.failedMsg = failedMsg;
    }

    public String getFailedCode() {
        return failedCode;
    }

    public void setFailedCode(String failedCode) {
        this.failedCode = failedCode;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(String currentEvent) {
        this.currentEvent = currentEvent;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public String getRecoveryMechanism() {
        return recoveryMechanism;
    }

    public void setRecoveryMechanism(String recoveryMechanism) {
        this.recoveryMechanism = recoveryMechanism;
    }

    public String getFaultRating() {
        return faultRating;
    }

    public void setFaultRating(String faultRating) {
        this.faultRating = faultRating;
    }
}
