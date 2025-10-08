package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkInventorySetCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoInventory;
import io.github.followsclosely.warehouse.entity.LegoInventorySet;
import io.github.followsclosely.warehouse.entity.LegoSet;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoInventoryRepository;
import io.github.followsclosely.warehouse.repository.LegoSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Order(90)
@Component
@RequiredArgsConstructor
public class RebrkInventorySetLoader extends WarehouseLoader<LegoInventorySet> {

    private final LegoSetRepository legoSetRepository;
    private final LegoInventoryRepository legoInventoryRepository;

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        Set<LegoInventorySet> sets = new HashSet<>();
        AtomicReference<Boolean> dirty = new AtomicReference<>(Boolean.FALSE);
        AtomicReference<LegoInventory> inventory = new AtomicReference<>();

        // TODO: Replace with actual catalog loader for inventory sets
        new RebrkInventorySetCatalogLoader().stream().forEach(rebrkInventory -> {
            log.info("Processing {}", rebrkInventory);
            String id = String.valueOf(rebrkInventory.getId());

            if (inventory.get() == null || !Objects.equals(id, inventory.get().getId())) {
                if (inventory.get() != null) {
                    if (dirty.get()) {
                        legoInventoryRepository.save(inventory.get());
                        log.info("Saved inventory: {} with {} unique sets", inventory.get().getId(), inventory.get().getSets().size());
                        dirty.set(Boolean.FALSE);
                    } else {
                        log.info("No changes for inventory: {}", inventory.get().getId());
                    }
                    sets.clear();
                }
                Optional<LegoInventory> o = legoInventoryRepository.findById(id);
                if (o.isPresent()) {
                    inventory.set(o.get());
                } else {
                    inventory.set(null);
                    log.error("Cannot find inventory (id={})", id);
                    return;
                }
            }

            LegoInventorySet legoInventorySet = LegoInventorySet.builder()
                    .quantity(rebrkInventory.getQuantity())
                    .build();

            legoSetRepository.findById(rebrkInventory.getSetId()).ifPresentOrElse(legoInventorySet::setLegoSet, () -> {
                log.error("Set not found: {}", rebrkInventory.getSetId());
            });

            // If present, update the quantity if changed, otherwise add the set to the inventory
            inventory.get().getSets().stream()
                .filter(s -> s.getLegoSet().getId().equals(legoInventorySet.getLegoSet().getId()))
                .findFirst()
                .ifPresentOrElse(s -> {
                    if (s.getQuantity() != legoInventorySet.getQuantity()) {
                        s.setQuantity(legoInventorySet.getQuantity());
                        job.getCounters().incrementUpdated();
                        dirty.set(Boolean.TRUE);
                    } else {
                        job.getCounters().incrementSkipped();
                    }
                }, () -> {
                    inventory.get().getSets().add(legoInventorySet);
                    job.getCounters().incrementInserted();
                    dirty.set(Boolean.TRUE);
                });
            sets.add(legoInventorySet);
            job.getCounters().incrementProcessed();
        });

        if (inventory.get() != null) {
            if (dirty.get()) {
                legoInventoryRepository.save(inventory.get());
                log.info("LAST: Saved inventory: {} with {} unique sets", inventory.get().getId(), inventory.get().getSets().size());
            }
        }
        job.complete();
    }
}

