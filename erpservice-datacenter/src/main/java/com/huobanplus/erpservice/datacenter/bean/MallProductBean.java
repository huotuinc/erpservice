package com.huobanplus.erpservice.datacenter.bean;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by allan on 2015/7/10.
 */
@Entity
@Table(name = "Mall_Order_Items")
public class MallProductBean {
    /**
     * ����id
     */
    @Id
    @Column(name = "Item_Id")
    private long id;
    /**
     * ������
     */
    @ManyToOne
    @JoinColumn(name = "Order_Id")
    private MallOrderBean order;
    /**
     * ��ƷID
     */
    @Column(name = "Product_Id")
    private int productId;
    /**
     * ����״̬
     */
    @Column(name = "Dly_Status")
    private String deliverStatus;
    /**
     * ��Ʒ����
     */
    @Column(name = "Type_Id")
    private int typeId;
    /**
     * ����
     */
    @Column(name = "Bn")
    private String bn;
    /**
     * ��Ʒ����
     */
    @Column(name = "Name")
    private String name;
    /**
     * �ɱ�
     */
    @Column(name = "Cost")
    private BigDecimal cost;
    /**
     * ����
     */
    @Column(name = "Price")
    private BigDecimal price;
    /**
     * �ܽ��
     */
    @Column(name = "Amount")
    private BigDecimal amount;
    /**
     * ����
     */
    @Column(name = "Nums")
    private int nums;
    /**
     * �ѷ�����
     */
    @Column(name = "Sendnum")
    private int sendNum;
    /**
     * ����
     */
    @Column(name = "Is_Type")
    private String isType;
    /**
     * ������Id
     */
    @Column(name = "Supplier_Id")
    private int supplierId;
    /**
     * �̻�Id
     */
    @Column(name = "Customer_Id")
    private int customerId;
    /**
     * ��ƷId
     */
    @Column(name = "Goods_Id")
    private int goodId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MallOrderBean getOrder() {
        return order;
    }

    public void setOrder(MallOrderBean order) {
        this.order = order;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getDeliverStatus() {
        return deliverStatus;
    }

    public void setDeliverStatus(String deliverStatus) {
        this.deliverStatus = deliverStatus;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getBn() {
        return bn;
    }

    public void setBn(String bn) {
        this.bn = bn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public String getIsType() {
        return isType;
    }

    public void setIsType(String isType) {
        this.isType = isType;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }
}