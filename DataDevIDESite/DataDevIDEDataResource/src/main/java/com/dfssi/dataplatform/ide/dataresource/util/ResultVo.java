package com.dfssi.dataplatform.ide.dataresource.util;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultVo
{
    private static Logger logger = Logger.getLogger(ResultVo.class);

    private int errorNo;
    private String errorMsg;
    private Map<String, Object> results = null;
    private String successMsg;



    public ResultVo() {
        this.errorNo = 0;
        this.results = new HashMap();
    }

    public int getErrorNo() {
        return this.errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public Map<String, Object> getResults() {
        return this.results;
    }

    public DataRow getData() {
        Iterator iter = this.results.keySet().iterator();
        if (iter.hasNext()) {
            String key = (String)iter.next();
            return getData(key);
        }
        return null;
    }

    public DataRow getData(String dsName)
    {
        Object value = getResult();

        if (value != null) {
            if (value instanceof DataRow) {
                return ((DataRow)value);
            }
            if (value instanceof Map)
            {
                DataRow dataRow = new DataRow();
                dataRow.putAll((Map)value);
                return dataRow;
            }
            if (value instanceof List) {
                List dataList = (List)value;
                if (dataList.size() > 0) {
                    value = dataList.get(0);
                    if (value instanceof DataRow) {
                        return ((DataRow)value);
                    }
                }
            }
        }

        return null;
    }

    public List getList() {
        Object value = getResult();
        if ((value != null) && (value instanceof List)) {
            return ((List)value);
        }
        return null;
    }

    public List getList(String dsName)
    {
        Object value = getResult(dsName);
        if ((value != null) && (value instanceof List)) {
            return ((List)value);
        }

        return null;
    }



    private Object getResult()
    {
        Iterator iter = this.results.keySet().iterator();
        if (iter.hasNext()) {
            String key = (String)iter.next();
            return getResult(key);
        }

        logger.error("????????????????????????!");
        return null;
    }

    private Object getResult(String dsName) {
        return getResults().get(dsName);
    }

    public void setResult(Object object) {
        this.results.put("DataSet", object);
    }

    public void setResult(String name, Object object) {
        this.results.put(name, object);
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }
}