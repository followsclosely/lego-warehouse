package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.RebrkColorClient;
import io.github.followsclosely.rebrickable.dto.RebrkColor;
import io.github.followsclosely.rebrickable.dto.RebrkResponse;
import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.entity.LegoColorProvider;
import io.github.followsclosely.warehouse.loaders.WarehouseLoader;
import io.github.followsclosely.warehouse.repository.LegoColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>
 * Loader component for fetching colors from Rebrickable and saving them to the local repository.
 * </p>
 * <p>
 * This class uses the RebrkColorClient to retrieve color data and maps it to the LegoColor entity,
 * which is then persisted using the LegoColorRepository. The RebrkColorCatalogLoader is not used here,
 * as we are directly interacting with the Rebrickable API through the client so that we get ExternalIds.
 * </p>
 */
@Slf4j
@Order(10)
@Component
@RequiredArgsConstructor
public class RebrkColorLoader extends WarehouseLoader<LegoColor> {

    private final RebrkColorClient rebrkColorClient;
    private final LegoColorRepository legoColorRepository;
    private final Javers javers = JaversBuilder.javers()
            .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
            .build();

    @Override
    public void load(LoaderContext context, LoaderContext.JobDetails job) {

        RebrkResponse<RebrkColor> response = rebrkColorClient.getColors(RebrkColorClient.Query.builder().pageSize(1000).build());
        response.getResults().forEach(color -> {

            Optional<LegoColor> existing = legoColorRepository.findById(String.valueOf(color.getId()));

            LegoColor entity = new LegoColor();
            entity.setId(color.getId().toString());
            entity.setName(color.getName());
            entity.setRgb(color.getRgb());
            entity.setTransparent(color.getTransparent());
            entity.getProviders().clear();
            color.getExternalIds().forEach((key, value) -> {
                for (int i = 0; i < value.getIds().length; i++) {
                    LegoColorProvider provider = LegoColorProvider.builder().provider(key)
                            .providerId(String.valueOf(value.getIds()[i]))
                            .description(String.join(", ", value.getNames()[i])).build();
                    entity.getProviders().add(provider);
                }
            });

            postProcessing(existing, entity, entity::getId, job, javers, legoColorRepository::save);

            context.getColorCache().put(entity.getId(), entity);
        });
    }


}
