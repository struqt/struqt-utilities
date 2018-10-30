/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class implements a method that encodes a 64-bit signed integer into a sequence of
 * variable-length bytes while supporting the corresponding decoding.
 *
 * <p>The encoding method follows the Little Endian Base 128 (LEB128) algorithm as described in
 * Section 7.6 of the <a href="http://dwarfstd.org/doc/Dwarf3.pdf">DWARF v3</a> documentation.
 *
 * @author Kang Wang
 * @since 1.2
 */
public class VarLengthInt64 {

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
  public static int encode(long value, OutputStream destination) throws IOException {
    int count = 0;
    long remaining = value >> 7;
    long end = value >= 0L ? 0L : -1L;
    boolean hasMore = true;
    while (hasMore) {
      hasMore = (remaining != end) || ((remaining & 1L) != ((value >> 6) & 1L));
      destination.write((int) ((value & 0x7F) | (hasMore ? 0x80L : 0L)));
      value = remaining;
      remaining >>= 7;
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
  public static int encode(long value, int size, byte[] destination, int offset) {
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
    int index = offset;
    long remaining = value >> 7;
    long end = value >= 0L ? 0L : -1L;
    boolean hasMore = true;
    while (hasMore) {
      hasMore = (remaining != end) || ((remaining & 1L) != ((value >> 6) & 1L));
      destination[index] = (byte) ((value & 0x7F) | (hasMore ? 0x80L : 0L));
      value = remaining;
      remaining >>= 7;
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
      else if (value < 0x20_00L) return 2;
      else if (value < 0x10_00_00L) return 3;
      else if (value < 0x08_00_00_00L) return 4;
      else if (value < 0x04_00_00_00_00L) return 5;
      else if (value < 0x02_00_00_00_00_00L) return 6;
      else if (value < 0x01_00_00_00_00_00_00L) return 7;
      else if (value < 0x80_00_00_00_00_00_00L) return 8;
      else if (value < 0x40_00_00_00_00_00_00_00L) return 9;
      else return 10;
    } else {
      if (value >= -0x40L) return 1;
      else if (value >= -0x20_00L) return 2;
      else if (value >= -0x10_00_00L) return 3;
      else if (value >= -0x08_00_00_00L) return 4;
      else if (value >= -0x04_00_00_00_00L) return 5;
      else if (value >= -0x02_00_00_00_00_00L) return 6;
      else if (value >= -0x01_00_00_00_00_00_00L) return 7;
      else if (value >= -0x80_00_00_00_00_00_00L) return 8;
      else if (value >= -0x40_00_00_00_00_00_00_00L) return 9;
      else return 10;
    }
  }

  /*
  public static long decode(InputStream source) throws IOException {
    return 0;
  } //*/
}
