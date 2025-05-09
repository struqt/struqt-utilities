package com.struqt.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Field assignment benchmarks with MethodHandle since Java 7 and LambdaMetafactory since Java 8.
 *
 * <p>Benchmark Test Results:
 *
 * <pre>
 * # JMH version: 1.20
 * # VM version: JDK 1.8.0_162, VM 25.162-b12
 * # VM options: none
 * # Warmup: 5 iterations, 1 s each
 * # Measurement: 3 iterations, 2 s each
 * # Threads: 1 thread, will synchronize iterations
 * # Benchmark mode: Average time, time/op
 *
 * Benchmark                                        Mode  Cnt   Score    Error  Units
 * MeasureAssignment.test101DirectField               avgt    3   6.544 ± 1.343  ns/op
 * MeasureAssignment.test102DirectSetter              avgt    3   6.583 ± 0.692  ns/op
 * MeasureAssignment.test201MethodHandleInvokeExact   avgt    3   7.106 ± 1.053  ns/op
 * MeasureAssignment.test202MethodHandleInvoke        avgt    3   7.071 ± 1.235  ns/op
 * MeasureAssignment.test203MethodHandleInvokeSwitch  avgt    3  14.451 ± 2.389  ns/op
 * MeasureAssignment.test301ReflectField              avgt    3  29.724 ± 0.608  ns/op
 * MeasureAssignment.test302ReflectMethod             avgt    3  57.160 ± 4.887  ns/op
 * MeasureAssignment.test401Lambda                    avgt    3   7.026 ± 1.693  ns/op
 * MeasureAssignment.test402LambdaSwitch              avgt    3  14.566 ± 2.882  ns/op
 * </pre>
 *
 * <p>Read more:
 *
 * <ul>
 *   <li><a href="https://jcp.org/en/jsr/detail?id=292">JSR 292</a>
 *   <li><a href="https://gist.github.com/raphw/881e1745996f9d314ab0">FieldBenchmark.java</a>
 *   <li><a href="https://www.optaplanner.org/blog/2018/01/09/JavaReflectionButMuchFaster.html">Java
 *       Reflection, but much faster</a>
 * </ul>
 *
 * @see java.lang.invoke.MethodHandle
 */
@State(Scope.Thread)
public class MeasureAssignment {

  private static MockData parent = new MockData();
  private static List<MockData> children = new ArrayList<>(0);

