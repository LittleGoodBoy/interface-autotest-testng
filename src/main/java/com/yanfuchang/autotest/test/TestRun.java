package com.yanfuchang.autotest.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.yanfuchang.autotest.bean.AutoLog;
import com.yanfuchang.autotest.utils.DateTimeUtil;
import com.yanfuchang.autotest.utils.DbcpUtil;
import com.yanfuchang.autotest.utils.ExcelUtil;
import com.yanfuchang.autotest.utils.HttpReqUtil;
import com.yanfuchang.autotest.utils.ParseJsonToMapUtil;
import com.yanfuchang.autotest.utils.ParseXmlUtil;
import com.yanfuchang.autotest.utils.StringUtil;

public class TestRun {

    private String filePath = null;
    private static List<AutoLog> list = new ArrayList<>();

    @BeforeTest
    public void initXmlConfig() {
        // 得到配置文件路径
        String configPath = this.getClass().getResource("/api-config.xml").getFile();
        new ParseXmlUtil(configPath);
    }

    @Parameters({"filePathParam"})
    @BeforeTest
    public void beforeTest(String fromTestngXMLParam) {
        this.filePath = fromTestngXMLParam;
    }

    @DataProvider(name = "testCaseData")
    public Object[][] dp() {
        // 初始化返回值
        Object[][] data = null;
        try {
            data = new ExcelUtil(filePath).getArrayCellValue(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Test(dataProvider = "testCaseData")
    public void httpReq(String id, String isExec, String testCase, String reqType, String reqHost, String reqInterface,
                        String reqData, String expResult, String isDep, String depKey) {
        // 初始化
        String actResult = "";
        String reqUrl = reqHost + reqInterface;

        // reqData-接口依赖处理-提取依赖的接口地址从map中获取相关的参数
        reqData = StringUtil.buildParam(ParseJsonToMapUtil.getINameAndIMap(), reqData);
        // reqData-函数化处理
        reqData = StringUtil.buildParam(reqData);

        // 开始执行用例-只执行isExec=YES
        if ("YES".equals(isExec)) {
            if ("GET".equals(reqType)) {
                actResult = HttpReqUtil.sendGet(reqUrl, reqData);
            } else {
                actResult = HttpReqUtil.sendPost(reqUrl, reqData);
            }
        } else {
            Reporter.log("用例不执行");
        }

        // 接口被依赖,把接口服务器返回值转化为Map进行存储
        if ("YES".equals(isDep)) {
            new ParseJsonToMapUtil().setINameAndIMap(reqInterface, actResult);
        }

        // 打印日志
        Reporter.log("请求接口:" + reqUrl);
        Reporter.log("请求参数:" + reqData);
        Reporter.log("预期值:" + expResult);
        Reporter.log("实际值:" + actResult);

        // 收集用例数据和结果入库
        AutoLog autoLog = new AutoLog(testCase, reqType, reqUrl, reqData, expResult, actResult,
                actResult.contains(expResult) ? 1 : 0, DateTimeUtil.getDateTime());
        list.add(autoLog);

        // 预期值和实际值 Assert
        Map<String, String> actResultMap = new ParseJsonToMapUtil().parseJsonToMap(actResult);

        String regex = "(\\w+)=(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expResult);
        while (matcher.find()) {
            String expResultKey = matcher.group(1);
            String expResultValue = matcher.group(2);
            Assert.assertTrue(actResultMap.get(expResultKey).equals(expResultValue));
        }
    }

    @AfterTest
    public void afterTest() {
        // 测试结果入库
        DbcpUtil.dbcpBatchUpdate(list);
    }
}
