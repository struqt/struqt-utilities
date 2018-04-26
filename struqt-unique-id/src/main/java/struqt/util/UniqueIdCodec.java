/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

public final class UniqueIdCodec {

  private final long instanceShift;
  private final long instanceMask;
  private final long instanceMax;
  private final long timestampShift;
  private final long timestampMask;
  private final long timestampMax;
  private final long sequenceMask;
  private final long sequenceMax;

  /**
   * Constructor of class UniqueId.UniqueIdCodec.
   *
   * @param timestampBits bit count
   * @param instanceBits bit count of instance id
   */
  public UniqueIdCodec(final long timestampBits, final long instanceBits) {
    if (timestampBits <= 0 || instanceBits <= 0) {
      throw new IllegalArgumentException(
          "Arguments 'timestampBits' and 'instanceBits' must be greater than 0");
    }
    long totalBits = Long.SIZE - 1L;
    long sequenceBits = totalBits - timestampBits - instanceBits;
    if (sequenceBits <= 0L) {
      throw new IllegalArgumentException(
          "Sum of 'timestampBits' and 'instanceBits' must be less than " + totalBits);
    }
    this.instanceShift = sequenceBits;
    this.timestampShift = sequenceBits + instanceBits;
    this.sequenceMask = (-1L ^ (-1L << sequenceBits));
    this.instanceMask = (-1L ^ (-1L << instanceBits)) << instanceShift;
    this.timestampMask = (-1L ^ (-1L << timestampBits)) << timestampShift;
    this.timestampMax = (1L << timestampBits) - 1L;
    this.instanceMax = (1L << instanceBits) - 1L;
    this.sequenceMax = (1L << sequenceBits) - 1L;
  }

  /**
   * Encode three component to a UniqueID value.
   *
   * @param timestamp timestamp component
   * @param instance instance component
   * @param sequence sequence component
   * @return value of UniqueID
   */
  public long encode(final long timestamp, final long instance, final long sequence) {
    return ((timestamp << timestampShift) & timestampMask)
        | ((instance << instanceShift) & instanceMask)
        | (sequence & sequenceMask);
  }

  public long getTimestamp(final long id) {
    return (id & timestampMask) >> timestampShift;
  }

  public long getInstance(final long id) {
    return (id & instanceMask) >> instanceShift;
  }

  public long getSequence(final long id) {
    return (id & sequenceMask);
  }

  public long getTimestampMax() {
    return timestampMax;
  }

  public long getInstanceMax() {
    return instanceMax;
  }

  public long getSequenceMax() {
    return sequenceMax;
  }
}
