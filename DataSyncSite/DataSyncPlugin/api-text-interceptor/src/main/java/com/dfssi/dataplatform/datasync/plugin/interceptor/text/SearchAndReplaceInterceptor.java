package com.dfssi.dataplatform.datasync.plugin.interceptor.text;

import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.interceptor.Interceptor;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JianKang
 * @date 2018/5/7
 * @description
 * 主要功能是查找并替换符合文本的拦截器,用于清洗转换,主要用于文本的查找和替换
 */
public class SearchAndReplaceInterceptor implements Interceptor {
    static final Logger logger = LoggerFactory.getLogger(SearchAndReplaceInterceptor.class);
    private final Pattern searchPattern;
    private final String replaceString;
    private final Charset charset;

    private SearchAndReplaceInterceptor(Pattern searchPattern,
                                        String replaceString,
                                        Charset charset) {
        this.searchPattern = searchPattern;
        this.replaceString = replaceString;
        this.charset = charset;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void close() {
    }

    @Override
    public Event intercept(Event event) {
        //origBody 原始报文
        String origBody = new String(event.getBody(), charset);
        Matcher matcher = searchPattern.matcher(origBody);
        String newBody = matcher.replaceAll(replaceString);
        event.setBody(newBody.getBytes(charset));
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        for (Event event : events) {
            intercept(event);
        }
        return events;
    }

    public static class Builder implements Interceptor.Builder {
        private static final String SEARCH_PAT_KEY = "searchPattern";
        private static final String REPLACE_STRING_KEY = "replaceString";
        private static final String CHARSET_KEY = "charset";

        private Pattern searchRegex;
        private String replaceString;
        private Charset charset = Charsets.UTF_8;

        @Override
        public void configure(Context context) {
            String searchPattern = context.getString(SEARCH_PAT_KEY);
            Preconditions.checkArgument(!StringUtils.isEmpty(searchPattern),
                    "Must supply a valid search pattern " + SEARCH_PAT_KEY +
                            " (may not be empty)");

            replaceString = context.getString(REPLACE_STRING_KEY);
            // Empty replacement String value or if the property itself is not present
            // assign empty string as replacement
            if (replaceString == null) {
                replaceString = "";
            }

            searchRegex = Pattern.compile(searchPattern);

            if (context.containsKey(CHARSET_KEY)) {
                // May throw IllegalArgumentException for unsupported charsets.
                charset = Charset.forName(context.getString(CHARSET_KEY));
            }
        }

        @Override
        public Interceptor build() {
            if(null == searchRegex){
                logger.warn("Regular expression search pattern required");
            }
            if(null == replaceString){
                logger.warn("Replacement string required");
            }
            return new SearchAndReplaceInterceptor(searchRegex, replaceString, charset);
        }
    }
}
