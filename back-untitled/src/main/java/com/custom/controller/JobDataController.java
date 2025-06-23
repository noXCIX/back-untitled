package com.custom.controller;

import com.custom.service.JobDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/job-data")
@RequiredArgsConstructor
public class JobDataController {
    private final JobDataService jobDataService;

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> getJobData(
            @RequestParam(required = false) Map<String, Object> inputOption
    ) {
        try {
            return ResponseEntity.ok(jobDataService.getJobData(inputOption));
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("status", "Failed");
            errorBody.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
}
