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

package com.dfssi.rpc.netty.transfers.registers;

import com.dfssi.rpc.netty.protocol.Encoders;
import com.dfssi.rpc.netty.transfers.messages.TransferMessage;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;

// Needed by ScalaDoc. See SPARK-7726

/**
 * A message sent from the driver to register with the MesosExternalShuffleService.
 */
public class RegisterMaster extends TransferMessage {
  private final String appId;
  private final long heartbeatTimeoutMs;

  public RegisterMaster(String appId, long heartbeatTimeoutMs) {
    this.appId = appId;
    this.heartbeatTimeoutMs = heartbeatTimeoutMs;
  }

  public String getAppId() { return appId; }

  public long getHeartbeatTimeoutMs() { return heartbeatTimeoutMs; }

  @Override
  protected Type type() { return Type.REGISTER_DRIVER; }

  @Override
  public int encodedLength() {
    return Encoders.Strings.encodedLength(appId) + Long.SIZE / Byte.SIZE;
  }

  @Override
  public void encode(ByteBuf buf) {
    Encoders.Strings.encode(buf, appId);
    buf.writeLong(heartbeatTimeoutMs);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(appId, heartbeatTimeoutMs);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RegisterMaster)) {
      return false;
    }
    return Objects.equal(appId, ((RegisterMaster) o).appId);
  }

  public static RegisterMaster decode(ByteBuf buf) {
    String appId = Encoders.Strings.decode(buf);
    long heartbeatTimeout = buf.readLong();
    return new RegisterMaster(appId, heartbeatTimeout);
  }
}
