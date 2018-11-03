/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.io.IOException;
import java.util.Arrays;

/**
 * Represents data to be encoded or stores data that decoded from a sequence of byte.
 *
 * @author Kang Wang
 * @since 1.2
 */
public final class DataPacket {

  /** A constant bytes as starting mark; "SDP" is short for "Struqt Data Package/Protocol". */
  public static final String START = "SDP";
  /** Default version of this packet implementation. */
  public static final int VERSION = 1;
  /** Default revision of this packet implementation. */
  public static final int REVISION = 1;

  private final int version;
  private final int revision;
  private final int nonce;
  private final DataPacketOptions options;
  private final int sign;
  private final int crypto;
  private final int compress;
  private final int format;
  private final int length;
  private final byte[] data;
  private final byte[] signature;

  private DataPacket(
      int version,
      int revision,
      int nonce,
      DataPacketOptions options,
      int sign,
      int crypto,
      int compress,
      int format,
      int length,
      byte[] data,
      byte[] signature) {
    this.version = version;
    this.revision = revision;
    this.nonce = nonce;
    this.options = options;
    this.sign = sign;
    this.crypto = crypto;
    this.compress = compress;
    this.format = format;
    this.length = length;
    this.data = data;
    this.signature = signature;
  }

  /**
   * Creates a new {@code DataPacket.Builder} object to build a new {@code DataPacket} instance.
   *
   * @return a new {@code DataPacket.Builder} object
   */
  public static Builder builder() {
    return new Builder();
  }

  public static DataPacket decode(StreamReader reader) throws IOException {
    return DataPacketDecoder.decode(reader);
  }

  /**
   * Writes the start constant bytes to the {@code stream}.
   *
   * @param stream destination {@code stream}
   * @return number of the written bytes
   * @throws IOException If an I/O error occurs
   */
  public static int writeStart(StreamWriter stream) throws IOException {
    String mark = DataPacket.START;
    int len = mark.length();
    for (int i = 0; i < len; ++i) {
      stream.write(mark.charAt(i));
    }
    return len;
  }

  public int sizeof() {
    return DataPacketEncoder.encoder(this).sizeof(this);
  }

  public int encode(StreamWriter writer) throws IOException {
    return DataPacketEncoder.encode(this, writer);
  }

  public int getVersion() {
    return this.version;
  }

  public int getRevision() {
    return this.revision;
  }

  public int getNonce() {
    return this.nonce;
  }

  public DataPacketOptions getOptions() {
    return this.options;
  }

  public int getSign() {
    return this.sign;
  }

  public int getCrypto() {
    return this.crypto;
  }

  public int getCompress() {
    return this.compress;
  }

  public int getFormat() {
    return this.format;
  }

  public int getLength() {
    return this.length;
  }

  public int dataSize() {
    return data == null ? 0 : data.length;
  }

  public byte dataByte(int index) {
    return data[index];
  }

  public int signatureSize() {
    return signature == null ? 0 : signature.length;
  }

  public byte signatureByte(int index) {
    return signature[index];
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object.
   */
  public int hashCode() {
    final int prime = 59;
    int result = 1;
    result = result * prime + this.getVersion();
    result = result * prime + this.getRevision();
    result = result * prime + this.getNonce();
    result = result * prime + this.getOptions().getValue();
    result = result * prime + this.getSign();
    result = result * prime + this.getCrypto();
    result = result * prime + this.getCompress();
    result = result * prime + this.getFormat();
    result = result * prime + this.getLength();
    result = result * prime + Arrays.hashCode(this.data);
    result = result * prime + Arrays.hashCode(this.signature);
    return result;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return "struqt.util.DataPacket(version="
        + this.getVersion()
        + ", revision="
        + this.getRevision()
        + ", nonce="
        + this.getNonce()
        + ", options="
        + this.getOptions()
        + ", sign="
        + this.getSign()
        + ", crypto="
        + this.getCrypto()
        + ", compress="
        + this.getCompress()
        + ", format="
        + this.getFormat()
        + ", length="
        + this.getLength()
        + ", data="
        + java.util.Arrays.toString(this.data)
        + ", signature="
        + java.util.Arrays.toString(this.signature)
        + ")";
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare
   * @return {@code true} if this object is the same as the {@code o} argument; {@code false}
   *     otherwise.
   */
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DataPacket)) {
      return false;
    }
    final DataPacket other = (DataPacket) o;
    if (this.getVersion() != other.getVersion()) {
      return false;
    }
    if (this.getRevision() != other.getRevision()) {
      return false;
    }
    if (this.getNonce() != other.getNonce()) {
      return false;
    }
    if (!this.getOptions().equals(other.getOptions())) {
      return false;
    }
    if (this.getSign() != other.getSign()) {
      return false;
    }
    if (this.getCrypto() != other.getCrypto()) {
      return false;
    }
    if (this.getCompress() != other.getCompress()) {
      return false;
    }
    if (this.getFormat() != other.getFormat()) {
      return false;
    }
    if (this.getLength() != other.getLength()) {
      return false;
    }
    if (!Arrays.equals(this.data, other.data)) {
      return false;
    }
    return Arrays.equals(this.signature, other.signature);
  }

  public static class Builder {

    private int version = VERSION;
    private int revision = REVISION;
    private int nonce;
    private int sign;
    private int crypto;
    private int compress;
    private int format;
    private int length;
    private byte[] data;
    private byte[] signature;

    private Builder() {}

    /**
     * Returns a {@code DataPacket} object.
     *
     * @return a {@code DataPacket} object
     */
    public DataPacket build() {
      boolean hasEncrypt = crypto != 0;
      boolean hasCompress = compress != 0;
      boolean hasData = data != null;
      boolean hasSignature = signature != null;
      DataPacketOptions ops = new DataPacketOptions(hasSignature, hasEncrypt, hasCompress, hasData);
      return new DataPacket(
          version, revision, nonce, ops, //
          sign, crypto, compress, format, length, data, signature);
    }

    public DataPacket.Builder version(int version) {
      this.version = version;
      return this;
    }

    public DataPacket.Builder revision(int revision) {
      this.revision = revision;
      return this;
    }

    public DataPacket.Builder nonce(int nonce) {
      this.nonce = nonce;
      return this;
    }

    public DataPacket.Builder sign(int sign) {
      this.sign = sign;
      return this;
    }

    public DataPacket.Builder crypto(int crypto) {
      this.crypto = crypto;
      return this;
    }

    public DataPacket.Builder compress(int compress) {
      this.compress = compress;
      return this;
    }

    public DataPacket.Builder format(int format) {
      this.format = format;
      return this;
    }

    public DataPacket.Builder length(int length) {
      this.length = length;
      return this;
    }

    /**
     * Set payload data bytes.
     *
     * @param bytes payload data bytes
     * @return this object
     */
    public DataPacket.Builder data(byte[] bytes) {
      if (bytes == null) {
        this.data = null;
      } else {
        this.data = Arrays.copyOf(bytes, bytes.length);
      }
      return this;
    }

    /**
     * Set signature data bytes.
     *
     * @param bytes signature data bytes
     * @return this object
     */
    public DataPacket.Builder signature(byte[] bytes) {
      if (bytes == null) {
        this.signature = null;
      } else {
        this.signature = Arrays.copyOf(bytes, bytes.length);
      }
      return this;
    }
  }
}
