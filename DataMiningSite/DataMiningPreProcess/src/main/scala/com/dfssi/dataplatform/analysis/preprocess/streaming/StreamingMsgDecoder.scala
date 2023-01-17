package com.dfssi.dataplatform.analysis.preprocess.streaming

import com.dfssi.dataplatform.streaming.message.StreamingMsg.Message
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties

class StreamingMsgDecoder(props: VerifiableProperties = null) extends Decoder[Message] {
  def fromBytes(bytes: Array[Byte]): Message = {
    try {
      return Message.parseFrom(bytes);
    } catch {
      case e: Exception => {
        e.printStackTrace();
        return null;
      }

    }

  }

}
