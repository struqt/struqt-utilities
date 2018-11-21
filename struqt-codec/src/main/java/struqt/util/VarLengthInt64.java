/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;

/**
 * This class implements a method of variable length 64-bit signed integer encoding and
 * corresponding decoding.
 *
 * <p>Variable length integers reduce storage or transfer byte length in the case of small values.
 *
 * <p>The encoding format follows the Little Endian Base 128 (LEB128) algorithm as described in
 * Section 7.6 of the <a href="http://dwarfstd.org/doc/Dwarf3.pdf">DWARF v3</a> documentation.
 *
 * <p>For more information on LEB128, see the <a
 * href="https://en.wikipedia.org/wiki/LEB128">LEB128</a> entry on Wiki
 *
 * @author Kang Wang
 * @since 1.2
 */
public final class VarLengthInt64 {

  private static final int BIT_COUNT = 64;
  private static final int ENCODED_BYTE_MAX = 10;

  private final long value;
  private final int size;

  private VarLengthInt64(long value, int size) {
    this.value = value;
    this.size = size;
  }

  public long getValue() {
    return value;
  }

  public int getSize() {
    return size;
  }

  private static VarLengthInt64 create(long value, int size) {
    assert size == sizeof(value);
    return new VarLengthInt64(value, size);
  }

  /**
   * Encodes the specified {@code value} argument with signed LEB128 algorithm and writes the
   * encoding result to the {@code destination} byte stream argument.
   *
   * @param value The {@code value} argument to be encoded
   * @param destination The {@code destination} byte stream argument where the encoding result is
   *     written to
   * @return An integer that indicates how many bytes have been written
   * @throws java.io.IOException If an I/O error occurs
   * @see java.io.OutputStream#write(int)
   * @since 1.2
   */
  public static int encode(final long value, final StreamWriter destination) throws IOException {
    long x = value;
    int count = 0;
    long current;
    boolean sign; /* sign bit of byte is set */
    boolean more = true;
    while (more) {
      current = (0x7FL & x);
      x >>= 7;
      sign = (0x40L == (0x40L & current));
      if ((sign && -1L == x) || (!sign && 0L == x)) {
        more = false;
      }
      destination.write((int) ((more ? 0x80L : 0L) | current));
      count++;
    }
    return count;
  }

  /**
   * Encodes the specified {@code value} argument with signed LEB128 and writes the encoding result
   * to the {@code destination} byte array.
   *
   * @param value The {@code value} argument to be encoded
   * @param destination The {@code destination} byte array argument where the encoding result is
   *     written to
   * @return An integer that indicates how many bytes have been written
   * @since 1.2
   */
  public static int encode(long value, byte[] destination) {
    return encode(value, sizeof(value), destination, 0);
  }

  /**
   * Encodes the specified {@code value} argument with signed LEB128 and writes the encoding result
   * to the {@code destination} byte array.
   *
   * @param value The {@code value} argument to be encoded
   * @param destination The {@code destination} byte array argument where the encoding result is
   *     written to
   * @param offset Starting position in the {@code destination} byte array
   * @return An integer that indicates how many bytes have been written
   * @since 1.2
   */
  public static int encode(long value, byte[] destination, int offset) {
    return encode(value, sizeof(value), destination, offset);
  }

  /**
   * Encodes the specified {@code value} argument with signed LEB128 and writes the encoding result
   * to the {@code destination} byte array.
   *
   * @param value The {@code value} argument to be encoded
   * @param size The result byte count when encoding the specified {@code value} argument with
   *     signed LEB128 algorithm
   * @param destination The {@code destination} byte array argument where the encoding result is
   *     written to
   * @return An integer that indicates how many bytes have been written
   * @since 1.2
   */
  public static int encode(long value, int size, byte[] destination) {
    return encode(value, size, destination, 0);
  }

  /**
   * Encodes the specified {@code value} argument with signed LEB128 and writes the encoding result
   * to the {@code destination} byte array.
   *
   * @param value The {@code value} argument to be encoded
   * @param size The result byte count when encoding the specified {@code value} argument with
   *     signed LEB128 algorithm
   * @param destination The {@code destination} byte array argument where the encoding result is
   *     written to
   * @param offset Starting position in the {@code destination} byte array
   * @return An integer that indicates how many bytes have been written
   * @exception NullPointerException If the {@code destination} argument is null
   * @exception IllegalArgumentException If the {@code size} argument is not positive or the {@code
   *     offset} argument is negative or the result of {@code offset}+{@code size} is outside the
   *     bounds of the {@code destination} array
   * @since 1.2
   */
  public static int encode(
      final long value, final int size, final byte[] destination, final int offset) {
    if (size <= 0) {
      throw new IllegalArgumentException("The size argument is not positive");
    }
    if (offset < 0) {
      throw new IllegalArgumentException("The offset argument is negative");
    }
    if (offset + size > destination.length) {
      throw new IllegalArgumentException(
          "The result of offset + size is outside the bounds of the bytes array");
    }
    long x = value;
    int index = offset;
    long current;
    boolean sign; /* sign bit of byte is set */
    boolean more = true;
    while (more) {
      current = (0x7FL & x);
      x >>= 7;
      sign = (0x40L == (0x40L & current));
      if ((sign && -1L == x) || (!sign && 0L == x)) {
        more = false;
      }
      destination[index] = (byte) ((more ? 0x80L : 0L) | current);
      index++;
    }
    return index - offset;
  }

