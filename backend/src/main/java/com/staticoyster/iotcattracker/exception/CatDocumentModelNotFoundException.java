package com.staticoyster.iotcattracker.exception;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CatDocumentModelNotFoundException extends RuntimeException {
    public CatDocumentModelNotFoundException(UUID catId) {
        super("The cat document model with id: " + catId + " is not found.");
    }

    public CatDocumentModelNotFoundException(String catName) {
        super("The cat document model with name: " + catName + " is not found.");
    }

    public CatDocumentModelNotFoundException(Set<UUID> catIds) {
        super("The cat document models with cats' ids: " + catIds + " are not found.");
    }

    public CatDocumentModelNotFoundException(List<String> catNames) {
        super("The cat document models with cats' names: " + catNames + " are not found.");
    }
}
