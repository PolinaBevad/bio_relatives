package util;

/**
 * Functional interface that represents some basic callable
 * function with zero or more input parameters.
 *
 * @param <V> Type of the output of the function.
 * @param <T> Type of the input arguments of the function.
 * @author Sergey Khvatov
 */
@FunctionalInterface
public interface CallableFunction<V, T> {
    @SuppressWarnings("unchecked")
    V call(T... params) throws Exception;
}
