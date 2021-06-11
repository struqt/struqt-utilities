/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.util.Arrays;

/**
 * Base64 encode and decode implementation as <a href="https://tools.ietf.org/html/rfc2045#section-6.8">RFC
 * 2045</a> and <a href="https://tools.ietf.org/html/rfc4648">RFC 4648</a> specified.
 *
 * @author wangkang
 * @since 1.1
 */
public abstract class Base64 {

  private static final char[] EMPTY_CHARS = new char[0];
  private static final char[] LINE_SEPARATOR = new char[] {'\r', '\n'};
  private static final char ALPHABET_PAD = '=';
  private static final char[] ALPHABET_BASIC = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
    'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
  };
  private static final char[] ALPHABET_URL_SAFE = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
    'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
  };

  private static final byte[] EMPTY_BYTES = new byte[0];
  private static final int[] DECODE_MAP = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63,
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -128, -1, -1,
    -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63,
    -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1
  };

  /**
   * Parse Base 64 encoded String to byte array.
   *
   * @param s Base 64 encoded String
   * @return Byte array as output
   * @since 1.1
   */
  public static byte[] decode(final CharSequence s) {
    if (s == null) {
      return EMPTY_BYTES;
    }
    return decodeNow(new InputCharSeq(s));
  }

  /**
   * Parse Base 64 encoded bytes to byte array.
   *
   * @param bytes Base 64 encoded bytes
   * @return Byte array as output
   * @since 1.1
   */
  public static byte[] decode(final byte[] bytes) {
    if (bytes == null || bytes.length <= 0) {
      return EMPTY_BYTES;
    }
    return decodeNow(new InputByteArray(bytes));
  }

  /**
   * Parse Base 64 encoded char array to byte array.
   *
   * @param chars Base 64 encoded char array
   * @return Byte array as output
   * @since 1.1
   */
  public static byte[] decode(final char[] chars) {
    if (chars == null || chars.length <= 0) {
      return EMPTY_BYTES;
    }
    return decodeNow(new InputCharArray(chars));
  }

  /**
   * Generate Base 64 encoding as specified in <a
   * href="https://tools.ietf.org/html/rfc4648#section-4">RFC 4648</a>.
   *
   * @param bytes Byte array as input
   * @return Base64 encoded String as output
   * @since 1.1
   */
  public static String encodeToString(byte[] bytes) {
    return String.valueOf(encode(bytes, false, false, true));
  }

  /**
   * Generate Base 64 Encoding with URL and Filename Safe Alphabet as specified in <a
   * href="https://tools.ietf.org/html/rfc4648#section-5">RFC 4648</a>.
   *
   * @param bytes Byte array as input
   * @return Base64 encoded String as output
   * @since 1.1
   */
  public static String encodeToUrlSafe(byte[] bytes) {
    return String.valueOf(encode(bytes, true, false, true));
  }

  /**
   * Generate Base64 Content-Transfer-Encoding as specified in <a
   * href="https://tools.ietf.org/html/rfc2045#section-6.8">RFC 2045</a>.
   *
   * <p>The output is multi-line String. Max characters of each line is 76. Uses "\r\n" as line
   * separator. No separator in the end of output. All line separators should be ignored when
   * decoding the encoded result.
   *
   * @param bytes Byte array as input
   * @return Base64 encoded String as output
   * @since 1.1
   */
  public static String encodeToMime(byte[] bytes) {
    return String.valueOf(encode(bytes, false, true, true));
  }

  /**
   * Generate Base 64 encoding as specified in <a
   * href="https://tools.ietf.org/html/rfc4648#section-4">RFC 4648</a>.
   *
   * @param bytes Byte array as input
   * @param url Use URL Safe alphabet or not
   * @param mime RFC 2045 specified multi-line Base 64 encoding
   * @param padding Whether fill the padding character in the end of output
   * @return Base64 encoded String as output
   * @since 1.1
   */
  public static char[] encode(byte[] bytes, boolean url, boolean mime, boolean padding) {
    if (url) {
      return encode(bytes, ALPHABET_URL_SAFE, padding, 0);
    } else {
      if (mime) {
        return encode(bytes, ALPHABET_BASIC, padding, 19);
      } else {
        return encode(bytes, ALPHABET_BASIC, padding, 0);
      }
    }
  }

  private static char[] encode(byte[] bytes, char[] alphabet, boolean padding, int wrap) {
    if (bytes == null || bytes.length <= 0) {
      return EMPTY_CHARS;
    }
    int loopCount = bytes.length / 3;
    int remainOffset = loopCount * 3;
    int remainCharOffset = loopCount * 4;
    int remains = bytes.length - remainOffset;
    int charCount = remains <= 0 ? remainCharOffset : remainCharOffset + 4;
    if (!padding) {
      int paddingCount = remains == 1 ? 2 : remains == 2 ? 1 : 0;
      charCount -= paddingCount;
    }
    int lineWidth = wrap << 2;
    int lineCount = lineWidth <= 0 ? 0 : charCount / lineWidth;
    if (charCount > lineWidth * lineCount) {
      lineCount++;
    }
    if (lineCount > 1) {
      charCount += ((lineCount - 1) << 1);
    }
    boolean multiLine = wrap > 0 && lineCount > 1;
    int iWrap = 0;
    int line = 0;
    char[] chars = new char[charCount];
    int bytesOffset;
    int charsOffset;
    for (int i = 0; i < loopCount; i++) {
      bytesOffset = i * 3;
      charsOffset = (i << 2) + (line << 1);
      encode24Bits(chars, charsOffset, bytes, bytesOffset, 3, alphabet, padding);
      if (multiLine && (line + 1) < lineCount) {
        iWrap++;
        if (iWrap == wrap) {
          chars[charsOffset + 4] = LINE_SEPARATOR[0];
          chars[charsOffset + 5] = LINE_SEPARATOR[1];
          line++;
          iWrap = 0;
        }
      }
    }
    if (remains > 0) {
      bytesOffset = loopCount * 3;
      charsOffset = (loopCount << 2) + (line << 1);
      encode24Bits(chars, charsOffset, bytes, bytesOffset, remains, alphabet, padding);
    }
    return chars;
  }

  private static void encode24Bits(
      char[] chars,
      int charsOffset,
      byte[] bytes,
      int bytesOffset,
      int byteCount,
      char[] alphabet,
      boolean padding) {

    if (byteCount >= 3) {
      int byte1 = 0xFF & bytes[bytesOffset];
      int byte2 = 0xFF & bytes[bytesOffset + 1];
      int byte3 = 0xFF & bytes[bytesOffset + 2];
      int bits = (byte1 << 16) | (byte2 << 8) | byte3;
      chars[charsOffset] = alphabet[0x3F & (bits >>> 18)];
      chars[charsOffset + 1] = alphabet[0x3F & (bits >>> 12)];
      chars[charsOffset + 2] = alphabet[0x3F & (bits >>> 6)];
      chars[charsOffset + 3] = alphabet[0x3F & bits];
    } else if (byteCount == 2) {
      int byte1 = 0xFF & bytes[bytesOffset];
      int byte2 = 0xFF & bytes[bytesOffset + 1];
      int bits = (byte1 << 16) | (byte2 << 8);
      chars[charsOffset] = alphabet[0x3F & (bits >>> 18)];
      chars[charsOffset + 1] = alphabet[0x3F & (bits >>> 12)];
      chars[charsOffset + 2] = alphabet[0x3F & (bits >>> 6)];
      if (padding) {
        chars[charsOffset + 3] = ALPHABET_PAD;
      }
    } else if (byteCount == 1) {
      int byte1 = 0xFF & bytes[bytesOffset];
      int bits = (byte1 << 16);
      chars[charsOffset] = alphabet[0x3F & (bits >>> 18)];
      chars[charsOffset + 1] = alphabet[0x3F & (bits >>> 12)];
      if (padding) {
        chars[charsOffset + 2] = ALPHABET_PAD;
        chars[charsOffset + 3] = ALPHABET_PAD;
      }
    }
  }

  private static byte[] decodeNow(final Base64Input s) {
    int len = s.size();
    int maxBytes = (len * 3) >>> 2;
    byte[] result = new byte[maxBytes];
    int i = 0;
    int[] chars = new int[] {-1, -1, -1, -1};
    int charCount = 0;
    int loop = 0;
    int bits;
    while (i < len) {
      int ch = s.byteAt(i++);
      if (ch < 9 || ch > 123) {
        throw new IllegalArgumentException(
            "Invalid base64 character '" + ch + "' at position " + i);
      }
      int c = DECODE_MAP[ch];
      if (c < 0) {
        continue;
      }
      chars[charCount++] = c;
      if (charCount == 4) {
        bits = chars[0] << 18 | chars[1] << 12 | chars[2] << 6 | chars[3];
        int offset = 3 * loop++;
        result[offset] = (byte) (bits >>> 16);
        result[offset + 1] = (byte) (bits >>> 8);
        result[offset + 2] = (byte) bits;
        charCount = 0;
        Arrays.fill(chars, -1);
      }
    }
    int offset = 3 * loop;
    if (charCount == 3) {
      bits = chars[0] << 18 | chars[1] << 12 | chars[2] << 6;
      result[offset++] = (byte) (bits >>> 16);
      result[offset++] = (byte) (bits >>> 8);
    } else if (charCount == 2) {
      bits = chars[0] << 18 | chars[1] << 12;
      result[offset++] = (byte) (bits >>> 16);
    } else if (charCount == 1) {
      throw new IllegalArgumentException("Illegal base64 character count");
    }
    return Arrays.copyOf(result, offset);
  }

  private interface Base64Input {

    int size();

    int byteAt(int i);
  }

  private static class InputByteArray implements Base64Input {

    private final byte[] bytes;

    InputByteArray(byte[] bytes) {
      this.bytes = bytes;
    }

    public int size() {
      return bytes.length;
    }

    public int byteAt(int i) {
      return bytes[i];
    }
  }

  private static class InputCharArray implements Base64Input {

    private final char[] chars;

    InputCharArray(char[] chars) {
      this.chars = chars;
    }

    public int size() {
      return chars.length;
    }

    public int byteAt(int i) {
      return chars[i];
    }
  }

  private static class InputCharSeq implements Base64Input {

    private final CharSequence chars;

    InputCharSeq(CharSequence chars) {
      this.chars = chars;
    }

    public int size() {
      return chars.length();
    }

    public int byteAt(int i) {
      return chars.charAt(i);
    }
  }
}
