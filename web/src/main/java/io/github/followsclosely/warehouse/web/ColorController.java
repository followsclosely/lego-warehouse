package io.github.followsclosely.warehouse.web;

import io.github.followsclosely.rebrickable.dto.RebrkColor;
import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.repository.LegoColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/colors")
@RequiredArgsConstructor
public class ColorController {

    private final LegoColorRepository legoColorRepository;

    @GetMapping(produces = "application/json")
    List<RebrkColor> getColors() {
        return List.of(
                RebrkColor.builder().id(1L).name("Red").rgb("FF0000").build(),
                RebrkColor.builder().id(2L).name("Green").rgb("00FF00").build(),
                RebrkColor.builder().id(3L).name("Blue").rgb("0000FF").build()
        );
    }

    @GetMapping(value = "/provider/{provider}/{id}", produces = "application/json")
    public LegoColor findByProviderAndProviderId(@PathVariable String provider, @PathVariable String id) {
        return legoColorRepository.findByProvider(provider, id);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Optional<LegoColor> findById(@PathVariable String id) {
        return legoColorRepository.findById(id);
    }
}