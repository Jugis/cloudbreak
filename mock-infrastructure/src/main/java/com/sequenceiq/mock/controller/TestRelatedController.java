package com.sequenceiq.mock.controller;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sequenceiq.mock.legacy.clouderamanager.DefaultModelService;

@Controller
@RequestMapping("/tests")
public class TestRelatedController {

    @Inject
    private DefaultModelService defaultModelService;

    @GetMapping("/new")
    public void newTest() {
        defaultModelService.reinit();
    }
}
