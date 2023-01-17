package com.dfssi.dataplatform.datasync.service.restful.entity.instruction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by HSF on 2018/1/18.
 */
public class MessageFactory {

    private static final Logger logger =
            LoggerFactory.getLogger(MessageFactory.class);

//    public static Message getInstance(
//            String messageType) {
//
//        Preconditions.checkNotNull(messageType,
//                "message type must not be null");
//
//        // try to find builder class in enum of known output serializers
//        MessageType type;
//        try {
//            type = MessageType.valueOf(messageType.toUpperCase(Locale.ENGLISH));
//        } catch (IllegalArgumentException e) {
//            logger.debug("Not in enum, loading builder class: {}", messageType);
//            type = MessageType.OTHER;
//        }
//        Class<? extends Message.Builder> builderClass =
//                type.getReqClass();
//
//        // handle the case where they have specified their own builder in the config
//        if (builderClass == null) {
//            try {
//                Class c = Class.forName(messageType);
//                if (c != null && Message.Builder.class.isAssignableFrom(c)) {
//                    builderClass = (Class<? extends Message.Builder>) c;
//                } else {
//                    String errMessage = "Unable to instantiate Builder from " +
//                            messageType + ": does not appear to implement " +
//                            Message.Builder.class.getName();
//                    throw new FlumeException(errMessage);
//                }
//            } catch (ClassNotFoundException ex) {
//                logger.error("Class not found: " + messageType, ex);
//                throw new FlumeException(ex);
//            }
//        }
//
//        // build the builder
//        Message.Builder builder;
//        try {
//            builder = builderClass.newInstance();
//        } catch (InstantiationException ex) {
//            String errMessage = "Cannot instantiate builder: " + messageType;
//            logger.error(errMessage, ex);
//            throw new FlumeException(errMessage, ex);
//        } catch (IllegalAccessException ex) {
//            String errMessage = "Cannot instantiate builder: " + messageType;
//            logger.error(errMessage, ex);
//            throw new FlumeException(errMessage, ex);
//        }
//
//        return builder.build();
//    }
}
