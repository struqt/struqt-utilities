package com.struqt.jmh;

import org.openjdk.jmh.annotations.*;
import struqt.util.UniqueIdGenerator;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 8, time = 5)
@Threads(64)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MeasureUniqueId {

  private static final UniqueIdGenerator generator = new UniqueIdGenerator(1);

  @Benchmark
  public void generate() {
    generator.next();
  }
}
