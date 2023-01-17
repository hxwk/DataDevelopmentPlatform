package com.dfssi.dataplatform.analysis.model.step;

import com.dfssi.dataplatform.analysis.entity.AnalysisStepAttrEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.*;

public class StepAttr extends LinkedHashMap<String, Object> {

    public final static String FIELD_TAG_ID = "id";

    @JsonIgnore
    private Step step;

    public boolean isNumber(Object obj) {
        if (obj == null) return false;

        return obj instanceof BigDecimal;
    }

    public void setId(String id) {
        this.put(FIELD_TAG_ID, id);
    }

    public String getId() {
        return (String) this.get(FIELD_TAG_ID);
    }

    public List<AnalysisStepAttrEntity> toAttrEntity(int index) {
        List<AnalysisStepAttrEntity> attrEntities = new ArrayList<AnalysisStepAttrEntity>();
        Iterator<Map.Entry<String, Object>> it = this.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (FIELD_TAG_ID.equalsIgnoreCase(entry.getKey())) {
                continue;
            }
            AnalysisStepAttrEntity attrEntity = new AnalysisStepAttrEntity();
            attrEntity.setId(attrEntity.buidId());
            attrEntity.setModelStepId(this.step.getId());
            attrEntity.setnRow(index);
            attrEntity.setCode(entry.getKey());
            if (entry.getValue() instanceof AttrValue) {
                AttrValue attrValue = (AttrValue) entry.getValue();
                attrEntity.setValueNum(attrValue.getValueNum());
                attrEntity.setValueStr(attrValue.getValueStr());
            }

            attrEntities.add(attrEntity);
        }

        return attrEntities;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttrValue {
        public static final String FIELD_TAG_VALUESTR = "valueStr";
        public static final String FIELD_TAG_VALUENUM = "valueNum";

        private String valueStr;
        private BigDecimal valueNum;

        public AttrValue() {
        }

        public AttrValue(String valueStr, BigDecimal valueNum) {
            this.setValueStr(valueStr);
            if (valueNum != null) this.setValueNum(valueNum.stripTrailingZeros());
        }

        public String getValueStr() {
            return valueStr;
        }

        public void setValueStr(String valueStr) {
            this.valueStr = valueStr;
        }

        public BigDecimal getValueNum() {
            return valueNum;
        }

        public void setValueNum(BigDecimal valueNum) {
            this.valueNum = valueNum;
        }

        @JsonIgnore
        public String getNotEmptyValue() {
            if (valueStr != null) {
                return valueStr;
            }

            if (valueNum != null) {
                return valueNum.toPlainString();
            }

            return null;
        }
    }
}
