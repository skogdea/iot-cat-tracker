package com.staticoyster.iotcattracker.service;

import com.staticoyster.iotcattracker.client.EmailClient;
import com.staticoyster.iotcattracker.dto.CatDrinkWaterSignal;
import com.staticoyster.iotcattracker.dto.ImmutableCatDrinkWaterSignal;
import com.staticoyster.iotcattracker.model.catdrinkwater.CatDrinkWaterSignalModel;
import com.staticoyster.iotcattracker.model.catdrinkwater.ProcessedSignalModel;
import com.staticoyster.iotcattracker.mongo.HasDrunkWaterRepository;
import com.staticoyster.iotcattracker.mongo.ProcessedSignalRepository;
import com.staticoyster.iotcattracker.mongo.RaspberryPiRepository;
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
    }

    @Test
    public void failedSaveAllDoesNotDropDrunkWaterOnRetry() throws Exception {
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
    }

    @Test
    public void alreadyProcessedSignalsAreNotSavedAgain() throws Exception {
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

    private static CatDrinkWaterSignal rawSignal(long signalId, long timeStamp) {
        return ImmutableCatDrinkWaterSignal.builder()
                .signalId(signalId)
                .sensorId("1")
                .location("bedroom")
                .timeStamp(timeStamp)
                .build();
    }

    private static CatDrinkWaterSignalModel firstRawSignal() {
        return CatDrinkWaterSignalModel.Builder.newBuilder()
                .withSignalId(FIRST_SIGNAL_ID)
                .withSensorId("1")
                .withLocation("bedroom")
                .withTimeStamp(0L)
                .build();
    }

    private static CatDrinkWaterSignalModel drinkingRawSignal() {
        return CatDrinkWaterSignalModel.Builder.newBuilder()
                .withSignalId(DRINKING_SIGNAL_ID)
                .withSensorId("1")
                .withLocation("bedroom")
                .withTimeStamp(seconds(15))
                .build();
    }

    private static long seconds(long seconds) {
        return TimeUnit.SECONDS.toMillis(seconds);
    }
}
