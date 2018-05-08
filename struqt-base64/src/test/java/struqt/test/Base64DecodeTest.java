package struqt.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import struqt.util.Base64;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Base 64 Decoding Test")
public class Base64DecodeTest {

  private static final Random random = new SecureRandom();

  @Test
  protected void inputEmpty() {
    assertEquals(0, Base64.decode((String) null).length);
    assertEquals(0, Base64.decode((byte[]) null).length);
    assertEquals(0, Base64.decode((char[]) null).length);
    assertEquals(0, Base64.decode("").length);
    assertEquals(0, Base64.decode(" \n").length);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc123456", "🍒"})
  protected void inputInvalid(String s) {
    assertThrows(IllegalArgumentException.class, () -> Base64.decode(s));
  }

  @ParameterizedTest
  @ValueSource(strings = {"12", "1234", "abc12345", "abc1234567", "abc12345678"})
  protected void inputSimple(String s) {
    byte[] expect = java.util.Base64.getDecoder().decode(s);
    byte[] actual = Base64.decode(s);
    assertTrue(Arrays.equals(actual, expect));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13})
  protected void inputChars(int len) {
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    String encoded = java.util.Base64.getUrlEncoder().encodeToString(bytes);
    char[] chars = new char[encoded.length()];
    encoded.getChars(0, chars.length, chars, 0);
    byte[] actual = Base64.decode(chars);
    assertTrue(Arrays.equals(actual, bytes));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13})
  protected void inputBas64UrlSafe(int len) {
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    byte[] encoded = java.util.Base64.getUrlEncoder().encode(bytes);
    byte[] actual = Base64.decode(encoded);
    assertTrue(Arrays.equals(actual, bytes));
  }

  @ParameterizedTest
  @ValueSource(ints = {56, 57, 58, 113, 114, 115, 170, 171, 172, 227, 228, 229})
  protected void inputBas64Mime(int len) {
    byte[] bytes = new byte[len];
    random.nextBytes(bytes);
    String encoded = java.util.Base64.getMimeEncoder().encodeToString(bytes);
    byte[] actual = Base64.decode(encoded);
    assertTrue(Arrays.equals(actual, bytes));
  }
}
