package org.apache.http.util.concurrent;

public interface Executor {
    void execute(Runnable runnable);
}
