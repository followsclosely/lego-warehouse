package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkInventoryCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkInventoryCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoInventory;
import io.github.followsclosely.warehouse.entity.LegoSet;
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
import java.util.Optional;

@Slf4j
@Order(80)
@Component
@RequiredArgsConstructor
public class RebrkInventoryLoader extends WarehouseLoader<LegoInventory> {
    private final LegoInventoryRepository legoInventoryRepository;
    private final LegoSetRepository legoSetRepository;
    private final LegoPartRepository legoPartRepository;
    private final LegoMinifigRepository legoMinifigRepository;
    private final Javers javers = JaversBuilder.javers().build();

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        RebrkInventoryCatalog catalog = new RebrkInventoryCatalogLoader();
        catalog.stream().forEach(rebrkInventory -> {
            Optional<LegoSet> optionalLegoSet = legoSetRepository.findById(String.valueOf(rebrkInventory.getSetId()));
            if (optionalLegoSet.isEmpty()) {
                log.warn("LegoSet not found for inventory {}: setId={}", rebrkInventory.getId(), rebrkInventory.getSetId());
                return;
            }
            LegoSet legoSet = optionalLegoSet.get();

            LegoInventory entity = LegoInventory.builder()
                    .id(String.valueOf(rebrkInventory.getId()))
                    .version(rebrkInventory.getVersion())
                    .legoSet(legoSet)
                    .build();

            Optional<LegoInventory> existing = legoInventoryRepository.findById(entity.getId());
            postProcessing(existing, entity, entity::getId, job, javers, legoInventoryRepository::save);
        });
    }
}
