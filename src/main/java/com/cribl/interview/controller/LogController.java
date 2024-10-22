package com.cribl.interview.controller;

import com.cribl.interview.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public List<String> getLogs(
            @RequestParam String filename,
            @RequestParam(required = false, defaultValue = "100") int limit,
            @RequestParam(required = false) String keyword) {

        return logService.getLogs(filename, limit, keyword);
    }
}

