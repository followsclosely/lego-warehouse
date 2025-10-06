package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkInventoryMinifigCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoInventory;
import io.github.followsclosely.warehouse.entity.LegoInventoryMinifig;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoInventoryRepository;
import io.github.followsclosely.warehouse.repository.LegoMinifigRepository;
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
@Order(85)
@Component
@RequiredArgsConstructor
public class RebrkInventoryMinifigLoader extends WarehouseLoader<LegoInventoryMinifig> {

    private final LegoMinifigRepository legoMinifigRepository;
    private final LegoInventoryRepository legoInventoryRepository;

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        Set<LegoInventoryMinifig> minifigs = new HashSet<>();
        AtomicReference<Boolean> dirty = new AtomicReference<>(Boolean.FALSE);
        AtomicReference<LegoInventory> inventory = new AtomicReference<>();

        new RebrkInventoryMinifigCatalogLoader().stream().forEach(rebrkInventory -> {
            log.info("Processing {}", rebrkInventory);
            String id = String.valueOf(rebrkInventory.getId());

            // If this is a new inventory or the first one, save the previous one (if exists) and load the new one
            if (inventory.get() == null || !Objects.equals(id, inventory.get().getId())) {
                if (inventory.get() != null) {
                    if (dirty.get()) {
                        legoInventoryRepository.save(inventory.get());
                        log.info("Saved inventory: {} with {} unique minifigs", inventory.get().getId(), inventory.get().getMinifigs().size());
                        dirty.set(Boolean.FALSE);
                    } else {
                        log.info("No changes for inventory: {}", inventory.get().getId());
                    }
                    minifigs.clear();
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

            LegoInventoryMinifig legoInventoryMinifig = LegoInventoryMinifig.builder()
                    .quantity(rebrkInventory.getQuantity())
                    .build();

            legoMinifigRepository.findById(rebrkInventory.getMinifigId()).ifPresentOrElse(legoInventoryMinifig::setLegoMinifig, () -> {
                log.error("Minifig not found: {}", rebrkInventory.getMinifigId());
            });

            // If present, update the quantity if changed, otherwise add the minifig to the inventory
            inventory.get().getMinifig(legoInventoryMinifig.getLegoMinifig().getId()).ifPresentOrElse(m -> {
                if (m.getQuantity() != legoInventoryMinifig.getQuantity()) {
                    m.setQuantity(legoInventoryMinifig.getQuantity());
                    job.getCounters().incrementUpdated();
                    dirty.set(Boolean.TRUE);
                } else {
                    job.getCounters().incrementSkipped();
                }
            }, () -> {
                inventory.get().getMinifigs().add(legoInventoryMinifig);
                job.getCounters().incrementInserted();
                dirty.set(Boolean.TRUE);
            });
            minifigs.add(legoInventoryMinifig);
            job.getCounters().incrementProcessed();
        });

        if (inventory.get() != null) {
            if (dirty.get()) {
                legoInventoryRepository.save(inventory.get());
                log.info("LAST: Saved inventory: {} with {} unique minifigs", inventory.get().getId(), inventory.get().getMinifigs().size());
            }
        }
        job.complete();
    }
}


