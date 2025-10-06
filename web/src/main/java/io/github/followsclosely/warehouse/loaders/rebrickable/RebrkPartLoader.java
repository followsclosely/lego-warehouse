package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkPartCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkPartCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoCategory;
import io.github.followsclosely.warehouse.entity.LegoPart;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoPartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Order(60)
//@Component
@RequiredArgsConstructor
public class RebrkPartLoader extends WarehouseLoader<LegoPart> {
    private final LegoPartRepository legoPartRepository;
    private final Javers javers = JaversBuilder.javers().build();

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        RebrkPartCatalog catalog = new RebrkPartCatalogLoader();
        catalog.stream().forEach(rebrkPart -> {
            Optional<LegoPart> existing = legoPartRepository.findById(rebrkPart.getId());

            LegoPart.LegoPartBuilder builder = LegoPart.builder()
                    .id(rebrkPart.getId())
                    .name(rebrkPart.getName())
                    .material(rebrkPart.getMaterial());

            if (rebrkPart.getCategoryId() != null) {
                LegoCategory category = context.getCategoryCache().get(String.valueOf(rebrkPart.getCategoryId()));
                if (category != null) {
                    builder.legoCategory(category);
                }
            }

            LegoPart entity = builder.build();
            postProcessing(existing, entity, entity::getId, job, javers, legoPartRepository::save);
        });
    }
}

