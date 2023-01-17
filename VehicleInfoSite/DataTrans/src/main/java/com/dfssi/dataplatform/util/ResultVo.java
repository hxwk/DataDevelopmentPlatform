package com.dfssi.dataplatform.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultVo
{
    private static Logger logger = LoggerFactory.getLogger(ResultVo.class);

    private int errorNo;
    private String errorMsg;
    private Map<String, Object> results = null;



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

        logger.error("返回的结果集为空!");
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

}