package com.yanfuchang.autotest.listener;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * 配置失败用例执行次数
 */
public class RetryAnalyzerImpl implements IRetryAnalyzer {
    //retry初始值,最大值
    private int retryCount = 0;
    private int retryMaxCount = 2;

    /**
     * 当该方法返回true时,则会重试失败的用例
     */
    @Override
    public boolean retry(ITestResult result) {
        boolean flag = false;

        if (retryCount < retryMaxCount) {
            retryCount++;
            flag = true;
            System.out.println("The current  method is re executed " + (retryCount) + " times");
        } else {
            resetRetryCount();
        }
        return flag;
    }

    /**
     * 重置retryCount
     */
    public void resetRetryCount() {
        retryCount = 0;
    }
}
