package dev.razorni.core.util.command;

@FunctionalInterface
public interface Processor<T, R> {
    R process(T var1);
}

