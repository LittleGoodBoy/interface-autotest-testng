package com.yanfuchang.autotest.functions;

import com.yanfuchang.autotest.utils.StringUtil;

/**
 * 随机数函数类
 */
public class RandomDecimalFunction implements Function{
	//${__Random()}==>${__Random(,)}
	//[1,2]  or  [,]
	@Override
	public String execute(String[] args){
		String result = null;
		if(args.length == 0 || StringUtil.isEmpty(args[0])){
			double d = Math.random();
			result = String.valueOf(d);
		}
		return result;
	}

	//excel函数表达式写法为${__Random()}
	@Override
	public String getReferenceKey() {
		return "Random";
	}
}
