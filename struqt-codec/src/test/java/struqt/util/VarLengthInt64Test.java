package struqt.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

class VarLengthInt64Test {

  private static final Logger log = LoggerFactory.getLogger(VarLengthInt64Test.class);
  private static final Random random = new SecureRandom();

  @ParameterizedTest
  @ValueSource(
      longs = {
        Long.MAX_VALUE,
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
        Long.MIN_VALUE,
      })
  void encode(long value) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int size = VarLengthInt64.sizeof(value);
    int count1 = VarLengthInt64.encode(value, out);
    int count2 = VarLengthInt64.encode(value, size, new byte[size]);
    Assertions.assertEquals(size, count1);
    Assertions.assertEquals(size, count2);
    log.debug("| {} | {} {}", size, value, Arrays.toString(out.toByteArray()));
  }

  @Test
  void encodeException() {
    final long value = random.nextLong();
    final int size = VarLengthInt64.sizeof(value);
    Assertions.assertThrows(
        NullPointerException.class, () -> VarLengthInt64.encode(value, size, null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> VarLengthInt64.encode(value, 0, null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> VarLengthInt64.encode(value, new byte[0], -1));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> VarLengthInt64.encode(value, new byte[0]));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> VarLengthInt64.encode(value, new byte[size - 1]));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> VarLengthInt64.encode(value, new byte[size], 1));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> VarLengthInt64.encode(value, size, new byte[size], 1));
  }
}
