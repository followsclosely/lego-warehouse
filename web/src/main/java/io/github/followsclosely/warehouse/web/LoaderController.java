package io.github.followsclosely.warehouse.web;

import io.github.followsclosely.warehouse.loaders.LoaderGroup;
import io.github.followsclosely.warehouse.loaders.rebrickable.LoaderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loader")
@RequiredArgsConstructor
public class LoaderController {

    private final LoaderGroup loaderGroup;

    @GetMapping(value = "/load", produces = "application/json")
    LoaderContext loadAll() {
        loaderGroup.loadAllAsync(new LoaderContext());
        return loaderGroup.getLastContext();
    }

    @GetMapping(value = "/load-status", produces = "application/json")
    LoaderContext loadStatus() {
        return loaderGroup.getLastContext();
    }
}
