package struqt.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import struqt.util.Base64;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base64Test {

  // private static final Logger log = LoggerFactory.getLogger(Base64Test.class);
  private static final Random random = new SecureRandom();

  @Test
  protected void testBas64Empty() {
    byte[] bytes = new byte[0];
    String expect = java.util.Base64.getEncoder().encodeToString(bytes);
    assertEquals(expect, Base64.encodeToString(bytes));
    assertEquals(expect, Base64.encodeToString(null));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13})
  protected void testBas64(int len) {
    byte[] bytes = new byte[len];
    Arrays.fill(bytes, (byte) -1);
    String expect = java.util.Base64.getEncoder().encodeToString(bytes);
    // log.info(expect);
    assertEquals(expect, Base64.encodeToString(bytes));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13})
  protected void testBas64NoPadding(int len) {
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    String expect = java.util.Base64.getEncoder().encodeToString(bytes);
    int tailCount = len % 3;
    int padCount = tailCount == 1 ? 2 : tailCount == 2 ? 1 : 0;
    expect = expect.substring(0, expect.length() - padCount);
    // log.info(expect);
    assertEquals(expect, String.valueOf(Base64.encodeToChars(bytes, false)));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13})
  protected void testBas64UrlSafe(int len) {
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    String expect = java.util.Base64.getUrlEncoder().encodeToString(bytes);
    assertEquals(expect, Base64.encodeToUrlSafe(bytes));
  }

  @ParameterizedTest
  @ValueSource(ints = {56, 57, 58, 113, 114, 115, 170, 171, 172, 227, 228, 229})
  protected void testBas64Mime(int len) {
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    String expect = java.util.Base64.getMimeEncoder().encodeToString(bytes);
    // log.info("\n{}", expect);
    assertEquals(expect, Base64.encodeToMime(bytes));
  }
}
