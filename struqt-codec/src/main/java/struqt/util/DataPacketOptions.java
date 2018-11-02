/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

public class DataPacketOptions {

  private static final int OPTION_SIGNATURE = 1 << 3;
  private static final int OPTION_ENCRYPT = 1 << 2;
  private static final int OPTION_COMPRESS = 1 << 1;
  private static final int OPTION_DATA = 1;

  private final int value;
  private final boolean signature;
  private final boolean encrypt;
  private final boolean compress;
  private final boolean data;

  /**
   * TODO ...
   *
   * @param options option bits
   */
  public DataPacketOptions(final int options) {
    this.value = options;
    this.signature = (options & OPTION_SIGNATURE & options) > 0;
    this.encrypt = (options & OPTION_ENCRYPT) > 0;
    this.compress = (options & OPTION_COMPRESS) > 0;
    this.data = (options & OPTION_DATA) > 0;
  }

  /**
   * TODO ...
   *
   * @param signature if signature bit is set
   * @param encrypt if encrypt bit is set
   * @param compress if compress bit is set
   * @param data if data bit is set
   */
  public DataPacketOptions(boolean signature, boolean encrypt, boolean compress, boolean data) {
    this.signature = signature;
    this.encrypt = encrypt;
    this.compress = compress;
    this.data = data;
    int value = 0;
    if (signature) {
      value |= OPTION_SIGNATURE;
    }
    if (encrypt) {
      value |= OPTION_ENCRYPT;
    }
    if (compress) {
      value |= OPTION_COMPRESS;
    }
    if (data) {
      value |= OPTION_DATA;
    }
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public boolean hasSignature() {
    return signature;
  }

  public boolean hasEncrypt() {
    return encrypt;
  }

  public boolean hasCompress() {
    return compress;
  }

  public boolean hasData() {
    return data;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare
   * @return {@code true} if this object is the same as the {@code o} argument; {@code false}
   *     otherwise.
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DataPacketOptions)) {
      return false;
    }
    DataPacketOptions that = (DataPacketOptions) o;
    return value == that.value;
  }

  public int hashCode() {
    return value;
  }

  public String toString() {
    return String.valueOf(value);
  }
}
