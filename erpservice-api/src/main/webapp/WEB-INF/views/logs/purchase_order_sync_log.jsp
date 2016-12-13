<%--
  ~ 版权所有:杭州火图科技有限公司
  ~ 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
  ~
  ~ (c) Copyright Hangzhou Hot Technology Co., Ltd.
  ~ Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
  ~ 2013-2016. All rights reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: allan
  Date: 4/13/16
  Time: 10:41 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>采购单上传</title>
    <link href="<c:url value="/resource/css/admin.global.css" />" rel="stylesheet" type="text/css">
    <link href="<c:url value="/resource/css/admin.content.css" />" rel="stylesheet" type="text/css">
    <link href="<c:url value="/resource/css/admin.mall.css" />" rel="stylesheet" type="text/css">
    <script src="<c:url value="/resource/scripts/lib/jquery-1.7.2.min.js" />" type="text/javascript"></script>
    <script type="text/javascript" src="<c:url value="/resource/scripts/lib/jBox/jquery.jBox-2.3.min.js" />"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resource/scripts/lib/jBox/Skins/Green/jbox.css"/>">
    <script type="text/javascript"
            src="<c:url value="/resource/scripts/lib/jqueryui/jquery-ui-1.8.20.min.js" />"></script>
    <link href="<c:url value="/resource/scripts/lib/jqueryui/jquery-ui-1.10.3.custom.min.css"/>" rel="stylesheet"
          type="text/css"/>
    <script src="<c:url value="/resource/scripts/lib/jquery.utils.js" />" type="text/javascript"></script>
    <script src="<c:url value="/resource/scripts/admin.js" />"></script>
    <script src="<c:url value="/resource/scripts/lib/My97DatePicker/WdatePicker.js" />" type="text/javascript"></script>
    <style>
        #uploadLabel {
            border: 1px solid #A4A4A4;
            background-color: white;
            color: #888;
            border-radius: 4px;
            width: 180px;
            display: inline-block;
            text-align: center;
            margin: 0px;
            padding: 0px;
        }

        #uploadFile {
            width: 100px;
            text-align: center;
            border-radius: 4px;
            height: 30px;
            background-color: #EE3B3B;
            color: white;
        }

    </style>
