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
   * Constructor of class UniqueIdGenerator.
   *
   * @param instanceId ID of a distributed application instance
   */
  public UniqueIdGenerator(final long instanceId) {
    this.codec = UniqueId.getCodec();
    this.instance = instanceId;
    this.sequenceMax = codec.getSequenceMax();
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
        yield();
      }
      timeMillis = now;
    } else {
      throw new RuntimeException("Time reversal ??");
    }
  }

  private void yield() {
    try {
      Thread.sleep(0L);
    } catch (InterruptedException ignored) {
    }
  }

  protected long systemTimeMillis() {
    return System.currentTimeMillis();
  }
}
