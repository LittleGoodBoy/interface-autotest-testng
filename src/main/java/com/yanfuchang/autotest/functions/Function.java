package com.yanfuchang.autotest.functions;

/**
 * 统一接口
 * 函数表达式,用于解决接口依赖函数
 * 		通过截取接口中所需的函数关键字,找到对应的函数,获取所需的函数返回值
 */
public interface Function {
	
	String execute(String[] args);
	
	String getReferenceKey();
	
}