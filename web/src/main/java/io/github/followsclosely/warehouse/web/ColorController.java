package io.github.followsclosely.warehouse.web;

import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.repository.LegoColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/colors")
@RequiredArgsConstructor
public class ColorController {

    private final LegoColorRepository legoColorRepository;

    @GetMapping(produces = "application/json")
    public Iterable<LegoColor> getColors() {
        return legoColorRepository.findAll();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Optional<LegoColor> findById(@PathVariable String id) {
        return legoColorRepository.findById(id);
    }

    @GetMapping(value = "/provider/{provider}/{id}", produces = "application/json")
    public LegoColor findByProviderAndProviderId(@PathVariable String provider, @PathVariable String id) {
        return legoColorRepository.findByProvider(provider, id);
    }
}