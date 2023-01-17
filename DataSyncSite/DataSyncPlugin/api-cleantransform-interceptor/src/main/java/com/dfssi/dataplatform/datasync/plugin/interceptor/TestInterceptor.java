package com.dfssi.dataplatform.datasync.plugin.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.common.platform.entity.MapperRule;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.event.EventBuilder;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.Interceptor;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.InterceptorBuilderFactory;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author JianKang
 * @date 2018/5/10
 * @description
 */
public class TestInterceptor {
    private static final Logger logger =
            LoggerFactory.getLogger(TestInterceptor.class);
    //String clasz = "com.dfssi.dataplatform.datasync.plugin.interceptor.cleantransfer.CleanTransferInterceptor$Builder";
    String clasz = "com.dfssi.dataplatform.datasync.plugin.interceptor.cleantransfer.CleanTransferInterceptor$Builder";

    private void testSearchReplace(Context context, String input, String output)
            throws Exception {
        Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(clasz);
        builder.configure(context);
        Interceptor interceptor = builder.build();

        Event event = EventBuilder.withBody(input, Charsets.UTF_8);
        logger.info("before interceptor val:{}",input);
        event = interceptor.intercept(event);
        String val = new String(event.getBody(), Charsets.UTF_8);
    }

    @Test
    public void testBody() throws Exception{
        Context context = new Context();
        context.put("type","com.dfssi.dataplatform.datasync.plugin.interceptor.cleantransfer.CleanTransferInterceptor");
        Map<String,Map<String,MapperRule>> cleanTranRules = Maps.newConcurrentMap();
        Map<String,MapperRule> mapRule0 = Maps.newConcurrentMap();
        MapperRule mapperRule = new MapperRule();
        mapperRule.setFactor("0.1");
        mapperRule.setOffset("1");
        mapRule0.put("numberhandle",mapperRule);
        cleanTranRules.put("0",mapRule0);

        List<String> columns = Lists.newArrayList();
        columns.add("can_id");


/****************************searchreplace************************/
        Map<String,MapperRule> mapRule1 = Maps.newConcurrentMap();
        MapperRule mapperRule1 = new MapperRule();
        mapperRule1.setSearchPattern("[A-Za-z0-9_.]+@[A-Za-z0-9_-]+\\.com");
        mapperRule1.setReplaceString("ABC");
        mapRule1.put("searchreplace",mapperRule1);
        cleanTranRules.put("1",mapRule1);

        columns.add("car_name");

/*****************************maskhandler******************************/
        Map<String,MapperRule> mapRule2 = Maps.newConcurrentMap();
        MapperRule mapperRule2 = new MapperRule();
        mapperRule2.setMaskStart("1");
        mapperRule2.setMaskEnd("2");
        mapperRule2.setInsertString("**");
        mapRule2.put("maskhandler",mapperRule2);
        cleanTranRules.put("2",mapRule2);

        columns.add("product_addr");

/*********************************************************************/
        Map<String,MapperRule> mapRule3 = Maps.newConcurrentMap();
        MapperRule mapperRule3 = new MapperRule();
        mapperRule3.setSearchPattern("[A-Za-z0-9_.]+@[A-Za-z0-9_-]+\\.com");
        mapperRule3.setReplaceString("ABC");
        mapRule3.put("searchreplace",mapperRule3);

        MapperRule mapperRule4 = new MapperRule();
        mapperRule4.setMaskStart("0");
        mapperRule4.setMaskEnd("2");
        mapperRule4.setInsertString("hao");
        mapRule3.put("maskhandler",mapperRule4);

        cleanTranRules.put("3",mapRule3);
        columns.add("province");

        /*****************************************************/
        context.put("columns",JSONObject.toJSONString(columns));
        context.put("cleanTranRule", JSONObject.toJSONString(cleanTranRules));


        Map<String,String> oriText = Maps.newConcurrentMap();
        oriText.put("can_id","12");
        oriText.put("car_name","Email addresses: test@test.com and foo@test.com");
        oriText.put("product_addr","abcdefg");
        oriText.put("province","123addresses: hello@test.com and foo@test.com");
        logger.info(JSONObject.toJSONString(cleanTranRules));
        testSearchReplace(context,JSONObject.toJSONString(oriText),"");
    }


    public void testRemovePrefix() throws Exception {
        Context context = new Context();
        context.put("searchPattern", "^prefix");
        context.put("replaceString", "");
        testSearchReplace(context, "prefix non-prefix suffix", " non-prefix suffix");
    }


    public void testSyslogStripPriority() throws Exception {
        final String input = "<13>Feb  5 17:32:18 10.0.0.99 Use the BFG!";
        final String output = "Feb  5 17:32:18 10.0.0.99 Use the BFG!";
        Context context = new Context();
        context.put("searchPattern", "^<[0-9]+>");
        context.put("replaceString", "");
        testSearchReplace(context, input, output);
    }


    public void testCapturedGroups() throws Exception {
        final String input = "The quick brown fox jumped over the lazy dog.";
        final String output = "The hungry dog ate the careless fox.";
        Context context = new Context();
        context.put("searchPattern", "The quick brown ([a-z]+) jumped over the lazy ([a-z]+).");
        context.put("replaceString", "The hungry $2 ate the careless $1.");
        testSearchReplace(context, input, output);
    }


    public void testRepeatedRemoval() throws Exception {
        final String input = "Email addresses: test@test.com and foo@test.com";
        final String output = "Email addresses: REDACTED and REDACTED";
        Context context = new Context();
        context.put("searchPattern", "[A-Za-z0-9_.]+@[A-Za-z0-9_-]+\\.com");
        context.put("replaceString", "REDACTED");
        testSearchReplace(context, input, output);
    }


    public void testReplaceEmpty() throws Exception {
        final String input = "Abc123@test.com";
        final String output = "@test.com";
        Context context = new Context();
        context.put("searchPattern", "^[A-Za-z0-9_]+");
        testSearchReplace(context, input, output);
        context.put("replaceString", "");
        testSearchReplace(context, input, output);
    }
}
