package io.github.followsclosely.warehouse.loaders;

import io.github.followsclosely.warehouse.loaders.rebrickable.LoaderContext;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public abstract class WarehouseLoader<E> {
    public void load(LoaderContext context) throws IOException {
        LoaderContext.JobDetails job = context.newJob(this.getClass().getSimpleName());
        load(context, job);
        job.complete();
    }

    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
    }

    public void postProcessing(Optional<E> existing, E entity, Supplier<String> id, LoaderContext.JobDetails job, Javers javers, java.util.function.Consumer<E> saveFunction) {
        if (existing != null && existing.isPresent()) {
            Diff differences = javers.compare(existing.get(), entity);
            if (differences.hasChanges()) {
                log.info(differences.prettyPrint());
                job.logChange(id.get(), entity.getClass().getName(), differences.prettyPrint());
                job.counters.incrementUpdated();
                saveFunction.accept(entity);
            } else {
                log.info("No changes for: {}", entity);
                job.counters.incrementSkipped();
            }
        } else {
            log.info("Initial Entry: {}", entity);
            job.counters.incrementInserted();
            saveFunction.accept(entity);
        }
        job.counters.incrementProcessed();
    }
}
