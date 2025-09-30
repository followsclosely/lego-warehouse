package io.github.followsclosely.warehouse.web;

import io.github.followsclosely.rebrickable.dto.RebrkColor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class ColorController {
    @GetMapping(value = "/colors", produces = "application/json")
    List<RebrkColor> getColors() {
        return List.of(
                RebrkColor.builder().id(1L).name("Red").rgb("FF0000").build(),
                RebrkColor.builder().id(2L).name("Green").rgb("00FF00").build(),
                RebrkColor.builder().id(3L).name("Blue").rgb("0000FF").build()
        );
    }
}
