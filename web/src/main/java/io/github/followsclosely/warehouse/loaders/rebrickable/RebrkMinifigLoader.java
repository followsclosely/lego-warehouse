package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkMinifigCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkMinifigCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoMinifig;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoMinifigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Order(40)
@Component
@RequiredArgsConstructor
public class RebrkMinifigLoader extends WarehouseLoader<LegoMinifig> {
    private final LegoMinifigRepository legoMinifigRepository;
    private final Javers javers = JaversBuilder.javers().build();

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        RebrkMinifigCatalog catalog = new RebrkMinifigCatalogLoader();
        catalog.stream().forEach(rebrkMinifig -> {
            Optional<LegoMinifig> existing = legoMinifigRepository.findById(rebrkMinifig.getId());

            LegoMinifig entity = LegoMinifig.builder()
                    .id(rebrkMinifig.getId())
                    .name(rebrkMinifig.getName())
                    .partCount(rebrkMinifig.getParts())
                    .imageUrl(rebrkMinifig.getImageUrl())
                    .build();

            postProcessing(existing, entity, entity::getId, job, javers, legoMinifigRepository::save);
        });
    }
}
