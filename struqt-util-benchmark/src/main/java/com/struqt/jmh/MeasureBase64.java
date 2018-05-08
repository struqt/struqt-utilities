/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package com.struqt.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@Warmup(iterations = 10)
@Measurement(iterations = 10, time = 2)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MeasureBase64 {

  private static final byte[] bytes = new byte[4096];
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

  @Benchmark
  public void decode() {
    struqt.util.Base64.decode(encodedBytes);
  }

  @Benchmark
  public void decodeJava8() {
    java.util.Base64.getDecoder().decode(encodedBytes);
  }

  // /*

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
}
