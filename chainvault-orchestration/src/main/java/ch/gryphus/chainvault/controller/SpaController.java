/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpaController {

    @GetMapping({"/", "/migration/**", "/dashboard"})
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping({"/migration/**", "/dashboard", "/overview"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }

    // Catch-all for other SPA routes, but EXCLUDE /api/*
    @GetMapping(value = "/**/{path:[^\\.]*}")
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}
