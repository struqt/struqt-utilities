/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;

/**
 * Represents a function that writes the specified byte to this byte stream.
 *
 * @author Kang Wang
 * @since 1.2
 */
public interface StreamWriter {
  /**
   * Writes the specified byte to this byte stream. The general contract for {@code write} is that
   * one byte is written to the byte stream. The byte to be written is the eight low-order bits of
   * the argument {@code b}. The 24 high-order bits of {@code b} are ignored.
   *
   * <p>Implementing this method with non-blocking IO is recommended.
   *
   * @param b the specified byte
   * @throws java.io.IOException if an I/O error occurs
   * @see java.io.OutputStream#write(int)
   * @since 1.2
   */
  void write(int b) throws IOException;
}
