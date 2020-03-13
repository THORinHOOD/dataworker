package com.thorinhood.dataworker.controllers;

import com.thorinhood.dataworker.loaders.Loader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loader")
public class LoaderController {

    private final Loader loader;

    public LoaderController(Loader loader) {
        this.loader = loader;
    }

    @GetMapping("/vk/start")
    public void start(@RequestParam List<String> ids,
                      @RequestParam int depth) {
        new Thread(() -> loader.load(ids, depth)).start();
    }

}
