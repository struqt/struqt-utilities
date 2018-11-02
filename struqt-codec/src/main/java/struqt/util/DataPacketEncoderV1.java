/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;

final class DataPacketEncoderV1 extends DataPacketEncoder {

  protected int sizeof(DataPacket packet) {
    int count = DataPacket.START.length();
    count += sizeof(packet.getVersion());
    count += sizeof(packet.getRevision());
    final DataPacketOptions options = packet.getOptions();
    count += sizeof(packet.getNonce());
    count += sizeof(packet.getOptions().getValue());
    if (options.hasSignature()) {
      count += sizeof(packet.getSign());
    }
    if (options.hasCompress()) {
      count += sizeof(packet.getCompress());
    }
    if (options.hasEncrypt()) {
      count += sizeof(packet.getCrypto());
    }
    count += sizeof(packet.getFormat());
    count += sizeof(packet.getLength());
    count += sizeof(packet.dataSize());
    count += packet.dataSize();
    if (options.hasSignature()) {
      count += sizeof(packet.signatureSize());
      count += packet.signatureSize();
    }
    return count;
  }

  protected int write(DataPacket packet, StreamWriter stream) throws IOException {
    int count = DataPacket.writeStart(stream);
    count += writeInt32(stream, packet.getVersion());
    count += writeInt32(stream, packet.getRevision());
    final DataPacketOptions options = packet.getOptions();
    count += writeInt32(stream, packet.getNonce());
    count += writeInt32(stream, packet.getOptions().getValue());
    if (options.hasSignature()) {
      count += writeInt32(stream, packet.getSign());
    }
    if (options.hasCompress()) {
      count += writeInt32(stream, packet.getCompress());
    }
    if (options.hasEncrypt()) {
      count += writeInt32(stream, packet.getCrypto());
    }
    count += writeInt32(stream, packet.getFormat());
    count += writeInt32(stream, packet.getLength());
    count += writeData(stream, packet);
    if (options.hasSignature()) {
      count += writeSignature(stream, packet);
    }
    return count;
  }
}
