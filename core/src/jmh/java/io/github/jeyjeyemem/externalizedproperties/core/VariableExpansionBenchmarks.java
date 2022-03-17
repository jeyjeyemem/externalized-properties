package io.github.jeyjeyemem.externalizedproperties.core;

import io.github.jeyjeyemem.externalizedproperties.core.resolvers.MapResolver;
import io.github.jeyjeyemem.externalizedproperties.core.variableexpansion.BasicVariableExpander;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark {@link ExternalizedProperties}' variable expansion API.
 */
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public abstract class VariableExpansionBenchmarks {
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private VariableExpander variableExpander;

        @Setup
        public void setup() {
            Map<String, String> propertySource = Collections.singletonMap("test", "test");
    
            /**
             * Basic setup. No caching.
             */
            variableExpander = new BasicVariableExpander(new MapResolver(propertySource));
        }
    }

    /**
     * Benchmarks that measure average time.
     */
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static class VariableExpansionBenchmarksAvgt extends VariableExpansionBenchmarks {}

    /**
     * Benchmarks that measure throughput.
     */
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class VariableExpansionBenchmarksThrpt extends VariableExpansionBenchmarks {}

    /**
     * Multi-threaded benchmarks that measure average time.
     */
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Threads(Threads.MAX)
    public static class VariableExpansionBenchmarksAvgtMultiThreaded 
        extends VariableExpansionBenchmarks {}

    /**
     * Multi-threaded benchmarks that measure throughput.
     */
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(Threads.MAX)
    public static class VariableExpansionBenchmarksThrptMultiThreaded 
        extends VariableExpansionBenchmarks {}

    /**
     * Benchmark variable expansion.
     * 
     * @param state The benchmark state.
     * @return For you, blackhole.
     */
    @Benchmark
    public String variableExpansion(BenchmarkState state) {
        return state.variableExpander.expandVariables("${test}");
    }
}
