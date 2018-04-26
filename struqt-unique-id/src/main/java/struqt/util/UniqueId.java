/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

public class UniqueId {

  public static UniqueId valueOf(final long timeMillis, final long instance, final long sequence) {
    return decode(encode(timeMillis, instance, sequence));
  }

  public static long encode(final long timeMillis, final long instance, final long sequence) {
    return CODEC.encode(timeMillis, instance, sequence);
  }

  public static UniqueId decode(final long id) {
    return new UniqueId(CODEC.getTimestamp(id), CODEC.getInstance(id), CODEC.getSequence(id));
  }

  public static UniqueIdCodec getCodec() {
    return CODEC;
  }

  private static final UniqueIdCodec CODEC = new UniqueIdCodec(43L, 9L);

  private final long value;
  private final long instance;
  private final long timestamp;
  private final long sequence;

  private UniqueId(final long timestamp, final long instance, final long sequence) {
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

  @Override
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

  @Override
  public int hashCode() {
    return Long.valueOf(getValue()).hashCode();
  }

  @Override
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
