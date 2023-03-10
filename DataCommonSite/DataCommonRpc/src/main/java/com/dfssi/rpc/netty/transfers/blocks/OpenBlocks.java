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

package com.dfssi.rpc.netty.transfers.blocks;

import com.dfssi.rpc.netty.protocol.Encoders;
import com.dfssi.rpc.netty.transfers.messages.TransferMessage;
import com.dfssi.rpc.netty.transfers.stream.StreamHandle;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;

// Needed by ScalaDoc. See SPARK-7726

/** Request to read a set of blocks. Returns {@link StreamHandle}. */
public class OpenBlocks extends TransferMessage {
  public final String appId;
  public final String execId;
  public final String[] blockIds;

  public OpenBlocks(String appId, String execId, String[] blockIds) {
    this.appId = appId;
    this.execId = execId;
    this.blockIds = blockIds;
  }

  @Override
  protected Type type() { return Type.OPEN_BLOCKS; }

  @Override
  public int hashCode() {
    return Objects.hashCode(appId, execId) * 41 + Arrays.hashCode(blockIds);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("appId", appId)
      .add("execId", execId)
      .add("blockIds", Arrays.toString(blockIds))
      .toString();
  }

  @Override
  public boolean equals(Object other) {
    if (other != null && other instanceof OpenBlocks) {
      OpenBlocks o = (OpenBlocks) other;
      return Objects.equal(appId, o.appId)
        && Objects.equal(execId, o.execId)
        && Arrays.equals(blockIds, o.blockIds);
    }
    return false;
  }

  @Override
  public int encodedLength() {
    return Encoders.Strings.encodedLength(appId)
      + Encoders.Strings.encodedLength(execId)
      + Encoders.StringArrays.encodedLength(blockIds);
  }

  @Override
  public void encode(ByteBuf buf) {
    Encoders.Strings.encode(buf, appId);
    Encoders.Strings.encode(buf, execId);
    Encoders.StringArrays.encode(buf, blockIds);
  }

  public static OpenBlocks decode(ByteBuf buf) {
    String appId = Encoders.Strings.decode(buf);
    String execId = Encoders.Strings.decode(buf);
    String[] blockIds = Encoders.StringArrays.decode(buf);
    return new OpenBlocks(appId, execId, blockIds);
  }
}
