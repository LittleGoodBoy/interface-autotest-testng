package com.yanfuchang.autotest.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * 根据RetryAnalyzerImpl中方法返回结果判断是否重新执行用例
 */
public class AnnotationTransformerImpl implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation,
                          @SuppressWarnings("rawtypes") Class testClass,
                          @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod) {
        //失败用例自动retry
        annotation.setRetryAnalyzer(RetryAnalyzerImpl.class);
    }
}
