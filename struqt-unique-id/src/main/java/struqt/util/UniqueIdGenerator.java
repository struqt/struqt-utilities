/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Another distributed unique ID generator inspired by Twitter's Snowflake.
 *
 * @author wangkang
 * @version 1.0
 */
public class UniqueIdGenerator {

  private final UniqueIdCodec codec;
  private final long instance;
  private final long sequenceMax;
  private final Lock lock;
  private long count;
  private long timeMillis;

  /**
   * Constructor.
   *
   * @param instanceId ID of the generator instance
   */
  public UniqueIdGenerator(final long instanceId) {
    this(instanceId, null);
  }

  /**
   * Constructor.
   *
   * @param instanceId ID of the generator instance
   * @param codec custom codec
   */
  public UniqueIdGenerator(final long instanceId, final UniqueIdCodec codec) {
    this.codec = codec == null ? UniqueId.getCodec() : codec;
    this.instance = instanceId;
    this.sequenceMax = this.codec.getSequenceMax();
    this.lock = new ReentrantLock();
    this.count = 0L;
    this.timeMillis = systemTimeMillis();
  }

  /**
   * Get next new value of UniqueID.
   *
   * @return value of UniqueID
   */
  public final long next() {
    final long id;
    try {
      lock.lock();
      id = nextId();
    } finally {
      lock.unlock();
    }
    return id;
  }

  private long nextId() {
    final long index = sequenceMax & count++;
    if (index == 0L) {
      updateTime();
    }
    return codec.encode(timeMillis, instance, index);
  }

  private void updateTime() {
    long prev = timeMillis;
    long now = systemTimeMillis();
    if (now > prev) {
      timeMillis = now;
    } else if (now == prev) {
      while (now <= prev) {
        now = systemTimeMillis();
      }
      timeMillis = now;
    } else {
      throw new TimeReversalException();
    }
  }

  protected long systemTimeMillis() {
    return System.currentTimeMillis();
  }
}
