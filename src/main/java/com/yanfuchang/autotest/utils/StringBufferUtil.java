package com.yanfuchang.autotest.utils;

import java.util.Map;

public class StringBufferUtil {
    //字符串替换标识
    private final static String DOOP_FLAG = "$";

    public static String getReqData(Map<String, Map<String, String>> iNameAndIJsonToMapResult, String reqData) {

        //字符串替换次数
        int loopCount = StringUtil.getFindstrCount(reqData, DOOP_FLAG);
        if (loopCount == 0) {
            return reqData;
        }

        StringBuffer bufferData = new StringBuffer(reqData);
        for (int i = 0; i < loopCount; i++) {
            //找到${ 和  }开始和结束下标位置
            int startIndex = bufferData.indexOf("${");
            int endIndex = bufferData.indexOf("}");

            //截取${xxx}范围内的值即/public/lazyentrance=status
            String reqDataINameAndIResponseKey = bufferData.substring(startIndex + 2, endIndex);

            //对/public/lazyentrance=status进行分隔,得到[/public/lazyentrance,status]
            String[] reqDataINameAndIResponseKeyArray = reqDataINameAndIResponseKey.split(":");

            //根据/public/lazyentrance得到Map值
            String reqDataIName = reqDataINameAndIResponseKeyArray[0];
            Map<String, String> iJsonToMapResult = iNameAndIJsonToMapResult.get(reqDataIName);

            //从Map值中找status的值
            String value = iJsonToMapResult.get(reqDataINameAndIResponseKeyArray[1]);

            //把${xxx}替换为value
            bufferData = bufferData.replace(startIndex, endIndex + 1, value);
        }
        return bufferData.toString();
    }
}
