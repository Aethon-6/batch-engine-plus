package com.engine.single.controller;

import com.engine.single.service.ISingleService;
import com.engine.utils.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("single")
public class SingleController {
    private ISingleService singleService;

    @PostMapping("execute/{nodeId}")
    private R execute(@PathVariable("nodeId") String nodeId) {
        return singleService.execute(nodeId);
    }
}