  /**
   * Calculates how many bytes will return when encoding the specified {@code value} argument with
   * signed LEB128 algorithm.
   *
   * @param value The {@code value} argument to be encoded
   * @return The result byte count
   * @since 1.2
   */
  public static int sizeof(long value) {
    if (value >= 0) {
      /* TODO: Google code format warning: 'if' expression need braces */
      if (value < 0x40L) return 1;
      else if (value < 0x2000L) return 2;
      else if (value < 0x100000L) return 3;
      else if (value < 0x08000000L) return 4;
      else if (value < 0x0400000000L) return 5;
      else if (value < 0x020000000000L) return 6;
      else if (value < 0x01000000000000L) return 7;
      else if (value < 0x80000000000000L) return 8;
      else if (value < 0x4000000000000000L) return 9;
      else return 10;
    } else {
      if (value >= -0x40L) return 1;
      else if (value >= -0x2000L) return 2;
      else if (value >= -0x100000L) return 3;
      else if (value >= -0x08000000L) return 4;
      else if (value >= -0x0400000000L) return 5;
      else if (value >= -0x020000000000L) return 6;
      else if (value >= -0x01000000000000L) return 7;
      else if (value >= -0x80000000000000L) return 8;
      else if (value >= -0x4000000000000000L) return 9;
      else return 10;
    }
  }

  /**
   * Decodes a 64-bit signed integer from the {@code source} byte array argument.
   *
   * @param source The {@code source} byte array
   * @return A 64-bit signed integer
   * @since 1.2
   */
  public static VarLengthInt64 decode(byte[] source) {
    return decode(source, 0);
  }

  /**
   * Decodes a 64-bit signed integer from the {@code source} byte array argument.
   *
   * @param source The {@code source} byte array
   * @param offset Starting position in the {@code source} byte array
   * @return A 64-bit signed integer
   * @exception IllegalArgumentException If the {@code offset} of the {@code source} can't be read
   *     or be decoded
   * @since 1.2
   */
  public static VarLengthInt64 decode(byte[] source, int offset) {
    if (null == source || 0 >= source.length || offset < 0 || offset >= source.length) {
      throw new IllegalArgumentException(
          "No byte available at offset position " + offset + " of the source byte array");
    }
    int index = offset;
    int max = ENCODED_BYTE_MAX + offset;
    if (max > source.length) {
      max = source.length;
    }
    int shift = 0;
    int current = 0;
    long result = 0L;
    boolean more = true;
    while (more) {
      if (index >= max) {
        break;
      }
      current = source[index];
      more = current < 0; /* 0x80 == (0x80 & current) */
      index++;
      if (-0x80 != current) {
        result |= (((long) (0x7F & current)) << shift);
      }
      shift += 7;
      if (shift > BIT_COUNT && result == 0) {
        int count = index - offset;
        throw new IllegalArgumentException(
            "The result overflows after reading "
                + count
                + " bytes which means offset position "
                + offset
                + " of the source is not a well formed LEB128");
      }
    }
    if (more) {
      int count = index - offset;
      throw new IllegalArgumentException(
          "No ending byte after reading "
              + count
              + " bytes which means offset position "
              + offset
              + " of the source is not a well formed LEB128");
    }
    if (shift < BIT_COUNT && 0 != (0x40 & current)) {
      result |= (-1L << shift);
    }
    return create(result, index - offset);
  }

  /**
   * Decodes a 64-bit signed integer from the {@code source} byte stream argument.
   *
   * @param source The {@code source} byte stream
   * @return A 64-bit signed integer
   * @since 1.2
   */
  public static VarLengthInt64 decode(StreamReader source) throws IOException {
    int index = 0;
    int shift = 0;
    int current = 0;
    long result = 0L;
    boolean more = true;
    while (more) {
      current = source.read();
      if (-1 == current) {
        if (index == 0) {
          throw new IllegalArgumentException("Reaches the end of the source byte steam");
        }
        break;
      }
      more = 0x80 == (0x80 & current);
      index++;
      if (0x80 != current) {
        result |= (((long) (0x7F & current)) << shift);
      }
      shift += 7;
      if (shift > BIT_COUNT && result == 0) {
        throw new IllegalArgumentException(
            "The result overflows after reading "
                + index
                + " bytes which means the source byte stream is not a well formed LEB128");
      }
    }
    if (more) {
      throw new IllegalArgumentException(
          "No ending byte after reading "
              + index
              + " bytes which means the source byte stream is not a well formed LEB128");
    }
    if (shift < BIT_COUNT && 0 != (0x40 & current)) {
      result |= (-1L << shift);
    }
    return create(result, index);
  }
}
