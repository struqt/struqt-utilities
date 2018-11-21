/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.OngoingStubbing;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static struqt.util.VarLengthInt64.decode;
import static struqt.util.VarLengthInt64.encode;
import static struqt.util.VarLengthInt64.sizeof;

@Slf4j
class VarLengthInt64Test {

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
    final int size = sizeof(value);
    byte[] encoded = new byte[size];
    encode(value, encoded);
    log.debug("value={}, encoded={}", value, Arrays.toString(encoded));
    testStreamEncoding(value, encoded);
    testStreamDecoding(value, encoded);
  }

  @Test
  protected void encodeException() {
    final long value = random.nextLong();
    final int size = sizeof(value);
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
    assertEquals(0, decode(source, 0).getValue());
    assertThrows(IllegalArgumentException.class, () -> decode(source, 1));
    assertThrows(IllegalArgumentException.class, () -> decode(source, 2));
    assertEquals(-1L << 62, decode(source, 3).getValue());
    assertEquals(-1L << 55, decode(source, 4).getValue());
    assertEquals(-1L << 48, decode(source, 5).getValue());
    assertEquals(-1L << 41, decode(source, 6).getValue());
    assertEquals(-1L << 34, decode(source, 7).getValue());
    assertEquals(-1L << 27, decode(source, 8).getValue());
    assertEquals(-1L << 20, decode(source, 9).getValue());
    assertEquals(-1L << 13, decode(source, 10).getValue());
    assertEquals(-1L << 6, decode(source, 11).getValue());
    assertThrows(IllegalArgumentException.class, () -> decode(source, 12));
    assertThrows(IllegalArgumentException.class, () -> decode(source, 13));
    assertThrows(NullPointerException.class, () -> decode((StreamReader) null));
    StreamReader mockReader = mock(StreamReader.class);
    when(mockReader.read()).thenReturn(-1);
    assertThrows(IllegalArgumentException.class, () -> decode(mockReader));
    when(mockReader.read()).thenReturn(-128);
    assertThrows(IllegalArgumentException.class, () -> decode(mockReader));
    when(mockReader.read()).thenReturn(255, 255, -1);
    assertThrows(IllegalArgumentException.class, () -> decode(mockReader));
  }

  private void testStreamEncoding(long value, byte[] encoded) throws IOException {
    final StreamWriter mockStream = mock(StreamWriter.class);
    int actual = encode(value, mockStream);
    for (byte b : encoded) {
      verify(mockStream, atLeast(1)).write(0xFF & b);
    }
    assertEquals(sizeof(value), actual);
  }

  private void testStreamDecoding(long value, byte[] encoded) throws IOException {
    final StreamReader reader = mock(StreamReader.class);
    OngoingStubbing<Integer> stub = when(reader.read());
    for (byte b : encoded) {
      stub = stub.thenReturn(0xFF & b);
    }
    VarLengthInt64 number = decode(reader);
    verify(reader, times(encoded.length)).read();
    assertEquals(value, number.getValue());
    assertEquals(sizeof(value), number.getSize());
  }
}
