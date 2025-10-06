package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkInventoryCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkInventoryCatalogLoader;
import io.github.followsclosely.rebrickable.catalog.RebrkInventoryPartCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkInventoryPartCatalogLoader;
import io.github.followsclosely.rebrickable.dto.RebrkColor;
import io.github.followsclosely.warehouse.entity.*;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoInventoryRepository;
import io.github.followsclosely.warehouse.repository.LegoMinifigRepository;
import io.github.followsclosely.warehouse.repository.LegoPartRepository;
import io.github.followsclosely.warehouse.repository.LegoSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Order(80)
@Component
@RequiredArgsConstructor
public class RebrkInventoryPartLoader extends WarehouseLoader<LegoInventoryPart> {

    private final LegoPartRepository legoPartRepository;
    private final LegoInventoryRepository legoInventoryRepository;

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {

        Set<LegoInventoryPart> parts = new HashSet<>();
        AtomicReference<Boolean> dirty = new AtomicReference<>(Boolean.FALSE);
        AtomicReference<LegoInventory> inventory = new AtomicReference<>();

        new RebrkInventoryPartCatalogLoader().stream().forEach(rebrkInventory -> {

            log.info("Processing {}", rebrkInventory);
            String id = String.valueOf(rebrkInventory.getId());

            // If this is a new inventory or the first one, save the previous one (if exists) and load the new one
            if( inventory.get() == null || !Objects.equals(id, inventory.get().getId())){
                if( inventory.get() != null){
                    if ( dirty.get() ){
                        legoInventoryRepository.save(inventory.get());
                        log.info("Saved inventory: {} with {} unique parts", inventory.get().getId(), inventory.get().getParts().size());
                        dirty.set(Boolean.FALSE);
                    } else {
                        log.info("No changes for inventory: {}", inventory.get().getId());
                    }
                    parts.clear();
               }
                Optional<LegoInventory> o = legoInventoryRepository.findById(id);
                if ( o.isPresent() ){
                    inventory.set(o.get());
                } else {
                    inventory.set(null);
                    log.error("Cannot find inventory (id={})", id);
                    return;
                }
            }

            LegoInventoryPart legoInventoryPart = LegoInventoryPart.builder()
                    .spare(rebrkInventory.getIsSpare())
                    .quantity(rebrkInventory.getQuantity())
                    .build();

            legoPartRepository.findById(rebrkInventory.getPartId()).ifPresentOrElse(legoInventoryPart::setLegoPart, () -> {;
                log.error("Part not found: {}", rebrkInventory.getPartId());
            });

            LegoColor color = context.getColorCache().get(String.valueOf(rebrkInventory.getColorId()));
            if( color == null ) {
                log.error("Color not found: {}", rebrkInventory.getColorId());
            } else {
                legoInventoryPart.setLegoColor(color);
            }
            //If present, update the quantity and spare flag if changed, otherwise add the part to the inventory
            inventory.get().getPart(legoInventoryPart.getLegoPart().getId(), legoInventoryPart.getLegoColor().getId(), legoInventoryPart.isSpare()).ifPresentOrElse(p -> {
                if (p.getQuantity() != legoInventoryPart.getQuantity()){
                    p.setQuantity(legoInventoryPart.getQuantity());
                    job.getCounters().incrementUpdated();
                    dirty.set(Boolean.TRUE);
                } else {
                    job.getCounters().incrementSkipped();
                }
            }, () -> {;
                //Add the part to the inventory if not already present
                inventory.get().getParts().add(legoInventoryPart);
                job.getCounters().incrementInserted();
                dirty.set(Boolean.TRUE);
            });
            parts.add(legoInventoryPart);
            job.getCounters().incrementProcessed();
        });

        //Save the last inventory
        if( inventory.get() != null){
            //Save the parts for the previous inventory
            if ( dirty.get() ) {
                legoInventoryRepository.save(inventory.get());
                log.info("LAST: Saved inventory: {} with {} unique parts", inventory.get().getId(), inventory.get().getParts().size());
            }
        }

        job.complete();
    }
}
