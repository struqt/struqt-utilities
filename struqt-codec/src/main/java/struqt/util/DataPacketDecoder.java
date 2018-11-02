/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;

public abstract class DataPacketDecoder {

  private int version;
  private int revision;

  static DataPacket decode(StreamReader stream) throws IOException {
    if (badStartMark(stream)) {
      throw new IllegalArgumentException("Bad start mark, it must be " + DataPacket.START);
    }
    final int version = readInt32(stream);
    final int revision = readInt32(stream);
    int nonce = 0;
    int options = 0;
    try {
      nonce = readInt32(stream);
      options = readInt32(stream);
    } catch (Throwable ignored) {
      /* ignored */
    }
    final DataPacketDecoder decoder;
    switch (version) {
      case 1:
        decoder = new DataPacketDecoderV1(nonce, options);
        break;
      default:
        decoder = new DataPacketDecoderV1(nonce, options);
        break;
    }
    decoder.init(version, revision);
    return decoder.read(stream);
  }

  private static boolean badStartMark(StreamReader stream) throws IOException {
    String mark = DataPacket.START;
    int len = mark.length();
    for (int i = 0; i < len; ++i) {
      if (stream.read() != mark.charAt(i)) {
        return true;
      }
    }
    return false;
  }

  protected abstract DataPacket read(StreamReader stream) throws IOException;

  protected int getVersion() {
    return version;
  }

  protected int getRevision() {
    return revision;
  }

  protected static int readInt32(StreamReader stream) throws IOException {
    return (int) VarLengthInt64.decode(stream);
  }

  protected static byte[] readBytes(StreamReader stream) throws IOException {
    int length = readInt32(stream);
    byte[] bytes = new byte[length];
    for (int i = 0; i < length; i++) {
      bytes[i] = (byte) (0xFF & stream.read());
    }
    return bytes;
  }

  protected void init(int version, int revision) {
    this.version = version;
    this.revision = revision;
  }
}
