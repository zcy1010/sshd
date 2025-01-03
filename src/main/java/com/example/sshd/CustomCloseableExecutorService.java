package com.example.sshd;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.sshd.common.future.CloseFuture;
import org.apache.sshd.common.future.SshFutureListener;
import org.apache.sshd.common.util.threads.CloseableExecutorService;

/**
 * @Author zcy
 * @Date 2024/12/26 20:45
 * @Version 1.0
 */
public class CustomCloseableExecutorService implements CloseableExecutorService {
    private final ExecutorService delegate;

    public CustomCloseableExecutorService(ExecutorService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return null;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return null;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException {
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
        TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void close() {
        shutdown();
    }

    @Override
    public CloseFuture close(boolean immediately) {
        if (immediately) {
            shutdownNow();
        } else {
            shutdown();
        }
        return null;
    }

    @Override
    public void addCloseFutureListener(SshFutureListener<CloseFuture> sshFutureListener) {

    }

    @Override
    public void removeCloseFutureListener(SshFutureListener<CloseFuture> sshFutureListener) {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    @Override
    public boolean awaitTermination(Duration timeout) throws InterruptedException {
        return awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    // Delegate other ExecutorService methods
    @Override
    public void execute(Runnable command) {
        delegate.execute(command);
    }

    // Add other methods as needed (submit, invokeAll, etc.)
}
