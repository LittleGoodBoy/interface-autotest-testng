package com.yanfuchang.autotest.functions;

import org.apache.commons.codec.digest.DigestUtils;

import com.yanfuchang.autotest.utils.StringUtil;

/**
 * md5加密函数类
 */
public class Md5Function implements Function{

	@Override
	public String execute(String[] args){
		String result = null;
		if(args.length > 0 || StringUtil.isNotEmpty(args[0])){
			result = DigestUtils.md5Hex(args[0]);
		}
		return result;
	}

	@Override
	public String getReferenceKey() {
		return "Md5";
	}

	//测试
	public static void main(String[] args) {
		String result = DigestUtils.md5Hex("admin");
		System.out.println(result);
	}
}
