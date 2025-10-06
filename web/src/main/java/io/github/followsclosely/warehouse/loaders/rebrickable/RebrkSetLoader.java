package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkSetCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkSetCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoSet;
import io.github.followsclosely.warehouse.entity.LegoTheme;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
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
@Order(30)
@Component
@RequiredArgsConstructor
public class RebrkSetLoader extends WarehouseLoader<LegoSet> {
    private final LegoSetRepository legoSetRepository;
    private final Javers javers = JaversBuilder.javers()
            .build();

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        RebrkSetCatalog catalog = new RebrkSetCatalogLoader();
        catalog.stream().forEach(rebrkSet -> {

            Optional<LegoSet> existing = legoSetRepository.findById(rebrkSet.getNumber());

            LegoSet entity = LegoSet.builder()
                    .id(rebrkSet.getNumber())
                    .name(rebrkSet.getName())
                    .releaseYear(rebrkSet.getYear())
                    .imageUrl(rebrkSet.getImageUrl())
                    .partCount(rebrkSet.getNumberOfParts())
                    .build();

            if (rebrkSet.getThemeId() != null) {
                LegoTheme theme = context.getThemeCache().get(String.valueOf(rebrkSet.getThemeId()));
                if (theme != null) {
                    entity.setLegoTheme(theme);
                }
            }

            postProcessing(existing, entity, entity::getId, job, javers, legoSetRepository::save);

        });
    }
}
