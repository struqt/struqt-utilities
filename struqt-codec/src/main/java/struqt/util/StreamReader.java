/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;

/**
 * Represents a function that reads the next byte from this byte stream.
 *
 * @author Kang Wang
 * @since 1.2
 */
public interface StreamReader {

  /**
   * Reads the next byte from this byte stream. The value byte is returned as an {@code int} in the
   * range 0 to 255. If no byte is available because the end of the stream has been reached, the
   * value -1 is returned.
   *
   * <p>Implementing this method with non-blocking IO is recommended.
   *
   * @return the next byte of data, or {@code -1} if the end of the stream is reached
   * @throws java.io.IOException if an I/O error occurs
   * @see java.io.InputStream#read()
   * @since 1.2
   */
  int read() throws IOException;
}
