/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package com.struqt.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MeasureBase64 {

  private static final byte[] bytes = new byte[1024];
  private static final String encoded;
  private static final byte[] encodedBytes;
  private static final String encodedUrlSafe;
  private static final String encodedMime;

  static {
    new Random().nextBytes(bytes);
    encoded = java.util.Base64.getEncoder().encodeToString(bytes);
    encodedUrlSafe = java.util.Base64.getUrlEncoder().encodeToString(bytes);
    encodedMime = java.util.Base64.getMimeEncoder().encodeToString(bytes);
    encodedBytes = encoded.getBytes(StandardCharsets.US_ASCII);
  }

  // /*

  @Benchmark
  public void decode() {
    struqt.util.Base64.decode(encodedBytes);
  }

  @Benchmark
  public void decodeJava8() {
    java.util.Base64.getDecoder().decode(encodedBytes);
  }

  @Benchmark
  public void decodeUrlSafe() {
    struqt.util.Base64.decode(encodedUrlSafe);
  }

  @Benchmark
  public void decodeUrlSafeJava8() {
    java.util.Base64.getMimeDecoder().decode(encodedUrlSafe);
  }

  @Benchmark
  public void decodeMime() {
    struqt.util.Base64.decode(encodedMime);
  }

  @Benchmark
  public void decodeMimeJava8() {
    java.util.Base64.getMimeDecoder().decode(encodedMime);
  }

  @Benchmark
  public void encode() {
    struqt.util.Base64.encodeToString(bytes);
  }

  @Benchmark
  public void encodeJava8() {
    java.util.Base64.getEncoder().encodeToString(bytes);
  }

  @Benchmark
  public void encodeUrlSafe() {
    struqt.util.Base64.encodeToUrlSafe(bytes);
  }

  @Benchmark
  public void encodeUrlSafeJava8() {
    java.util.Base64.getUrlEncoder().encodeToString(bytes);
  }

  @Benchmark
  public void encodeMime() {
    struqt.util.Base64.encodeToMime(bytes);
  }

  @Benchmark
  public void encodeMimeJava8() {
    java.util.Base64.getMimeEncoder().encodeToString(bytes);
  }

  // */

  public static void main(String[] args) throws RunnerException {
    new Runner(
            new OptionsBuilder()
                .include(MeasureBase64.class.getSimpleName())
                .forks(1)
                .threads(Runtime.getRuntime().availableProcessors())
                .mode(Mode.AverageTime)
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(2))
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupIterations(5)
                .build())
        .run();
  }
}
