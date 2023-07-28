package dev.razorni.hcfactions.utils.commandapi.command;

@FunctionalInterface
public interface Processor<T, R> {
    R process(T var1);
}

