package com.staticoyster.iotcattracker.controller;

import com.staticoyster.iotcattracker.service.CatDrinkWaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskIntervalController {

    private final CatDrinkWaterService catDrinkWaterService;
    private static final Logger logger = LoggerFactory.getLogger(TaskIntervalController.class);

    public TaskIntervalController(CatDrinkWaterService catDrinkWaterService) {
        this.catDrinkWaterService = catDrinkWaterService;
    }

    @PutMapping("/update-interval")
    public ResponseEntity<String> updateTaskInterval(@RequestParam long interval) {
        logger.debug(
                "Thread of updateTaskInterval() is {}", Thread.currentThread().isDaemon());
        try {
            if (interval <= 0) {
                return ResponseEntity.badRequest().body("Interval must be greater than 0.");
            }
            catDrinkWaterService.updateTaskInterval(interval);
            return ResponseEntity.ok("Task interval has been updated to " + interval + " milliseconds.");
        } catch (Exception exception) {
            logger.error("Interval is too large or unsupported: {}", interval);
            throw new IllegalArgumentException();
        }
    }
}
