package io.github.followsclosely.warehouse.loaders.rebrickable;

import io.github.followsclosely.rebrickable.RebrkColorClient;
import io.github.followsclosely.rebrickable.dto.RebrkColor;
import io.github.followsclosely.rebrickable.dto.RebrkResponse;
import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.repository.LegoColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RebrkColorLoader {

    private final RebrkColorClient rebrkColorClient;
    private final LegoColorRepository legoColorRepository;

    public void load(){
        RebrkResponse<RebrkColor> response = rebrkColorClient.getColors();
        response.getResults().forEach(color -> {
            LegoColor entity = new LegoColor();
            entity.setId(color.getId().toString());
            entity.setName(color.getName());
            entity.setRgb(color.getRgb());
            entity.setTransparent(color.getTransparent());

            System.out.println("Loading color: " + entity);
            legoColorRepository.update(entity);
        });
    }

}
