package com.staticoyster.iotcattracker.service;

import com.staticoyster.iotcattracker.client.EmailClient;
import com.staticoyster.iotcattracker.dto.CatDrinkWaterSignal;
import com.staticoyster.iotcattracker.dto.ImmutableCatDrinkWaterSignal;
import com.staticoyster.iotcattracker.model.catdrinkwater.CatDrinkWaterSignalModel;
import com.staticoyster.iotcattracker.model.catdrinkwater.ProcessedSignalModel;
import com.staticoyster.iotcattracker.mongo.HasDrunkWaterRepository;
import com.staticoyster.iotcattracker.mongo.ProcessedSignalRepository;
import com.staticoyster.iotcattracker.mongo.RaspberryPiRepository;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class CatDrinkWaterServiceDuplicatePersistenceTest {

    private static final long FIRST_SIGNAL_ID = 1L;
    private static final long DRINKING_SIGNAL_ID = 4972210839327032968L;
    private static final long FOLLOW_UP_SIGNAL_ID = 2L;
    private static final long NEXT_SIGNAL_ID = 3L;
    private static final long MINUTE_INTERVAL = 60_000L;

    @Mock
    private RaspberryPiRepository raspberryPiRepository;

    @Mock
    private ProcessedSignalRepository processedSignalRepository;

    @Mock
    private HasDrunkWaterRepository hasDrunkWaterRepository;

    @Mock
    private EmailClient emailClient;

    @Mock
    private RestTemplate restTemplate;

    private CatDrinkWaterService service;

    @BeforeEach
    public void before() {
        service = new CatDrinkWaterService(
                raspberryPiRepository,
                processedSignalRepository,
                hasDrunkWaterRepository,
                emailClient,
                restTemplate,
                "http://pi:5000",
                "sender@example.com",
                "recipient@example.com",
                "default");
    }

    @Test
    public void receiveSignalDoesNotPersistTheSameRawSignalTwice() {
        CatDrinkWaterSignal signal = rawSignal(DRINKING_SIGNAL_ID, seconds(15));
        Mockito.when(raspberryPiRepository.existsById(DRINKING_SIGNAL_ID))
                .thenReturn(false)
                .thenReturn(true);

        service.receiveSignal(signal);
        service.receiveSignal(signal);

        Mockito.verify(raspberryPiRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void processingADrinkingVisitSavesEachSignalOnceWithDrunkWaterPreserved() throws Exception {
        setInterval(MINUTE_INTERVAL);
        Mockito.when(processedSignalRepository.findTopByOrderByTimeStampDesc()).thenReturn(null);
        Mockito.when(raspberryPiRepository.findTopByOrderByTimeStampAsc()).thenReturn(firstRawSignal());
        Mockito.when(raspberryPiRepository.findByTimeStampAfter(0L)).thenReturn(List.of(drinkingRawSignal()));
        Mockito.when(processedSignalRepository.existsById(DRINKING_SIGNAL_ID)).thenReturn(false);
        Mockito.when(processedSignalRepository.saveAll(Mockito.anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        invokeCheckIfCatHasDrunkWater();

        List<ProcessedSignalModel> saved = savedProcessedSignals();
        Assertions.assertEquals(1, saved.size());
        Assertions.assertEquals(DRINKING_SIGNAL_ID, saved.get(0).getSignalId());
        Assertions.assertTrue(saved.get(0).isDrunkWater());
        Mockito.verify(hasDrunkWaterRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(processedSignalRepository, Mockito.times(1)).saveAll(Mockito.anyList());
        Mockito.verify(emailClient, Mockito.times(1))
                .sendEmail(Mockito.eq("recipient@example.com"), Mockito.eq("sender@example.com"), Mockito.any());
    }

    @Test
    public void failedSaveAllDoesNotDropDrunkWaterOnRetry() throws Exception {
        setInterval(MINUTE_INTERVAL);
        Mockito.when(processedSignalRepository.findTopByOrderByTimeStampDesc()).thenReturn(null);
        Mockito.when(raspberryPiRepository.findTopByOrderByTimeStampAsc()).thenReturn(firstRawSignal());
        Mockito.when(raspberryPiRepository.findByTimeStampAfter(0L)).thenReturn(List.of(drinkingRawSignal()));
        Mockito.when(processedSignalRepository.existsById(DRINKING_SIGNAL_ID)).thenReturn(false);
        Mockito.when(processedSignalRepository.saveAll(Mockito.anyList()))
                .thenThrow(new RuntimeException("mongo unavailable"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        invokeCheckIfCatHasDrunkWater();
        invokeCheckIfCatHasDrunkWater();

        ArgumentCaptor<List<ProcessedSignalModel>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(processedSignalRepository, Mockito.times(2)).saveAll(captor.capture());
        List<ProcessedSignalModel> retriedSave = captor.getAllValues().get(1);
        Assertions.assertEquals(1, retriedSave.size());
        Assertions.assertEquals(DRINKING_SIGNAL_ID, retriedSave.get(0).getSignalId());
        Assertions.assertTrue(retriedSave.get(0).isDrunkWater());
        Mockito.verify(emailClient, Mockito.times(1))
                .sendEmail(Mockito.eq("recipient@example.com"), Mockito.eq("sender@example.com"), Mockito.any());
    }

    @Test
    public void alreadyProcessedSignalsAreNotSavedAgain() throws Exception {
        setInterval(MINUTE_INTERVAL);
        Mockito.when(processedSignalRepository.findTopByOrderByTimeStampDesc()).thenReturn(null);
        Mockito.when(raspberryPiRepository.findTopByOrderByTimeStampAsc()).thenReturn(firstRawSignal());
        Mockito.when(raspberryPiRepository.findByTimeStampAfter(Mockito.anyLong()))
                .thenReturn(List.of(drinkingRawSignal()));
        Mockito.when(processedSignalRepository.existsById(DRINKING_SIGNAL_ID))
                .thenReturn(false)
                .thenReturn(true);
        Mockito.when(processedSignalRepository.saveAll(Mockito.anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        invokeCheckIfCatHasDrunkWater();
        invokeCheckIfCatHasDrunkWater();

        Mockito.verify(processedSignalRepository, Mockito.times(1)).saveAll(Mockito.anyList());
        Mockito.verify(hasDrunkWaterRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(emailClient, Mockito.times(1))
                .sendEmail(Mockito.eq("recipient@example.com"), Mockito.eq("sender@example.com"), Mockito.any());
    }

    @Test
    public void resumeDoesNotTreatAContinuingDrinkingVisitAsNew() throws Exception {
        setInterval(MINUTE_INTERVAL);
        ProcessedSignalModel lastDrink = processed(DRINKING_SIGNAL_ID, seconds(15), true);
        Mockito.when(processedSignalRepository.findTopByOrderByTimeStampDesc()).thenReturn(lastDrink);
        Mockito.when(raspberryPiRepository.findByTimeStampAfter(seconds(15)))
                .thenReturn(List.of(rawModel(NEXT_SIGNAL_ID, seconds(30))));
        Mockito.when(processedSignalRepository.existsById(NEXT_SIGNAL_ID)).thenReturn(false);
        Mockito.when(processedSignalRepository.saveAll(Mockito.anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        invokeCheckIfCatHasDrunkWater();

        List<ProcessedSignalModel> saved = savedProcessedSignals();
        Assertions.assertEquals(1, saved.size());
        Assertions.assertEquals(NEXT_SIGNAL_ID, saved.get(0).getSignalId());
        Assertions.assertFalse(saved.get(0).isDrunkWater());
        Mockito.verify(emailClient, Mockito.never()).sendEmail(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void resumeDoesNotTreatFollowUpSignalsInTheSameVisitAsNew() throws Exception {
        setInterval(MINUTE_INTERVAL);
        ProcessedSignalModel lastDrink = processed(DRINKING_SIGNAL_ID, seconds(15), true);
        ProcessedSignalModel lastFollowUp = processed(FOLLOW_UP_SIGNAL_ID, seconds(20), false);
        Mockito.when(processedSignalRepository.findTopByOrderByTimeStampDesc()).thenReturn(lastFollowUp);
        Mockito.when(processedSignalRepository.findTopByDrunkWaterTrueOrderByTimeStampDesc())
                .thenReturn(lastDrink);
        Mockito.when(processedSignalRepository.findByTimeStampBetweenOrderByTimeStampAsc(seconds(15), seconds(20)))
                .thenReturn(List.of(lastDrink, lastFollowUp));
        Mockito.when(raspberryPiRepository.findByTimeStampAfter(seconds(20)))
                .thenReturn(List.of(rawModel(NEXT_SIGNAL_ID, seconds(40))));
        Mockito.when(processedSignalRepository.existsById(NEXT_SIGNAL_ID)).thenReturn(false);
        Mockito.when(processedSignalRepository.saveAll(Mockito.anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        invokeCheckIfCatHasDrunkWater();

        List<ProcessedSignalModel> saved = savedProcessedSignals();
        Assertions.assertEquals(1, saved.size());
        Assertions.assertFalse(saved.get(0).isDrunkWater());
        Mockito.verify(emailClient, Mockito.never()).sendEmail(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void resumeStillDetectsANewVisitAfterTheCatHasLeft() throws Exception {
        setInterval(MINUTE_INTERVAL);
        ProcessedSignalModel lastDrink = processed(DRINKING_SIGNAL_ID, seconds(15), true);
        ProcessedSignalModel lastAfterLeave = processed(FOLLOW_UP_SIGNAL_ID, seconds(400), false);
        Mockito.when(processedSignalRepository.findTopByOrderByTimeStampDesc()).thenReturn(lastAfterLeave);
        Mockito.when(processedSignalRepository.findTopByDrunkWaterTrueOrderByTimeStampDesc())
                .thenReturn(lastDrink);
        Mockito.when(processedSignalRepository.findByTimeStampBetweenOrderByTimeStampAsc(seconds(15), seconds(400)))
                .thenReturn(List.of(lastDrink, lastAfterLeave));
        Mockito.when(raspberryPiRepository.findByTimeStampAfter(seconds(400)))
                .thenReturn(List.of(rawModel(NEXT_SIGNAL_ID, seconds(415))));
        Mockito.when(processedSignalRepository.existsById(NEXT_SIGNAL_ID)).thenReturn(false);
        Mockito.when(processedSignalRepository.saveAll(Mockito.anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        invokeCheckIfCatHasDrunkWater();

        List<ProcessedSignalModel> saved = savedProcessedSignals();
        Assertions.assertEquals(1, saved.size());
        Assertions.assertTrue(saved.get(0).isDrunkWater());
        Mockito.verify(emailClient, Mockito.times(1))
                .sendEmail(Mockito.eq("recipient@example.com"), Mockito.eq("sender@example.com"), Mockito.any());
    }

    @SuppressWarnings("unchecked")
    private List<ProcessedSignalModel> savedProcessedSignals() {
        ArgumentCaptor<List<ProcessedSignalModel>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(processedSignalRepository).saveAll(captor.capture());
        return captor.getValue();
    }

    private void invokeCheckIfCatHasDrunkWater() throws Exception {
        Method method = CatDrinkWaterService.class.getDeclaredMethod("checkIfCatHasDrunkWater");
        method.setAccessible(true);
        method.invoke(service);
    }

    private void setInterval(long millis) throws Exception {
        Field field = CatDrinkWaterService.class.getDeclaredField("interval");
        field.setAccessible(true);
        field.setLong(service, millis);
    }

    private static CatDrinkWaterSignal rawSignal(long signalId, long timeStamp) {
        return ImmutableCatDrinkWaterSignal.builder()
                .signalId(signalId)
                .sensorId("1")
                .location("bedroom")
                .timeStamp(timeStamp)
                .build();
    }

    private static CatDrinkWaterSignalModel firstRawSignal() {
        return rawModel(FIRST_SIGNAL_ID, 0L);
    }

    private static CatDrinkWaterSignalModel drinkingRawSignal() {
        return rawModel(DRINKING_SIGNAL_ID, seconds(15));
    }

    private static CatDrinkWaterSignalModel rawModel(long signalId, long timeStamp) {
        return CatDrinkWaterSignalModel.Builder.newBuilder()
                .withSignalId(signalId)
                .withSensorId("1")
                .withLocation("bedroom")
                .withTimeStamp(timeStamp)
                .build();
    }

    private static ProcessedSignalModel processed(long signalId, long timeStamp, boolean drunkWater) {
        return ProcessedSignalModel.Builder.newBuilder()
                .withSignalId(signalId)
                .withTimeStamp(timeStamp)
                .withDrunkWater(drunkWater)
                .build();
    }

    private static long seconds(long seconds) {
        return TimeUnit.SECONDS.toMillis(seconds);
    }
}
