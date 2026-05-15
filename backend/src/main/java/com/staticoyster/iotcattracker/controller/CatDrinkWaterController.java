package com.staticoyster.iotcattracker.controller;

import com.staticoyster.iotcattracker.dto.CatDrinkWaterSignal;
import com.staticoyster.iotcattracker.service.CatDrinkWaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatDrinkWaterController {

    private final CatDrinkWaterService catDrinkWaterService;
    private static final Logger logger = LoggerFactory.getLogger(CatDrinkWaterController.class);

    public CatDrinkWaterController(CatDrinkWaterService catDrinkWaterService) {
        this.catDrinkWaterService = catDrinkWaterService;
    }

    @PostMapping("/start-monitoring")
    public ResponseEntity<String> startMonitoring() {
        logger.debug("Thread of startMonitoring() is {}", Thread.currentThread().isDaemon());
        try {
            catDrinkWaterService.startMonitoring();
            return ResponseEntity.ok("Successfully to get Raspberry Pi started monitoring");
        } catch (Exception exception) {
            logger.error("Failed to get Raspberry Pi started monitoring: {}", exception.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/receive-signal")
    public ResponseEntity<String> receiveSignal(@RequestBody CatDrinkWaterSignal signal) {
        logger.debug("Thread of receiveSignal() is {}", Thread.currentThread().isDaemon());
        logger.info("signalId received: {}", signal.getSignalId());
        try {
            catDrinkWaterService.receiveSignal(signal);
        } catch (Exception exception) {
            logger.error("Failed to receive signal: {}", exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Signal received successfully!");
    }

    @PostMapping("/stop-monitoring")
    public ResponseEntity<String> stopMonitoring() {
        logger.debug("Thread of stopMonitoring() is {}", Thread.currentThread().isDaemon());
        try {
            catDrinkWaterService.stopMonitoring();
            return ResponseEntity.ok("Successfully to get Raspberry Pi stopped monitoring");
        } catch (Exception exception) {
            logger.error("Failed to get Raspberry Pi stopped monitoring: {}", exception.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
