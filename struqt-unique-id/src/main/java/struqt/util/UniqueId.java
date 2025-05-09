/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

/**
 * Provide UniqueId data definition and some helper static method with a default encode and decode
 * rules.
 *
 * <p>The default UniqueIdCodec use 43 bits to present timestamp, 9 bits to present a distributed
 * instance id, and 11 bits to present a sequence number.
 *
 * @author wangkang
 * @since 1.0
 */
public class UniqueId {

  private static final UniqueIdCodec CODEC = new UniqueIdCodec(43L, 9L);
  private final long value;
  private final long instance;
  private final long timestamp;
  private final long sequence;

  public static UniqueId valueOf(final long timeMillis, final long instance, final long sequence) {
    return decode(encode(timeMillis, instance, sequence));
  }

  public static UniqueId valueOf(final long id) {
    return getCodec().decode(id);
  }

  public static UniqueId decode(final long id) {
    return getCodec().decode(id);
  }

  public static long encode(final long timeMillis, final long instance, final long sequence) {
    return getCodec().encode(timeMillis, instance, sequence);
  }

  public static UniqueIdCodec getCodec() {
    return CODEC;
  }

  UniqueId(final long timestamp, final long instance, final long sequence) {
    this.value = CODEC.encode(timestamp, instance, sequence);
    this.timestamp = CODEC.getTimestamp(value);
    this.instance = CODEC.getInstance(value);
    this.sequence = CODEC.getSequence(value);
  }

  public long getTimestamp() {
    return timestamp;
  }

  public long getInstance() {
    return instance;
  }

  public long getSequence() {
    return sequence;
  }

  public long getValue() {
    return value;
  }

  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UniqueId)) {
      return false;
    }
    UniqueId uniqueId = (UniqueId) o;
    return getValue() == uniqueId.getValue();
  }

  public int hashCode() {
    return Long.valueOf(getValue()).hashCode();
  }

  public String toString() {
    return "UniqueId{"
        + "value="
        + value
        + ", instance="
        + instance
        + ", timestamp="
        + timestamp
        + ", sequence="
        + sequence
        + '}';
  }
}
