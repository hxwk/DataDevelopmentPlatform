package com.dfssi.elasticsearch.sql611.query.join;

import com.dfssi.elasticsearch.sql611.domain.Field;
import com.dfssi.elasticsearch.sql611.domain.Select;
import org.elasticsearch.action.search.SearchRequestBuilder;

import java.util.List;

/**
 * Created by Eliran on 28/8/2015.
 */
public class TableInJoinRequestBuilder {
    private SearchRequestBuilder requestBuilder;
    private String alias;
    private List<Field> returnedFields;
    private Select originalSelect;
    private Integer hintLimit;

    public TableInJoinRequestBuilder() {
    }

    public SearchRequestBuilder getRequestBuilder() {
        return requestBuilder;
    }

    public void setRequestBuilder(SearchRequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<Field> getReturnedFields() {
        return returnedFields;
    }

    public void setReturnedFields(List<Field> returnedFields) {
        this.returnedFields = returnedFields;
    }

    public Select getOriginalSelect() {
        return originalSelect;
    }

    public void setOriginalSelect(Select originalSelect) {
        this.originalSelect = originalSelect;
    }

    public Integer getHintLimit() {
        return hintLimit;
    }

    public void setHintLimit(Integer hintLimit) {
        this.hintLimit = hintLimit;
    }
}
