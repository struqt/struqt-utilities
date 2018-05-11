package com.struqt.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import struqt.util.UniqueIdGenerator;

import java.util.concurrent.TimeUnit;

public class MeasureUniqueId {

  private static final UniqueIdGenerator generator = new UniqueIdGenerator(1);

  @Benchmark
  public void generate() {
    generator.next();
  }

  public static void main(String[] args) throws RunnerException {
    new Runner(
            new OptionsBuilder()
                .include(MeasureUniqueId.class.getSimpleName())
                .forks(1)
                .threads(Runtime.getRuntime().availableProcessors())
                .mode(Mode.Throughput)
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(3))
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupIterations(5)
                .build())
        .run();
  }
}
