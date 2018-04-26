/*
 * Copyright (c) 2018. Kang Wang. The following code is distributed under
 * the terms of the MIT license found at http://opensource.org/licenses/MIT
 */

package struqt.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import struqt.util.UniqueId;
import struqt.util.UniqueIdCodec;
import struqt.util.UniqueIdGenerator;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing UniqueID and Generator")
class UniqueIdTest {
  private static final Logger log = LoggerFactory.getLogger(UniqueIdTest.class);
  private static final Random random = new SecureRandom();

  @Nested
  @DisplayName("Testing Unique ID UniqueIdCodec")
  class TestCodec {
    @Test
    void defaultBits() {
      UniqueIdCodec codec = UniqueId.getCodec();
      assertEquals(0x7FFFFFFFFFFFFFFFL, codec.encode(-1L, -1L, -1L));
      assertEquals(0x7FFFFFFFFFFL, codec.getTimestampMax());
      assertEquals(0x1FFL, codec.getInstanceMax());
      assertEquals(0x7FFL, codec.getSequenceMax());
      assertEquals(0x7FFFFFFFFFFL, codec.getTimestamp(-1L));
      assertEquals(0x1FFL, codec.getInstance(-1L));
      assertEquals(0x7FFL, codec.getSequence(-1L));
    }

    @Test
    void customBits() {
      UniqueIdCodec codec = new UniqueIdCodec(43L, 10L);
      assertEquals(0x7FFFFFFFFFFL, codec.getTimestampMax());
      assertEquals(0x3FFL, codec.getInstanceMax());
      assertEquals(0x3FFL, codec.getSequenceMax());
    }

    @Test
    void exceptionThrows() {
      Assertions.assertThrows(IllegalArgumentException.class, () -> new UniqueIdCodec(-1L, 1L));
      Assertions.assertThrows(IllegalArgumentException.class, () -> new UniqueIdCodec(1L, -1L));
      Assertions.assertThrows(IllegalArgumentException.class, () -> new UniqueIdCodec(40L, 23L));
    }
  }

  @Nested
  @DisplayName("Testing Unique ID")
  class TestUniqueId {
    @Test
    void minUniqueId() {
      UniqueId id = UniqueId.decode(0L);
      assertEquals(0L, id.getValue());
      assertEquals(0L, id.getTimestamp());
      assertEquals(0L, id.getInstance());
      assertEquals(0L, id.getSequence());
      assertEquals("1970-01-01T00:00:00Z", Instant.ofEpochMilli(id.getTimestamp()).toString());
    }

    @Test
    void maxUniqueId() {
      UniqueId id = UniqueId.decode(-1L);
      assertEquals(0x7FFFFFFFFFFFFFFFL, id.getValue());
      assertEquals(0x7FFFFFFFFFFL, id.getTimestamp());
      assertEquals(0x1FFL, id.getInstance());
      assertEquals(0x7FFL, id.getSequence());
      assertEquals("2248-09-26T15:10:22.207Z", Instant.ofEpochMilli(id.getTimestamp()).toString());
    }

    @ParameterizedTest
    @ArgumentsSource(RandomParamsProvider.class)
    void randomUniqueId(long timestamp, long sequence, long instance) {
      UniqueId id = UniqueId.decode(UniqueId.encode(timestamp, instance, sequence));
      assertEquals(timestamp, id.getTimestamp());
      assertEquals(instance, id.getInstance());
      assertEquals(sequence, id.getSequence());
    }

    @ParameterizedTest
    @ArgumentsSource(RandomParamsProvider.class)
    void equals(long timestamp, long sequence, long instance) {
      UniqueId id1 = UniqueId.valueOf(timestamp, instance, sequence);
      UniqueId id2 = UniqueId.valueOf(timestamp, instance, sequence);
      assertEquals(id1, id1);
      assertEquals(id1, id2);
      assertNotEquals(id1, new Object());
    }

    @ParameterizedTest
    @ArgumentsSource(RandomParamsProvider.class)
    void hashCode(long timestamp, long sequence, long instance) {
      UniqueId id = UniqueId.valueOf(timestamp, instance, sequence);
      long value = id.getValue();
      assertEquals((int) (value ^ (value >>> 32)), id.hashCode());
    }

    @ParameterizedTest
    @ArgumentsSource(RandomParamsProvider.class)
    void toString(long timestamp, long sequence, long instance) {
      UniqueId id = UniqueId.valueOf(timestamp, instance, sequence);
      long value = id.getValue();
      String s =
          "UniqueId{"
              + "value="
              + value
              + ", instance="
              + instance
              + ", timestamp="
              + timestamp
              + ", sequence="
              + sequence
              + '}';
      assertEquals(s, id.toString());
    }
  }

  @Nested
  @DisplayName("Testing Unique ID Generator")
  class TestGenerator {
    private final UniqueIdGenerator generator = new UniqueIdGenerator(1);
    private final ExecutorService executor = ForkJoinPool.commonPool();
    private static final int latchCount = 40960;
    private final CountDownLatch latch = new CountDownLatch(latchCount);

    @Test
    void generatorSingle() {
      int len = latchCount;
      long[] array = new long[len];
      long start = System.nanoTime();
      for (int i = 0; i < len; i++) {
        array[i] = generator.next();
      }
      log.info("Generator single thread - {} nano sec", System.nanoTime() - start);
      assertTrue(checkUnique(array));
    }

    @Test
    void generatorConcurrent() throws InterruptedException {
      int len = latchCount;
      long[] array = new long[len];
      long start = System.nanoTime();
      for (int index = 0; index < len; index++) {
        final int i = index;
        executor.submit(
            () -> {
              array[i] = generator.next();
              latch.countDown();
            });
      }
      if (latch.await(10, TimeUnit.SECONDS)) {
        log.info("Generator multi thread - {} nano sec", System.nanoTime() - start);
        assertTrue(checkUnique(array));
      }
    }

    @Test
    void exceptionThrows() {
      FakeGenerator fake = new FakeGenerator();
      FakeGenerator.fakeTime -= 100;
      Assertions.assertThrows(RuntimeException.class, fake::next);
    }
  }

  private static boolean checkUnique(long[] numbers) {
    Arrays.sort(numbers);
    for (int i = 1; i < numbers.length; i++) {
      if (numbers[i] == numbers[i - 1]) {
        return false;
      }
    }
    return true;
  }

  private static final class RandomParamsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      RandomParams[] params = new RandomParams[5];
      for (int i = 0; i < params.length; i++) {
        params[i] = new RandomParams();
      }
      return Arrays.stream(params);
    }
  }

  private static final class RandomParams implements Arguments {
    @Override
    public Object[] get() {
      long timestamp = Instant.now().toEpochMilli();
      long sequence = random.nextInt((int) UniqueId.getCodec().getSequenceMax() + 1);
      long instance = random.nextInt((int) UniqueId.getCodec().getInstanceMax() + 1);
      return new Long[] {timestamp, sequence, instance};
    }
  }

  private static final class FakeGenerator extends UniqueIdGenerator {
    static long fakeTime = Integer.MAX_VALUE;

    private FakeGenerator() {
      super(0L);
    }

    @Override
    protected long systemTimeMillis() {
      return fakeTime++;
    }
  }
}
