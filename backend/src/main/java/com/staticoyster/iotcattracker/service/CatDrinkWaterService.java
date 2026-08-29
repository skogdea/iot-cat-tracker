package com.staticoyster.iotcattracker.service;

import com.staticoyster.iotcattracker.client.EmailClient;
import com.staticoyster.iotcattracker.dto.CatDrinkWaterSignal;
import com.staticoyster.iotcattracker.dto.EmailContent;
import com.staticoyster.iotcattracker.dto.ImmutableCatDrinkWaterSignal;
import com.staticoyster.iotcattracker.dto.ImmutableEmailContent;
import com.staticoyster.iotcattracker.exception.CatDrinkWaterSignalModelNotFoundException;
import com.staticoyster.iotcattracker.model.catdrinkwater.CatDrinkWaterSignalModel;
import com.staticoyster.iotcattracker.model.catdrinkwater.ProcessedSignalModel;
import com.staticoyster.iotcattracker.mongo.HasDrunkWaterRepository;
import com.staticoyster.iotcattracker.mongo.ProcessedSignalRepository;
import com.staticoyster.iotcattracker.mongo.RaspberryPiRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatDrinkWaterService {

    private final RaspberryPiRepository raspberryPiRepository;
    private final ProcessedSignalRepository processedSignalRepository;
    private final HasDrunkWaterRepository hasDrunkWaterRepository;
    private final EmailClient emailClient;
    private final RestTemplate restTemplate;
    private final String pirFlaskApiUrl;
    private final String senderEmail;
    private final String recipientEmail;
    private final String runMode;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private ProcessedSignalModel lastSignalModelProcessed;
    private CatDrinkWaterSignalModel firstSignalModel;
    private CatDrinkWaterSignal currentSignal;
    private ProcessedSignalModel currentDrunkWaterSignalModel;
    private boolean isDrinking = false;
    private final AtomicBoolean hasSentReport = new AtomicBoolean(false);
    private ScheduledFuture<?> task;

    @Value("${task.interval}")
    private long interval;

    private static final Logger logger = LoggerFactory.getLogger(CatDrinkWaterService.class);

    public CatDrinkWaterService(
            RaspberryPiRepository raspberryPiRepository,
            ProcessedSignalRepository processedSignalRepository,
            HasDrunkWaterRepository hasDrunkWaterRepository,
            EmailClient emailClient,
            RestTemplate restTemplate,
            @Value("${pir.flask.api.url}") String pirFlaskApiUrl,
            @Value("${mailgun.sender.email}") String senderEmail,
            @Value("${mailgun.recipient.email}") String recipientEmail,
            @Value("${app.run-mode:default}") String runMode) {
        this.raspberryPiRepository = raspberryPiRepository;
        this.processedSignalRepository = processedSignalRepository;
        this.hasDrunkWaterRepository = hasDrunkWaterRepository;
        this.emailClient = emailClient;
        this.restTemplate = restTemplate;
        this.pirFlaskApiUrl = pirFlaskApiUrl;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.runMode = runMode;
    }

    public void startMonitoring() {
        if (runMode.equals("deploy")) {
            logger.info("Running in deploy mode, Raspberry Pi starts immediately.");
        } else {
            //    to get raspberry pi started monitoring
            String url = pirFlaskApiUrl + "/start-monitoring";
            try {
                restTemplate.postForEntity(url, null, String.class);
                task = scheduler.scheduleAtFixedRate(this::checkIfCatHasDrunkWater, 0, interval, TimeUnit.MILLISECONDS);
                logger.info("Successfully scheduled task with interval: {}", interval);
                logger.debug("Scheduler is shut down. {}", scheduler.isShutdown());
            } catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
    }

    //    receive signal from raspberry pi and save it in mongodb
    public void receiveSignal(CatDrinkWaterSignal signal) {
        if (signal.getSignalId() != null && raspberryPiRepository.existsById(signal.getSignalId())) {
            logger.info("Ignoring duplicate raw signal {}", signal.getSignalId());
            return;
        }
        CatDrinkWaterSignalModel modelToBeSaved = convertToSignalModel(signal);
        raspberryPiRepository.save(modelToBeSaved);
    }

    //    to get raspberry pi stopped monitoring
    public void stopMonitoring() {
        String url = pirFlaskApiUrl + "/stop-monitoring";
        try {
            restTemplate.postForEntity(url, null, String.class);
            if (task != null && !task.isCancelled()) {
                task.cancel(true);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public void updateTaskInterval(long interval) {
        // User will have 4 options of interval: by a minute, 5 minutes, one hour and one day.
        this.interval = interval;
        stopMonitoring();
        startMonitoring();
    }

    //    get signals from Mongodb which haven't been processed
    private List<CatDrinkWaterSignal> getSignals() {
        List<CatDrinkWaterSignalModel> newSignalModels;

        //    Resume from Mongo after restart so already-processed signals are not saved again.
        if (lastSignalModelProcessed == null) {
            lastSignalModelProcessed = processedSignalRepository.findTopByOrderByTimeStampDesc();
            if (lastSignalModelProcessed != null) {
                isDrinking = restoreIsDrinking(lastSignalModelProcessed);
            }
        }

        //    First time run, there's no signal been processed, retrieve the first signal instead.
        if (lastSignalModelProcessed == null) {
            logger.warn("No signals found in the processed signal database. Retrieve the first signal received.");
            firstSignalModel = raspberryPiRepository.findTopByOrderByTimeStampAsc();
            long firstSignalTimeStamp = firstSignalModel.getTimeStamp();
            Instant firstSignalTime = Instant.ofEpochMilli(firstSignalTimeStamp);
            logger.info(
                    "First signal model retrieved: {}, its time: {}", firstSignalModel, formattedTime(firstSignalTime));
            newSignalModels = retrieveNewSignalModels(firstSignalTimeStamp);
        } else {
            long lastProcessedSignalTimeStamp = lastSignalModelProcessed.getTimeStamp();
            Instant lastProcessedSignalTime = Instant.ofEpochMilli(lastProcessedSignalTimeStamp);
            logger.info(
                    "Last signal model processed retrieved: {}, its time: {}",
                    lastSignalModelProcessed,
                    formattedTime(lastProcessedSignalTime));
            newSignalModels = retrieveNewSignalModels(lastProcessedSignalTimeStamp);
        }
        if (!newSignalModels.isEmpty()) {
            logger.info("Retrieve {} new signal models", newSignalModels.size());
            return newSignalModels.stream().map(this::convertToSignal).toList();
        }
        throw new CatDrinkWaterSignalModelNotFoundException(
                lastSignalModelProcessed == null
                        ? formattedTime(Instant.ofEpochMilli(firstSignalModel.getTimeStamp()))
                        : formattedTime(Instant.ofEpochMilli(lastSignalModelProcessed.getTimeStamp())));
    }

    private void checkIfCatHasDrunkWater() {
        logger.debug("checkIfCatHasDrunkWater() is executed at: {}", LocalDateTime.now());
        logger.debug(
                "Current thread {} processing checkIfCatHasDrunkWater() is Daemon: {}",
                Thread.currentThread().getName(),
                Thread.currentThread().isDaemon());
        logger.info("Last signal model processed is: {}", lastSignalModelProcessed);

        try {
            List<CatDrinkWaterSignal> sortedSignals = getSignals().stream()
                    .sorted(Comparator.comparing(CatDrinkWaterSignal::getTimeStamp))
                    .filter(signal -> !processedSignalRepository.existsById(signal.getSignalId()))
                    .toList();
            if (sortedSignals.isEmpty()) {
                return;
            }

            List<ProcessedSignalModel> newDrinkingEvents = new ArrayList<>();
            // Commit isDrinking only after saveAll so a failed write can still mark drunkWater on retry.
            boolean drinking = isDrinking;
            for (int i = 0; i < sortedSignals.size(); i++) {
                currentSignal = sortedSignals.get(i);
                long currentSignalTimeStamp = TimeUnit.MILLISECONDS.toSeconds(currentSignal.getTimeStamp());
                long previousSignalTimeStamp;

                if (lastSignalModelProcessed == null && i == 0) {
                    previousSignalTimeStamp = TimeUnit.MILLISECONDS.toSeconds(firstSignalModel.getTimeStamp());
                } else if (lastSignalModelProcessed != null && i == 0) {
                    previousSignalTimeStamp = TimeUnit.MILLISECONDS.toSeconds(lastSignalModelProcessed.getTimeStamp());
                } else {
                    previousSignalTimeStamp = TimeUnit.MILLISECONDS.toSeconds(
                            sortedSignals.get(i - 1).getTimeStamp());
                }

                long duration = currentSignalTimeStamp - previousSignalTimeStamp;
                drinking = processSignalDuration(duration, newDrinkingEvents, drinking);
            }

            Set<Long> newDrinkingSignalIds = newDrinkingEvents.stream()
                    .map(ProcessedSignalModel::getSignalId)
                    .collect(Collectors.toSet());
            List<ProcessedSignalModel> processedSignalModels = sortedSignals.stream()
                    .map(signal -> toProcessedSignalModel(signal, newDrinkingSignalIds))
                    .toList();
            List<ProcessedSignalModel> processedSignalModelsSaved =
                    processedSignalRepository.saveAll(processedSignalModels);
            logger.info(
                    "Saved {} processed signal models to the database in checkIfCatHasDrunkWater().",
                    processedSignalModelsSaved.size());

            lastSignalModelProcessed = processedSignalModelsSaved.get(processedSignalModelsSaved.size() - 1);
            isDrinking = drinking;
            notifyNewDrinkingEvents(newDrinkingEvents);
        } catch (Exception exception) {
            logger.warn("Warning or error in checkIfCatHasDrunkWater(): {}", exception.getMessage());
        }
    }

    private boolean processSignalDuration(
            long duration, List<ProcessedSignalModel> newDrinkingEvents, boolean drinking) {
        logger.debug(
                "Thread {} processes signal duration {}", Thread.currentThread().getName(), duration);
        try {
            // It's assumed that cat might have swagged, changed position or left if there are more than one signal
            // detected within 10 seconds:
            if (duration < 10) {
                logger.info("Your cat might have drunk water.");
                // It's assumed that cat has drunk water if it stays 10 seconds or longer and less than 5 minutes:
            } else if (duration < 300) {
                if (!drinking) {
                    logger.info("Your \uD83D\uDC08 has drunk water.");
                    ProcessedSignalModel currentSignalModel = convertToProcessedSignalModel(currentSignal);
                    currentSignalModel.setDrunkWater(true);
                    currentDrunkWaterSignalModel = currentSignalModel;
                    newDrinkingEvents.add(currentSignalModel);
                    drinking = true;
                    logger.debug(
                            "Thread {} sets isDrinking to true",
                            Thread.currentThread().getName());
                    Instant currentDrunkWaterTime = Instant.ofEpochMilli(currentSignalModel.getTimeStamp());
                    logger.info(
                            "Recording drinking event: {} in processSignalDuration(), its time: {}",
                            currentDrunkWaterSignalModel,
                            formattedTime(currentDrunkWaterTime));
                }
            }
            // It's assumed that cat has left long ago if there's been no signal for 5 minutes or longer:
            if (duration >= 300) {
                drinking = false;
                logger.info(
                        "Thread {} sets isDrinking to false",
                        Thread.currentThread().getName());
                logger.info("Your cat has left the water bowl long ago.");
            }
        } catch (Exception exception) {
            logger.error("Error in processSignalDuration(): {}", exception.getMessage());
        }
        return drinking;
    }

    private boolean restoreIsDrinking(ProcessedSignalModel lastProcessed) {
        if (lastProcessed.isDrunkWater()) {
            return true;
        }
        ProcessedSignalModel lastDrink = processedSignalRepository.findTopByDrunkWaterTrueOrderByTimeStampDesc();
        if (lastDrink == null || lastDrink.getTimeStamp() > lastProcessed.getTimeStamp()) {
            return false;
        }
        List<ProcessedSignalModel> visitSignals = processedSignalRepository.findByTimeStampBetweenOrderByTimeStampAsc(
                lastDrink.getTimeStamp(), lastProcessed.getTimeStamp());
        if (visitSignals.isEmpty()) {
            return false;
        }
        for (int i = 1; i < visitSignals.size(); i++) {
            long duration = TimeUnit.MILLISECONDS.toSeconds(visitSignals.get(i).getTimeStamp())
                    - TimeUnit.MILLISECONDS.toSeconds(visitSignals.get(i - 1).getTimeStamp());
            if (duration >= 300) {
                return false;
            }
        }
        return true;
    }

    private void notifyNewDrinkingEvents(List<ProcessedSignalModel> newDrinkingEvents) {
        for (ProcessedSignalModel event : newDrinkingEvents) {
            currentDrunkWaterSignalModel = event;
            try {
                if (interval == 60000 || interval == 300000) {
                    sendEmail();
                } else if (interval == 3600000 || interval == 86400000) {
                    scheduler.scheduleAtFixedRate(
                            () -> {
                                if (hasSentReport.compareAndSet(false, true)) {
                                    sendEmail();
                                    scheduler.scheduleAtFixedRate(
                                            () -> hasSentReport.set(false), 0, interval, TimeUnit.MILLISECONDS);
                                }
                            },
                            0,
                            interval,
                            TimeUnit.MILLISECONDS);
                    logger.info("It's scheduled to sendEmail and reset hasSentReport.");
                }
            } catch (Exception exception) {
                logger.error("Failed to send notification or summary report: {}", exception.getMessage());
            }
        }
    }

    private List<CatDrinkWaterSignalModel> retrieveNewSignalModels(long timeStamp) {
        return raspberryPiRepository.findByTimeStampAfter(timeStamp);
    }

    private void sendEmail() {
        logger.debug("Thread {} processes sendEmail()", Thread.currentThread().getName());
        try {
            // A notification email will be sent:
            if (interval == 60000 || interval == 300000) {
                emailClient.sendEmail(recipientEmail, senderEmail, buildNotification());
                // A summary report will be sent:
            } else if (interval == 3600000 || interval == 86400000) {
                emailClient.sendEmail(recipientEmail, senderEmail, buildSummaryReport());
            }
        } catch (Exception exception) {
            logger.error("Error in sendEmail(): {}", exception.getMessage());
        }
    }

    private EmailContent buildNotification() {
        logger.debug(
                "Thread {} processes buildNotification()",
                Thread.currentThread().getName());
        Instant currentDrunkWaterTime = Instant.ofEpochMilli(currentDrunkWaterSignalModel.getTimeStamp());
        return ImmutableEmailContent.builder()
                .subject("\uD83E\uDDE1\uD83D\uDC3ECat Tracker Alert!\uD83D\uDC3E\uD83E\uDDE1")
                .text("Your \uD83D\uDC08 has drunk water at " + formattedTime(currentDrunkWaterTime) + ".")
                .build();
    }

    private EmailContent buildSummaryReport() {
        logger.debug(
                "Thread {} processes buildSummaryReport()",
                Thread.currentThread().getName());
        List<ProcessedSignalModel> hasDrunkWaterSignalModels = getDrinkingEventsByInterval(interval);
        int hasDrunkWaterCounts = hasDrunkWaterSignalModels.size();
        String timeOfHasDrunkWater = hasDrunkWaterSignalModels.stream()
                .map(processedSignalModel -> Instant.ofEpochMilli(processedSignalModel.getTimeStamp()))
                .map(this::formattedTime)
                .collect(Collectors.joining(", "));
        return ImmutableEmailContent.builder()
                .subject("\uD83D\uDCEECat Tracker Summary Report\uD83D\uDC3E")
                .text("Your \uD83D\uDC08 has drunk water " + hasDrunkWaterCounts + " times, at " + timeOfHasDrunkWater
                        + ".")
                .build();
    }

    private List<ProcessedSignalModel> getDrinkingEventsByInterval(long interval) {
        logger.debug(
                "Thread {} processes getDrinkingEventsByInterval()",
                Thread.currentThread().getName());
        if (interval == 3600000) {
            return getHourlyDrinkingEvents();
        } else if (interval == 86400000) {
            return getDailyDrinkingEvents();
        } else {
            throw new IllegalArgumentException("Unsupported interval: " + interval);
        }
    }

    private List<ProcessedSignalModel> getHourlyDrinkingEvents() {
        logger.debug(
                "Thread {} is processing getHourlyDrinkingEvents()",
                Thread.currentThread().getName());
        LocalDateTime start = LocalDateTime.now().minusHours(1).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusHours(1);
        logger.info("Querying hourly drinking events between {} and {}", start, end);

        long startMillis = start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endMillis = end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // HasDrunkWaterRepository and ProcessedSignalRepository shares the same collection, ProcessedSignalModel,
        // it has to be filtered by 'drunkWater: true':
        List<ProcessedSignalModel> results =
                hasDrunkWaterRepository.findByTimeStampBetween(startMillis, endMillis).stream()
                        .filter(ProcessedSignalModel::isDrunkWater)
                        .toList();
        logger.info("Found {} drinking events in an hour: {}", results.size(), results);
        return results;
    }

    private List<ProcessedSignalModel> getDailyDrinkingEvents() {
        LocalDateTime start = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().atStartOfDay();
        logger.info("Querying daily drinking events between {} and {}", start, end);

        long startMillis = start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endMillis = end.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<ProcessedSignalModel> results =
                hasDrunkWaterRepository.findByTimeStampBetween(startMillis, endMillis).stream()
                        .filter(ProcessedSignalModel::isDrunkWater)
                        .toList();
        logger.info("Found {} drinking events in a day: {}", results.size(), results);
        return results;
    }

    private String formattedTime(Instant instant) {
        logger.debug("System Locale is : {}", Locale.getDefault());
        return DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss VV yyyy")
                .withLocale(Locale.UK) // Force English for Weekdays/Months
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    public CatDrinkWaterSignalModel convertToSignalModel(CatDrinkWaterSignal signal) {
        return CatDrinkWaterSignalModel.Builder.newBuilder()
                .withSignalId(signal.getSignalId())
                .withSensorId(signal.getSensorId())
                .withLocation(signal.getLocation())
                .withTimeStamp(signal.getTimeStamp())
                .build();
    }

    public CatDrinkWaterSignal convertToSignal(CatDrinkWaterSignalModel signalModel) {
        return ImmutableCatDrinkWaterSignal.builder()
                .signalId(signalModel.getSignalId())
                .sensorId(signalModel.getSensorId())
                .location(signalModel.getLocation())
                .timeStamp(signalModel.getTimeStamp())
                .build();
    }

    public ProcessedSignalModel convertToProcessedSignalModel(CatDrinkWaterSignal signal) {
        return ProcessedSignalModel.Builder.newBuilder()
                .withSignalId(signal.getSignalId())
                .withTimeStamp(signal.getTimeStamp())
                .build();
    }

    private ProcessedSignalModel toProcessedSignalModel(CatDrinkWaterSignal signal, Set<Long> newDrinkingSignalIds) {
        ProcessedSignalModel model = convertToProcessedSignalModel(signal);
        if (newDrinkingSignalIds.contains(signal.getSignalId())) {
            model.setDrunkWater(true);
        }
        return model;
    }
}
