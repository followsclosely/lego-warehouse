package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkCategoryCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkCategoryCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoCategory;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Order(50)
@Component
@RequiredArgsConstructor
public class RebrkCategoryLoader extends WarehouseLoader<LegoCategory> {
    private final LegoCategoryRepository legoCategoryRepository;
    private final Javers javers = JaversBuilder.javers().build();

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        RebrkCategoryCatalog catalog = new RebrkCategoryCatalogLoader();
        catalog.stream().forEach(rebrkCategory -> {
            Optional<LegoCategory> existing = legoCategoryRepository.findById(String.valueOf(rebrkCategory.getId()));

            LegoCategory entity = LegoCategory.builder()
                    .id(String.valueOf(rebrkCategory.getId()))
                    .name(rebrkCategory.getName())
                    .build();

            postProcessing(existing, entity, entity::getId, job, javers, legoCategoryRepository::save);

            context.getCategoryCache().put(entity.getId(), entity);
        });
    }
}
