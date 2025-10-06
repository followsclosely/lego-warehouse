package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.catalog.RebrkElementCatalog;
import io.github.followsclosely.rebrickable.catalog.RebrkElementCatalogLoader;
import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.entity.LegoElement;
import io.github.followsclosely.warehouse.entity.LegoPart;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoElementRepository;
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
@Order(70)
//@Component
@RequiredArgsConstructor
public class RebrkElementLoader extends WarehouseLoader<LegoElement> {
    private final LegoElementRepository legoElementRepository;
    private final LegoPartRepository legoPartRepository;
    private final Javers javers = JaversBuilder.javers().build();

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) throws IOException {
        RebrkElementCatalog catalog = new RebrkElementCatalogLoader();
        catalog.stream().forEach(rebrkElement -> {
            Optional<LegoPart> optionalLegoPart = legoPartRepository.findById(rebrkElement.getPartId());
            if (optionalLegoPart.isEmpty()) {
                log.error("LegoPart not found for element {}: partId={}", rebrkElement.getId(), rebrkElement.getPartId());
                return;
            }
            LegoPart part = optionalLegoPart.get();

            LegoColor color = context.getColorCache().get(String.valueOf(rebrkElement.getColorId()));
            if (color == null) {
                log.error("LegoColor not found for element {}: colorId={}", rebrkElement.getId(), rebrkElement.getColorId());
                return;
            }

            LegoElement entity = LegoElement.builder()
                    .id(rebrkElement.getId())
                    .legoPart(part)
                    .legoColor(color)
                    .design(rebrkElement.getDesignId())
                    .build();

            Optional<LegoElement> existing = legoElementRepository.findById(entity.getId());
            postProcessing(existing, entity, entity::getId, job, javers, legoElementRepository::save);
        });
    }
}
