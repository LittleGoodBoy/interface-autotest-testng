package com.yanfuchang.autotest.bean;
import java.io.Serializable;

/**
 * 用例执行记录实体类
 * 		用于记录接口测试相关参数及结果,存储到数据库,方便后续追溯
 */
public class AutoLog implements Serializable{
	private static final long serialVersionUID=1L;
	
	private int id;			 //主键
	private String testCase; //用例模块
    private String reqType;  //请求类型
    private String reqUrl;	 //请求url
    private String reqData;  //请求参数
    private String expResult;//预期结果
    private String actResult;//实际结果
    private int result;		 //是否通过	0未通过  1通过
    private String execTime; //执行时间

	public AutoLog() {}
	
	public AutoLog(String testCase,String reqType,String reqUrl,String reqData,String expResult,String actResult,int result,String execTime) {
		this.testCase = testCase;
		this.reqType = reqType;
		this.reqUrl = reqUrl;
		this.reqData = reqData;
		this.expResult = expResult;
		this.actResult = actResult;
		this.result = result;
		this.execTime = execTime;
	}

    public int getId() {
		return id;
	}
    
	public void setId(int id) {
		this.id = id;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public String getReqData() {
		return reqData;
	}

	public void setReqData(String reqData) {
		this.reqData = reqData;
	}

	public String getExpResult() {
		return expResult;
	}

	public void setExpResult(String expResult) {
		this.expResult = expResult;
	}

	public String getActResult() {
		return actResult;
	}

	public void setActResult(String actResult) {
		this.actResult = actResult;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getExecTime() {
		return execTime;
	}

	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}

	@Override
	public String toString() {
		return "AutoLog [id=" + id + ", testCase=" + testCase + ", reqType=" + reqType + ", reqUrl=" + reqUrl
				+ ", reqData=" + reqData + ", expResult=" + expResult + ", actResult=" + actResult + ", result="
				+ result + ", execTime=" + execTime + "]";
	}
}
