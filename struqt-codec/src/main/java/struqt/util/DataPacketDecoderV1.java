/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;

final class DataPacketDecoderV1 extends DataPacketDecoder {

  private final int nonce;
  private final DataPacketOptions options;

  protected DataPacketDecoderV1(final int nonce, final int options) {
    this.nonce = nonce;
    this.options = new DataPacketOptions(options);
  }

  protected DataPacket read(StreamReader stream) throws IOException {
    DataPacket.Builder b = DataPacket.builder();
    b.version(getVersion());
    b.revision(getRevision());
    b.nonce(nonce);
    if (options.hasSignature()) {
      b.sign(readInt32(stream));
    }
    if (options.hasEncrypt()) {
      b.crypto(readInt32(stream));
    }
    if (options.hasCompress()) {
      b.compress(readInt32(stream));
    }
    if (options.hasData()) {
      b.format(readInt32(stream));
      b.length(readInt32(stream));
      b.data(readBytes(stream));
      if (options.hasSignature()) {
        b.signature(readBytes(stream));
      }
    }
    return b.build();
  }
}
