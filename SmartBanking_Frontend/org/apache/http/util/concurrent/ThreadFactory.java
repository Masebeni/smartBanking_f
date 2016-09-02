package org.apache.http.util.concurrent;

public interface ThreadFactory {
    Thread newThread(Runnable runnable);
}
