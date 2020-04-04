package com.yanfuchang.autotest.functions;

import com.yanfuchang.autotest.utils.DateTimeUtil;
import com.yanfuchang.autotest.utils.StringUtil;

/**
 * 日期函数类
 */
public class TimeFunction implements Function{
	
	@Override
	public String execute(String[] args){
		String result = null;
		if(args.length == 0 || StringUtil.isEmpty(args[0])){
			result = String.valueOf(DateTimeUtil.getTimeStamp());//时间戳
		}else{
			result = DateTimeUtil.getDateTime();//年月日时分秒
		}
		return result;
	}

	@Override
	public String getReferenceKey() {
		return "Time";
	}
}
