package io.github.followsclosely.warehouse.web;

import io.github.followsclosely.rebrickable.dto.RebrkColor;
import io.github.followsclosely.warehouse.loaders.rebrickable.RebrkColorLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loader")
@RequiredArgsConstructor
public class LoaderController {

    private final RebrkColorLoader rebrkColorLoader;

    @GetMapping(value = "/colors", produces = "application/json")
    String getColors() {
        rebrkColorLoader.load();
        return "LoaderController: colors";
    }
}