  public static void main(String[] args) throws Throwable {
    new Runner(
            new OptionsBuilder()
                .include(MeasureAssignment.class.getSimpleName())
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(2))
                .forks(1)
                .build())
        .run();
  }

  @Benchmark
  public MockData test101DirectField() {
    MockData data = new MockData();
    data.boolValue = true;
    data.intValue = 99;
    data.doubleValue = 1.23;
    data.parent = parent;
    data.children = children;
    return data;
  }

  @Benchmark
  public MockData test102DirectSetter() {
    MockData data = new MockData();
    data.setBoolValue(true);
    data.setIntValue(99);
    data.setDoubleValue(1.23);
    data.setParent(parent);
    data.setChildren(children);
    return data;
  }

  @Benchmark
  public MockData test201MethodHandleInvokeExact() throws Throwable {
    MockData data = new MockData();
    MockData.Assignment.intValueSetter.invokeExact(data, 99);
    MockData.Assignment.boolValueSetter.invokeExact(data, true);
    MockData.Assignment.doubleValueSetter.invokeExact(data, 1.23);
    MockData.Assignment.parentSetter.invokeExact(data, parent);
    MockData.Assignment.childrenSetter.invokeExact(data, children);
    return data;
  }

  @Benchmark
  public MockData test202MethodHandleInvoke() throws Throwable {
    MockData data = new MockData();
    MockData.Assignment.intValueSetter.invoke(data, 99);
    MockData.Assignment.boolValueSetter.invoke(data, true);
    MockData.Assignment.doubleValueSetter.invoke(data, 1.23);
    MockData.Assignment.parentSetter.invoke(data, parent);
    MockData.Assignment.childrenSetter.invoke(data, children);
    return data;
  }

  @Benchmark
  public MockData test203MethodHandleInvokeSwitch() throws Throwable {
    MockData data = new MockData();
    MockData.Assignment.setObject(data, "intValue", 98);
    MockData.Assignment.setObject(data, "boolValue", true);
    MockData.Assignment.setObject(data, "doubleValue", 1.23);
    MockData.Assignment.setObject(data, "parent", parent);
    MockData.Assignment.setObject(data, "children", children);
    return data;
  }

  @Benchmark
  public MockData test301ReflectField() throws Throwable {
    MockData data = new MockData();
    MockData.Assignment2.intValueSetterF.setInt(data, 97);
    MockData.Assignment2.boolValueSetterF.setBoolean(data, true);
    MockData.Assignment2.doubleValueSetterF.setDouble(data, 1.23);
    MockData.Assignment2.parentSetterF.set(data, parent);
    MockData.Assignment2.childrenSetterF.set(data, children);
    return data;
  }

  @Benchmark
  public MockData test302ReflectMethod() throws Throwable {
    MockData data = new MockData();
    MockData.Assignment3.intValueSetterM.invoke(data, 99);
    MockData.Assignment3.boolValueSetterM.invoke(data, true);
    MockData.Assignment3.doubleValueSetterM.invoke(data, 1.23);
    MockData.Assignment3.parentSetterM.invoke(data, parent);
    MockData.Assignment3.childrenSetterM.invoke(data, children);
    return data;
  }

  @Benchmark
  public MockData test401Lambda() {
    MockData data = new MockData();
    MockData.Assignment4.intValueSetterFunc.accept(data, 99);
    MockData.Assignment4.boolValueSetterFunc.accept(data, true);
    MockData.Assignment4.doubleValueSetterFunc.accept(data, 1.23);
    MockData.Assignment4.parentSetterFunc.accept(data, parent);
    MockData.Assignment4.childrenSetterFunc.accept(data, children);
    return data;
  }

  @Benchmark
  public MockData test402LambdaSwitch() {
    MockData data = new MockData();
    MockData.Assignment4.setObject(data, "intValue", 98);
    MockData.Assignment4.setObject(data, "boolValue", true);
    MockData.Assignment4.setObject(data, "doubleValue", 1.23);
    MockData.Assignment4.setObject(data, "parent", parent);
    MockData.Assignment4.setObject(data, "children", children);
    return data;
  }

  private static class MockData {

    public int intValue;
    public boolean boolValue;
    public double doubleValue;
    public MockData parent;
    public List<MockData> children;

    public final void setIntValue(int intValue) {
      this.intValue = intValue;
    }

    public final void setBoolValue(boolean boolValue) {
      this.boolValue = boolValue;
    }

    public final void setDoubleValue(double doubleValue) {
      this.doubleValue = doubleValue;
    }

    public final void setParent(MockData parent) {
      this.parent = parent;
    }

    public final void setChildren(List<MockData> children) {
      this.children = children;
    }

    private abstract static class Assignment {
      private static final MethodHandle intValueSetter;
      private static final MethodHandle boolValueSetter;
      private static final MethodHandle doubleValueSetter;
      private static final MethodHandle parentSetter;
      private static final MethodHandle childrenSetter;

      private static void setObject(MockData data, String field, Object value) throws Throwable {
        int hash = field.hashCode();
        switch (hash) {
          case 556050114:
            intValueSetter.invokeExact(data, (int) value);
            break;
          case 2044569767:
            boolValueSetter.invokeExact(data, (boolean) value);
            break;
          case -1626611680:
            doubleValueSetter.invokeExact(data, (double) value);
            break;
          case -995424086:
            parentSetter.invokeExact(data, (MockData) value);
            break;
          case 1659526655:
            childrenSetter.invokeExact(data, (List) value);
            break;
          default:
            break;
        }
      }

      static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
          intValueSetter = lookup.findSetter(MockData.class, "intValue", int.class);
          boolValueSetter = lookup.findSetter(MockData.class, "boolValue", boolean.class);
          doubleValueSetter = lookup.findSetter(MockData.class, "doubleValue", double.class);
          parentSetter = lookup.findSetter(MockData.class, "parent", MockData.class);
          childrenSetter = lookup.findSetter(MockData.class, "children", List.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
      }
    }

    private static class Assignment2 {
      private static final Field intValueSetterF;
      private static final Field boolValueSetterF;
      private static final Field doubleValueSetterF;
      private static final Field parentSetterF;
      private static final Field childrenSetterF;

      static {
        try {
          intValueSetterF = MockData.class.getDeclaredField("intValue");
          boolValueSetterF = MockData.class.getDeclaredField("boolValue");
          doubleValueSetterF = MockData.class.getDeclaredField("doubleValue");
          parentSetterF = MockData.class.getDeclaredField("parent");
          childrenSetterF = MockData.class.getDeclaredField("children");
        } catch (NoSuchFieldException e) {
          throw new IllegalStateException(e);
        }
      }
    }

    private static class Assignment3 {
      private static final Method intValueSetterM;
      private static final Method boolValueSetterM;
      private static final Method doubleValueSetterM;
      private static final Method parentSetterM;
      private static final Method childrenSetterM;

      static {
        try {
          intValueSetterM = MockData.class.getDeclaredMethod("setIntValue", int.class);
          boolValueSetterM = MockData.class.getDeclaredMethod("setBoolValue", boolean.class);
          doubleValueSetterM = MockData.class.getDeclaredMethod("setDoubleValue", double.class);
          parentSetterM = MockData.class.getDeclaredMethod("setParent", MockData.class);
          childrenSetterM = MockData.class.getDeclaredMethod("setChildren", List.class);
        } catch (Throwable e) {
          throw new IllegalStateException(e);
        }
      }
    }

    private static class Assignment4 {
      private static final BiConsumer<MockData, Object> intValueSetterFunc;
      private static final BiConsumer<MockData, Object> boolValueSetterFunc;
      private static final BiConsumer<MockData, Object> doubleValueSetterFunc;
      private static final BiConsumer<MockData, Object> parentSetterFunc;
      private static final BiConsumer<MockData, Object> childrenSetterFunc;

      private static void setObject(MockData data, String field, Object value) {
        int hash = field.hashCode();
        switch (hash) {
          case 556050114:
            intValueSetterFunc.accept(data, value);
            break;
          case 2044569767:
            boolValueSetterFunc.accept(data, value);
            break;
          case -1626611680:
            doubleValueSetterFunc.accept(data, value);
            break;
          case -995424086:
            parentSetterFunc.accept(data, value);
            break;
          case 1659526655:
            childrenSetterFunc.accept(data, value);
            break;
          default:
            break;
        }
      }

      static {
        try {
          MethodHandles.Lookup lookup = MethodHandles.lookup();
          intValueSetterFunc = makeCallSite(lookup, "setIntValue", int.class);
          boolValueSetterFunc = makeCallSite(lookup, "setBoolValue", boolean.class);
          doubleValueSetterFunc = makeCallSite(lookup, "setDoubleValue", double.class);
          parentSetterFunc = makeCallSite(lookup, "setParent", MockData.class);
          childrenSetterFunc = makeCallSite(lookup, "setChildren", List.class);
        } catch (Throwable e) {
          throw new IllegalStateException(e);
        }
      }

      @SuppressWarnings("unchecked")
      private static BiConsumer<MockData, Object> makeCallSite(
          MethodHandles.Lookup lookup, String setterName, Class<?> setterClass) throws Throwable {
        CallSite callsite =
            LambdaMetafactory.metafactory(
                lookup,
                "accept",
                MethodType.methodType(BiConsumer.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findVirtual(
                    MockData.class, setterName, MethodType.methodType(void.class, setterClass)),
                MethodType.methodType(void.class, MockData.class, setterClass));
        return (BiConsumer<MockData, Object>) callsite.getTarget().invokeExact();
      }
    }
  }
}
