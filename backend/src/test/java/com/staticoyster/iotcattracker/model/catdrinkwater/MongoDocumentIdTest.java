package com.staticoyster.iotcattracker.model.catdrinkwater;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;

public class MongoDocumentIdTest {

    @Test
    public void processedSignalUsesSpringDataIdSoMongoUpsertsBySignalId() throws Exception {
        assertSpringDataId(ProcessedSignalModel.class);
    }

    @Test
    public void rawSignalUsesSpringDataIdSoMongoUpsertsBySignalId() throws Exception {
        assertSpringDataId(CatDrinkWaterSignalModel.class);
    }

    private static void assertSpringDataId(Class<?> documentType) throws Exception {
        Field signalId = documentType.getDeclaredField("signalId");
        Assertions.assertNotNull(
                signalId.getAnnotation(Id.class),
                documentType.getSimpleName() + " must use org.springframework.data.annotation.Id");
        Assertions.assertNull(
                signalId.getAnnotation(jakarta.persistence.Id.class),
                documentType.getSimpleName() + " must not use jakarta.persistence.Id");
    }
}
