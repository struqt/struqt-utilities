/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

/**
 * Represents bit set options for controlling how to decode some parts of a byte sequence.
 *
 * @author Kang Wang
 * @since 1.2
 * @see DataPacket#getOptions()
 */
public final class DataPacketOptions {

  private static final int OPTION_SIGNATURE /* */ = 1 << 3;
  private static final int OPTION_ENCRYPT /*   */ = 1 << 2;
  private static final int OPTION_COMPRESS /*  */ = 1 << 1;
  private static final int OPTION_DATA /*      */ = 1;

  private final int value;
  private final boolean signature;
  private final boolean encrypt;
  private final boolean compress;
  private final boolean data;

  /**
   * Construct an instance with an integer that contains all bit set {@code options}.
   *
   * @param options an integer that contains all bit set {@code options}
   * @see DataPacketDecoder#decode(StreamReader)
   */
  public DataPacketOptions(final int options) {
    this.value = options;
    this.signature = (options & OPTION_SIGNATURE & options) > 0;
    this.encrypt = (options & OPTION_ENCRYPT) > 0;
    this.compress = (options & OPTION_COMPRESS) > 0;
    this.data = (options & OPTION_DATA) > 0;
  }

  /**
   * Construct an instance with some specified {@code boolean} arguments.
   *
   * @param signature if signature bit is set
   * @param encrypt if encrypt bit is set
   * @param compress if compress bit is set
   * @param data if data bit is set
   * @see DataPacket.Builder#build()
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

  /**
   * Returns an integer that contains all bit set {@code options}.
   *
   * @return an integer that contains all bit set {@code options}
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns a {@code boolean} value indicating whether the packet has a signature to make sure the
   * packet is free of been tampered with.
   *
   * @return true when {@code 0x08 & value} is not 0 and false otherwise
   */
  public boolean hasSignature() {
    return signature;
  }

  /**
   * Returns a {@code boolean} value indicating whether the packet data is encrypted to prevent
   * sensitive information from being leaked.
   *
   * @return true when {@code 0x04 & value} is not 0 and false otherwise
   */
  public boolean hasEncrypt() {
    return encrypt;
  }

  /**
   * Returns a {@code boolean} value indicating whether the packet data is compressed to maintain a
   * smaller size.
   *
   * @return true when {@code 0x02 & value} is not 0 and false otherwise
   */
  public boolean hasCompress() {
    return compress;
  }

  /**
   * Returns a {@code boolean} value indicating whether the packet data is absent.
   *
   * @return true when {@code 0x01 & value} is not 0 and false otherwise
   */
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

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  public int hashCode() {
    return value;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return String.valueOf(value);
  }
}
