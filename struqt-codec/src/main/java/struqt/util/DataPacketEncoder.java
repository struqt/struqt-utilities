/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;

public abstract class DataPacketEncoder {

  static DataPacketEncoder encoder(DataPacket packet) {
    DataPacketEncoder version;
    switch (packet.getVersion()) {
      case 1:
        version = new DataPacketEncoderV1();
        break;
      default:
        version = new DataPacketEncoderV1();
        break;
    }
    return version;
  }

  static int encode(DataPacket packet, StreamWriter stream) throws IOException {
    return encoder(packet).write(packet, stream);
  }

  abstract int sizeof(DataPacket packet);

  protected int sizeof(int value) {
    return VarLengthInt64.sizeof(value);
  }

  protected abstract int write(DataPacket packet, StreamWriter stream) throws IOException;


  protected int writeInt32(StreamWriter stream, int value) throws IOException {
    return VarLengthInt64.encode(value, stream);
  }

  protected int writeData(StreamWriter stream, DataPacket packet) throws IOException {
    int len = packet.dataSize();
    int count = VarLengthInt64.encode(len, stream);
    for (int i = 0; i < len; ++i) {
      stream.write(packet.dataByte(i));
    }
    count += len;
    return count;
  }

  protected int writeSignature(StreamWriter stream, DataPacket packet) throws IOException {
    int len = packet.signatureSize();
    int count = VarLengthInt64.encode(len, stream);
    for (int i = 0; i < len; ++i) {
      stream.write(packet.signatureByte(i));
    }
    count += len;
    return count;
  }
}
