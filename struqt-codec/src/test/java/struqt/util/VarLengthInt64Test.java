package struqt.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static struqt.util.VarLengthInt64.decode;
import static struqt.util.VarLengthInt64.encode;

class VarLengthInt64Test {

  private static final Logger log = LoggerFactory.getLogger(VarLengthInt64Test.class);
  private static final Random random = new SecureRandom();

  @ParameterizedTest
  @ValueSource(
      longs = {
        Long.MAX_VALUE,
        Long.MAX_VALUE - 1L,
        (1L << 62),
        (1L << 62) - 1L,
        (1L << 55),
        (1L << 55) - 1L,
        (1L << 48),
        (1L << 48) - 1L,
        (1L << 41),
        (1L << 41) - 1L,
        (1L << 34),
        (1L << 34) - 1L,
        (1L << 27),
        (1L << 27) - 1L,
        (1L << 20),
        (1L << 20) - 1L,
        (1L << 13),
        (1L << 13) - 1L,
        (1L << 6),
        (1L << 6) - 1L,
        1L,
        0L,
        -1L,
        (-1L << 6),
        (-1L << 6) - 1L,
        (-1L << 13),
        (-1L << 13) - 1L,
        (-1L << 20),
        (-1L << 20) - 1L,
        (-1L << 27),
        (-1L << 27) - 1L,
        (-1L << 34),
        (-1L << 34) - 1L,
        (-1L << 41),
        (-1L << 41) - 1L,
        (-1L << 48),
        (-1L << 48) - 1L,
        (-1L << 55),
        (-1L << 55) - 1L,
        (-1L << 62),
        (-1L << 62) - 1L,
        Long.MIN_VALUE + 1L,
        Long.MIN_VALUE,
      })
  protected void thresholds(long value) throws IOException {
    int size = VarLengthInt64.sizeof(value);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int count1 = encode(value, out);
    assertEquals(size, count1);
    byte[] bytes = out.toByteArray();
    String expect = Arrays.toString(bytes);
    byte[] encoded = new byte[size];
    int count2 = encode(value, size, encoded);
    assertEquals(size, count2);
    assertEquals(expect, Arrays.toString(encoded));
    assertEquals(value, decode(bytes));
    assertEquals(value, decode(new ByteArrayInputStream(bytes)));
    log.trace("| {} | {} {}", size, value, expect);
  }

  @Test
  protected void encodeException() {
    final long value = random.nextLong();
    final int size = VarLengthInt64.sizeof(value);
    assertThrows(NullPointerException.class, () -> encode(value, size, null));
    assertThrows(IllegalArgumentException.class, () -> encode(value, 0, null));
    assertThrows(IllegalArgumentException.class, () -> encode(value, new byte[0], -1));
    assertThrows(IllegalArgumentException.class, () -> encode(value, new byte[0]));
    assertThrows(IllegalArgumentException.class, () -> encode(value, new byte[size - 1]));
    assertThrows(IllegalArgumentException.class, () -> encode(value, new byte[size], 1));
    assertThrows(IllegalArgumentException.class, () -> encode(value, size, new byte[size], 1));
  }

  @Test
  protected void decodeException() throws IOException {
    assertThrows(IllegalArgumentException.class, () -> decode(null, 0));
    assertThrows(IllegalArgumentException.class, () -> decode(new byte[0]));
    assertThrows(IllegalArgumentException.class, () -> decode(new byte[] {-128}));
    final byte[] source =
        new byte[] {0, -128, -128, -128, -128, -128, -128, -128, -128, -128, -128, 64};
    assertEquals(0, decode(source, 0));
    assertThrows(IllegalArgumentException.class, () -> decode(source, 1));
    /* Invalid source cause overflow */
    assertThrows(IllegalArgumentException.class, () -> decode(source, 2));
    assertEquals(-1L << 62, decode(source, 3));
    assertEquals(-1L << 55, decode(source, 4));
    assertEquals(-1L << 48, decode(source, 5));
    assertEquals(-1L << 41, decode(source, 6));
    assertEquals(-1L << 34, decode(source, 7));
    assertEquals(-1L << 27, decode(source, 8));
    assertEquals(-1L << 20, decode(source, 9));
    assertEquals(-1L << 13, decode(source, 10));
    assertEquals(-1L << 6, decode(source, 11));
    assertThrows(IllegalArgumentException.class, () -> decode(source, 12));
    assertThrows(IllegalArgumentException.class, () -> decode(source, 13));

    assertThrows(IllegalArgumentException.class, () -> decode((InputStream) null));
    assertThrows(
        IllegalArgumentException.class, () -> decode(new ByteArrayInputStream(new byte[0])));
    assertThrows(
        IllegalArgumentException.class, () -> decode(new ByteArrayInputStream(new byte[] {-128})));
    InputStream stream = new ByteArrayInputStream(source);
    stream.mark(source.length);
    assertEquals(0, decode(stream));
    assertThrows(IllegalArgumentException.class, () -> decodeStream(stream, 1));
    assertThrows(IllegalArgumentException.class, () -> decodeStream(stream, 2));
    assertEquals(-1L << 62, decodeStream(stream, 3));
    assertEquals(-1L << 55, decodeStream(stream, 4));
    assertEquals(-1L << 48, decodeStream(stream, 5));
    assertEquals(-1L << 41, decodeStream(stream, 6));
    assertEquals(-1L << 34, decodeStream(stream, 7));
    assertEquals(-1L << 27, decodeStream(stream, 8));
    assertEquals(-1L << 20, decodeStream(stream, 9));
    assertEquals(-1L << 13, decodeStream(stream, 10));
    assertEquals(-1L << 6, decodeStream(stream, 11));
    assertThrows(IllegalArgumentException.class, () -> decodeStream(stream, 12));
    assertThrows(IllegalArgumentException.class, () -> decodeStream(stream, 13));
  }

  private long decodeStream(InputStream stream, int offset) throws IOException {
    stream.reset();
    long skip = stream.skip(offset);
    if (skip > offset) {
      return 0;
    }
    return decode(stream);
  }
}
