package com.dfssi.dataplatform.analysis.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

public class ResponseObjCheck {

    public static StatusMessage buildStatusMsgFromJson(String jsonStr) {
        JsonObject modelJsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        JsonObject statusJsonObj = modelJsonObject.getAsJsonObject("status");
        StatusMessage statusMessage = new StatusMessage();
        if (statusJsonObj != null) {
            statusMessage.setCode(statusJsonObj.get("code") != null ? statusJsonObj.get("code").getAsString() : null);
            statusMessage.setMessage(statusJsonObj.get("message") != null ? statusJsonObj.get("message").getAsString
                    () : null);
            statusMessage.setDetails(statusJsonObj.get("details") != null ? statusJsonObj.get("details").getAsString
                    () : null);
        }

        return statusMessage;
    }

    public static class StatusMessage {
        private final static String SUCCESS_CODE = "0";
        private String code;
        private String message;
        private String details;

        public StatusMessage() {
        }

        public StatusMessage(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public boolean isValid() {
            if (StringUtils.isBlank(code) || SUCCESS_CODE.equalsIgnoreCase(this.getCode())) {
                return true;
            }

            return false;
        }
    }
}
