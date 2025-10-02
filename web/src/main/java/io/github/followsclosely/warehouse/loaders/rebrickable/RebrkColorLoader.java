package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.RebrkColorClient;
import io.github.followsclosely.rebrickable.dto.RebrkColor;
import io.github.followsclosely.rebrickable.dto.RebrkResponse;
import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.entity.LegoColorProvider;
import io.github.followsclosely.warehouse.repository.LegoColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RebrkColorLoader {

    private final RebrkColorClient rebrkColorClient;
    private final LegoColorRepository legoColorRepository;

    public void load() {
        RebrkResponse<RebrkColor> response = rebrkColorClient.getColors();
        response.getResults().forEach(color -> {
            LegoColor entity = new LegoColor();
            entity.setId(color.getId().toString());
            entity.setName(color.getName());
            entity.setRgb(color.getRgb());
            entity.setTransparent(color.getTransparent());

            color.getExternalIds().forEach((key, value) -> {
                for (int i = 0; i < value.getIds().length; i++) {
                    LegoColorProvider provider = new LegoColorProvider();
                    provider.setProvider(key);
                    provider.setProviderId(String.valueOf(value.getIds()[i]));
                    provider.setDescription(String.join(", ", value.getNames()[i]));
                    entity.getProviders().add(provider);
                }
            });

            //System.out.println("Loading color: " + entity.getName());
            legoColorRepository.save(entity);
        });
    }

}
