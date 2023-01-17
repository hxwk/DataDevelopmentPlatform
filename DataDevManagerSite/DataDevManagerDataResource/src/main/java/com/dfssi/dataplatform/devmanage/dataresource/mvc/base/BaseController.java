package com.dfssi.dataplatform.devmanage.dataresource.mvc.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseController {

    public static final String RETURN_TAG_SUCCESS = "SUCCESS";
    public static final String RETURN_TAG_OK = "OK";
    public static final String RETURN_TAG_FAIL = "FAIL";

    protected Logger logger = LoggerFactory.getLogger(getClass());


}
