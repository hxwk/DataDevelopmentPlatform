/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfssi.rpc.netty.transfers.messages;

import com.dfssi.rpc.netty.protocol.Encodable;
import com.dfssi.rpc.netty.transfers.blocks.OpenBlocks;
import com.dfssi.rpc.netty.transfers.blocks.UploadBlock;
import com.dfssi.rpc.netty.transfers.registers.RegisterExecutor;
import com.dfssi.rpc.netty.transfers.registers.RegisterMaster;
import com.dfssi.rpc.netty.transfers.registers.ServerHeartbeat;
import com.dfssi.rpc.netty.transfers.stream.StreamHandle;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * At a high level:
 *   - OpenBlock is handled by both services, but only services shuffle files for the external
 *     shuffle service. It returns a StreamHandle.
 *   - UploadBlock is only handled by the NettyTransferService.
 *   - RegisterExecutor is only handled by the external shuffle service.
 */
public abstract class TransferMessage implements Encodable {
  protected abstract Type type();

  /** Preceding every serialized message is its type, which allows us to deserialize it. */
  public enum Type {
    OPEN_BLOCKS(0), UPLOAD_BLOCK(1), REGISTER_EXECUTOR(2), STREAM_HANDLE(3), REGISTER_DRIVER(4),
    HEARTBEAT(5), JSON(6);

    private final byte id;

    Type(int id) {
      assert id < 128 : "Cannot have more than 128 message types";
      this.id = (byte) id;
    }

    public byte id() { return id; }
  }

  // NB: Java does not support static methods in interfaces, so we must put this in a static class.
  public static class Decoder {
    /** Deserializes the 'type' byte followed by the message itself. */
    public static TransferMessage fromByteBuffer(ByteBuffer msg) {
      ByteBuf buf = Unpooled.wrappedBuffer(msg);
      byte type = buf.readByte();
      switch (type) {
        case 0: return OpenBlocks.decode(buf);
        case 1: return UploadBlock.decode(buf);
        case 2: return RegisterExecutor.decode(buf);
        case 3: return StreamHandle.decode(buf);
        case 4: return RegisterMaster.decode(buf);
        case 5: return ServerHeartbeat.decode(buf);
        case 6: return JsonMessage.decode(buf);
        default: throw new IllegalArgumentException("Unknown message type: " + type);
      }
    }
  }

  /** Serializes the 'type' byte followed by the message itself. */
  public ByteBuffer toByteBuffer() {
    // Allow room for encoded message, plus the type byte
    ByteBuf buf = Unpooled.buffer(encodedLength() + 1);
    buf.writeByte(type().id);
    encode(buf);
    assert buf.writableBytes() == 0 : "Writable bytes remain: " + buf.writableBytes();
    return buf.nioBuffer();
  }
}
