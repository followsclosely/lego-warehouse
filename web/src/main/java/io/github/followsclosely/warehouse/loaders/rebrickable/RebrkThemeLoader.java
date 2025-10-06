package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkThemeCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkThemeCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoTheme;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Order(20)
@Component
@RequiredArgsConstructor
public class RebrkThemeLoader extends WarehouseLoader<LegoTheme> {

    private final LegoThemeRepository legoThemeRepository;
    private final Javers javers = JaversBuilder.javers()
            .build();


    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        RebrkThemeCatalog catalog = new RebrkThemeCatalogLoader();
        catalog.stream().forEach(rebrkTheme -> {
            Optional<LegoTheme> existing = legoThemeRepository.findById(String.valueOf(rebrkTheme.getId()));

            // Create LegoTheme entity from RebrkTheme
            LegoTheme entity = LegoTheme.builder().id(String.valueOf(rebrkTheme.getId())).name(rebrkTheme.getName()).build();
            // Link to parent if exists on the RebrkTheme
            if (rebrkTheme.getParentId() != null) {
                LegoTheme parent = context.getThemeCache().get(String.valueOf(rebrkTheme.getParentId()));
                if (parent != null) {
                    entity.setParent(parent);
                }
            }

            postProcessing(existing, entity, entity::getId, job, javers, legoThemeRepository::save);

            context.getThemeCache().put(entity.getId(), entity);
        });
    }
}
