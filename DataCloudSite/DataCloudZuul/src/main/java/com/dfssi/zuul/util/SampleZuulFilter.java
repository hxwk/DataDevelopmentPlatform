package com.dfssi.zuul.util;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

/**
 * 路由转发
 * @author yanghs 2018-8-7
 *
 */
//@Component
public class SampleZuulFilter extends ZuulFilter{
	
	private Logger logger = LoggerFactory.getLogger(SampleZuulFilter.class);

	//执行内容
	@Override
	public Object run() {
		RequestContext ctx = getCurrentContext();
		HttpServletRequest request =  ctx.getRequest();
        ctx.getResponse().setHeader("Access-Control-Allow-Origin","*");
        return null;
	}

	//是否执行该过滤器，此处为true，说明需要过滤  
	@Override
	public boolean shouldFilter() {
		return true;
	}

	//转发级别 数字越小 优先级越大
	@Override
	public int filterOrder() {
		return 0;
	}

	//filterType 为转发状态 pre转发前调用 post转发请求返回后调用 routing直接转发 error处理请求时发生错误时被调用
	@Override
	public String filterType() {
		return "pre";
	}

}
