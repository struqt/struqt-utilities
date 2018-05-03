/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

/**
 * Base64 implementation specified in <a href="https://tools.ietf.org/html/rfc4648">RFC 4648</a>
 *
 * @author wangkang
 * @since 1.1
 */
public class Base64 {

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

  /**
   * Generate Base 64 encoding as specified in <a
   * href="https://tools.ietf.org/html/rfc4648#section-4">RFC 4648</a>.
   *
   * @param bytes Byte array as input
   * @return Base64 encoded String as output
   */
  public static String encodeToString(byte[] bytes) {
    return String.valueOf(encode(bytes, false, false, true));
  }

  /**
   * Generate Base 64 encoding as specified in <a
   * href="https://tools.ietf.org/html/rfc4648#section-4">RFC 4648</a>.
   *
   * @param bytes Byte array as input
   * @param padding Whether fill the padding character in the end of output
   * @return Base64 encoded String as output
   */
  public static char[] encodeToChars(byte[] bytes, boolean padding) {
    return encode(bytes, false, false, padding);
  }

  /**
   * Generate Base 64 Encoding with URL and Filename Safe Alphabet as specified in <a
   * href="https://tools.ietf.org/html/rfc4648#section-5">RFC 4648</a>.
   *
   * @param bytes Byte array as input
   * @return Base64 encoded String as output
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
   */
  public static String encodeToMime(byte[] bytes) {
    return String.valueOf(encode(bytes, false, true, true));
  }

  private static char[] encode(byte[] bytes, boolean url, boolean mime, boolean padding) {
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
}
