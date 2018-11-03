/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class DataPacketTest {

  private static final Random random = new SecureRandom();

  @Test
  protected void minimal() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataPacket.writeStart(out::write);
    VarLengthInt64.encode(0x01, out::write);
    VarLengthInt64.encode(0x01, out::write);
    byte[] bytes = out.toByteArray();
    InputStream stream = new ByteArrayInputStream(bytes);
    DataPacket packet = DataPacket.decode(stream::read);
    assertNotNull(packet);
    log.debug("Minimal packet, {} Bytes {} ---> {}", bytes.length, Arrays.toString(bytes), packet);
  }

  @Test
  protected void decodeException() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write('S');
    out.write('D');
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          InputStream stream = new ByteArrayInputStream(out.toByteArray());
          DataPacket.decode(stream::read);
        });
  }

  @Test
  protected void encodeEmpty() throws IOException {
    DataPacket packet =
        DataPacket.builder()
            .version(1)
            .revision(33)
            .length(0)
            .data(new byte[0])
            .nonce(random.nextInt(0x7FFF))
            .crypto(22)
            .compress(22)
            .build();
    int size = packet.sizeof();
    ByteArrayOutputStream out = new ByteArrayOutputStream(size);
    int count = packet.encode(out::write);
    byte[] bytes = out.toByteArray();
    log.debug("Data packet, {} Bytes --> {}", bytes.length, Arrays.toString(bytes));
    assertEquals(size, count);

    InputStream in = new ByteArrayInputStream(bytes);
    DataPacket decoded = DataPacket.decode(in::read);
    assertEquals(packet, decoded);
    assertEquals(packet.hashCode(), decoded.hashCode());
  }

  @Test
  @SuppressWarnings("all")
  protected void comparePacket() {
    DataPacket packet = DataPacket.builder().build();
    assertEquals(packet.getOptions().hashCode(), packet.getOptions().hashCode());
    assertTrue(packet.equals(packet));
    assertFalse(packet.equals(null));
    assertFalse(packet.equals(DataPacket.builder().version(2).build()));
    assertFalse(packet.equals(DataPacket.builder().revision(2).build()));
    assertFalse(packet.equals(DataPacket.builder().nonce(88).build()));
    assertFalse(packet.equals(DataPacket.builder().data(new byte[0]).build()));
    assertFalse(packet.equals(DataPacket.builder().sign(1).build()));
    assertFalse(packet.equals(DataPacket.builder().format(1).build()));
    assertFalse(packet.equals(DataPacket.builder().length(20).build()));
    assertFalse(
        DataPacket.builder().crypto(1).build().equals(DataPacket.builder().crypto(2).build()));
    assertFalse(
        DataPacket.builder().compress(1).build().equals(DataPacket.builder().compress(2).build()));
    assertFalse(
        DataPacket.builder()
            .data(new byte[] {1})
            .build()
            .equals(DataPacket.builder().data(new byte[] {2}).build()));
    assertTrue(packet.getOptions().equals(packet.getOptions()));
    assertFalse(packet.getOptions().equals(null));
  }

  @Test
  protected void encodeNull() throws IOException {
    DataPacket packet =
        DataPacket.builder()
            .data(null)
            .signature(null)
            .version(1)
            .revision(33)
            .length(0)
            .nonce(random.nextInt(0x7FFF))
            .crypto(22)
            .compress(22)
            .build();
    int size = packet.sizeof();
    ByteArrayOutputStream out = new ByteArrayOutputStream(size);
    int count = packet.encode(out::write);
    byte[] bytes = out.toByteArray();
    log.debug("Data packet, {} Bytes --> {}", bytes.length, Arrays.toString(bytes));
    assertEquals(size, count);

    InputStream in = new ByteArrayInputStream(bytes);
    DataPacket decoded = DataPacket.decode(in::read);
    assertEquals(packet, decoded);
    assertEquals(packet.hashCode(), decoded.hashCode());
  }

  @Test
  protected void encode() throws IOException {
    byte[] sig = "I'm from struqt".getBytes(StandardCharsets.US_ASCII);
    byte[] data = "Good morning, world!".getBytes(StandardCharsets.US_ASCII);
    log.debug("Raw data, {} Bytes --> {}", data.length, Arrays.toString(data));

    DataPacket packet =
        DataPacket.builder()
            .version(9)
            .revision(33)
            .length(data.length)
            .nonce(random.nextInt(0x7FFF))
            .data(data)
            .format(2)
            .signature(sig)
            .crypto(22)
            .compress(22)
            .build();
    int size = packet.sizeof();
    ByteArrayOutputStream out = new ByteArrayOutputStream(size);
    int count = packet.encode(out::write);
    byte[] bytes = out.toByteArray();
    log.debug("Data packet, {} Bytes --> {}", bytes.length, Arrays.toString(bytes));
    assertEquals(size, count);
    assertEquals(size, bytes.length);

    InputStream in = new ByteArrayInputStream(bytes);
    DataPacket decoded = DataPacket.decode(in::read);
    assertEquals(packet, decoded);
    assertEquals(packet.hashCode(), decoded.hashCode());
  }
}