</head>
<body>
<div class="container">
    <div class="blank10">
    </div>

    <form method="post" id="uploadForm" action="/erpService/platform/uploadPurchaseOrderFile"
          enctype="multipart/form-data">
        <div class="search block" style="display: block;">
            <div class="h">
                <span class="icon-sprite icon-magnifier"></span>

                <h3>采购单文件上传</h3>
            </div>
            <div class="cnt-wp" style="padding: 35px 10px 10px;">

                <input type="hidden" value="${erpUserType}" name="erpUserType"/>
                <div class="division">
                    <table width="100%" border="0" cellpadding="0" cellspacing="0">
                        <tbody>
                        <tr>
                            <th style="vertical-align: middle;">提单号</th>
                            <td style="text-align: left"><input type="text" name="blno" id="blno" class="input-normal"/>
                            </td>
                        </tr>
                        <tr>
                            <th style="vertical-align: middle;">供应商编码</th>
                            <td style="text-align: left"><input type="text" name="supplierId" id="supplierId"
                                                                class="input-normal"/></td>
                        </tr>
                        <tr>
                            <th style="vertical-align: middle;">上传文件</th>
                            <td style="text-align: left">
                                <label for="sourceFile" id="uploadLabel" class="input-normal">请选择采购单文件</label>
                                <input type="file" name="file" value="上传文件" id="sourceFile" style="display: none;"/>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div style="text-align: center;">
                    <input type="button" value="提交" id="uploadFile"/>
                </div>

            </div>
        </div>
    </form>

    <form method="get" id="searchForm">
        <input type="hidden" name="erpUserType" value="${erpUserType}"/>
        <div class="blank10">
        </div>

        <div class="block">
            <div class="tl corner">
            </div>
            <div class="tr corner">
            </div>
            <div class="bl corner">
            </div>
            <div class="br corner">
            </div>
            <div class="cnt-wp" style="padding: 10px 10px 10px;display: block;">

                <div class="cnt">
                    <table class="data-table even1" width="100%" border="0" cellspacing="0" cellpadding="0">
                        <thead>
                        <tr class="even">
                            <th scope="col">全选 <input type="checkbox" id="checkAll"/></th>
                            <th scope="col">提单号
                            </th>
                            <th scope="col">供应商编码
                            </th>
                            <th scope="col">收货订单号
                            </th>
                            <th scope="col">ERP
                            </th>
                            <th scope="col">同步状态
                            </th>
                            <th scope="col">添加时间
                            </th>
                            <th scope="col">最后一次同步时间
                            </th>
                            <th scope="col">操作
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="log" items="${purchaseOrderPage.getContent()}">
                            <tr>
                                <td class="txt40 c"><input type="checkbox" class="checkItem"></td>
                                <td class="txt20 c">${log.blno}</td>
                                <td class="txt20 c">${log.supplierId}</td>
                                <td class="txt20 c">${log.receiveNo}</td>
                                <td class="txt40 c">${log.providerType.name}</td>
                                <td class="txt40 c">${log.detailSyncStatus.name}</td>
                                <td class="txt40 c"><fmt:formatDate value="${log.createTime}"
                                                                    pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td class="txt40 c"><fmt:formatDate value="${log.syncTime}"
                                                                    pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td class="txt80 c">
                                    <a href="/erpService/platform/purchaseItem?id=${log.id}&erpUserType=${erpUserType}">查看明细</a>
                                    |
                                    <c:if test="${log.detailSyncStatus.code==0}">
                                        <a href="javascript:rePush(${log.id})"> 重新推送</a> |
                                        <span style="display:none;" id="errorMsg${log.id}">${log.errorMsg}</span>
                                        <a href="javascript:showErrorMsg(${log.id})">查看错误信息</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <script type="text/javascript">
                    var pageSize = ${pageSize};
                    var pageIndex = ${pageIndex};
                    var pageCount = ${purchaseOrderPage.getTotalPages()};
                    var recordCount = ${purchaseOrderPage.getTotalElements()};
                    var formName = 'searchForm';
                    Pager.Output(formName, 'pageIndex', pageSize, pageIndex, pageCount, recordCount);
                </script>
            </div>
        </div>
    </form>

</div>

<div id="error_dialog" style="padding: 20px;display: none;"></div>

<script>
    $(document).ready(function () {
        $("#uploadFile").click(function () {
            var filePath = $.trim($("#sourceFile").val());
            console.log(filePath);
            var blno = $.trim($("#blno").val());
            var supplierId = $.trim($("#supplierId").val());
            if (blno.length == 0) {
                $.jBox.tip("请输入提单号");
                return false;
            }
            if (supplierId.length == 0) {
                $.jBox.tip("请输入供应商编码");
                return false;
            }

            if (filePath.length == 0) {
                $.jBox.tip("请选择采购单文件");
                return false;
            }
            $("#uploadForm").submit();
        });


        $("#sourceFile").change(function () {
            var filePath = $(this).val();
            console.log(filePath);
            if (filePath.indexOf("xlsx") != -1 || filePath.indexOf("xls") != -1) {
                $(".fileerrorTip").html("").hide();
                var arr = filePath.split('\\');
                var fileName = arr[arr.length - 1];
                $("#uploadLabel").text("已选择" + fileName);
            } else {
                $("#uploadLabel").text("请选择采购单文件");
                $("#sourceFile").val("");
                $.jBox.tip("文件类型有误,请从新选择");
                return false
            }
        })

    });

    function showErrorMsg(id) {
        var errorMsg = $("#errorMsg" + id).html();
        $("#error_dialog").html(errorMsg);
        J.ShowDialogButton("error_dialog", "错误信息", {
            "关闭": function () {
                $(this).dialog('close');
            }
        })
    }


</script>
</body>
</html>
