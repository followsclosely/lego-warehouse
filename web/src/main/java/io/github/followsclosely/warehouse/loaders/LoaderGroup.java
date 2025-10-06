package io.github.followsclosely.warehouse.loaders;

import io.github.followsclosely.warehouse.loaders.rebrickable.LoaderContext;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class LoaderGroup {

    private final List<WarehouseLoader<?>> loaders;

    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private LoaderContext lastContext = null;

    private volatile CompletableFuture<LoaderContext> currentLoadFuture = null;

    public LoaderGroup(List<WarehouseLoader<?>> loaders) {
        this.loaders = loaders;
    }

    /**
     * Starts loading asynchronously. Only one execution is allowed at a time. If a load is in progress,
     * throws an IllegalStateException. When the load completes, a new call is allowed.
     *
     * @throws IllegalStateException if a load is already in progress
     */
    public synchronized CompletableFuture<LoaderContext> loadAllAsync(@Nonnull LoaderContext context) {
        lock.lock();
        try {
            if (currentLoadFuture != null && !currentLoadFuture.isDone()) {
                throw new IllegalStateException("A load job is already running.");
            }
            this.lastContext = context;
            currentLoadFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    for (WarehouseLoader<?> loader : loaders) {
                        log.info("Starting loader: {}", loader.getClass().getSimpleName());
                        loader.load(this.lastContext);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return this.lastContext;
            });
            return currentLoadFuture;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Synchronous version: blocks until loading is complete and returns LoaderContext.
     * Only one execution is allowed at a time.
     */
    public LoaderContext loadAll(@Nonnull LoaderContext context) throws IOException {
        try {
            return loadAllAsync(context).join();
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) throw (IOException) cause;
            throw e;
        }
    }
}
