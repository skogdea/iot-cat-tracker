package com.staticoyster.iotcattracker.controller;

import com.staticoyster.iotcattracker.dto.CatDocument;
import com.staticoyster.iotcattracker.exception.CatDocumentModelNotFoundException;
import com.staticoyster.iotcattracker.service.CatDocumentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cat-document")
public class CatDocumentController {
    private final CatDocumentService catDocumentService;

    public CatDocumentController(CatDocumentService catDocumentService) {
        this.catDocumentService = catDocumentService;
    }

    @PostMapping("/create")
    public ResponseEntity<CatDocument> createCatDocument(@Valid @RequestBody CatDocument catDocument) {
        CatDocument catDocumentCreated = catDocumentService.createCatDocument(catDocument);
        if (catDocumentCreated != null) {
            return new ResponseEntity<>(catDocumentCreated, HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/by-id/{catId}")
    public ResponseEntity<CatDocument> getCatDocumentById(@Valid @PathVariable("catId") UUID catId) {
        CatDocument catDocumentRequested = catDocumentService.getCatDocumentById(catId);
        if (catDocumentRequested == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(catDocumentRequested);
    }

    @GetMapping("/by-name/{catName}")
    public ResponseEntity<CatDocument> getCatDocumentByName(@Valid @PathVariable("catName") String catName) {
        CatDocument catDocumentRequested = catDocumentService.getCatDocumentByName(catName);
        if (catDocumentRequested == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(catDocumentRequested);
    }

    @GetMapping("/by-ids/{catIds}")
    public ResponseEntity<List<CatDocument>> getAllCatDocumentByIds(@Valid @PathVariable("catIds") Set<UUID> catIds) {
        try {
            List<CatDocument> catDocumentsRequested = catDocumentService.getAllCatDocumentByIds(catIds);
            return ResponseEntity.ok(catDocumentsRequested);
        } catch (CatDocumentModelNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-names/{catNames}")
    public ResponseEntity<List<CatDocument>> getAllCatDocumentByNames(
            @Valid @PathVariable("catNames") List<String> catNames) {
        try {
            List<CatDocument> catDocumentsRequested = catDocumentService.getAllCatDocumentByNames(catNames);
            return ResponseEntity.ok(catDocumentsRequested);
        } catch (CatDocumentModelNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("id/{catId}/update")
    public ResponseEntity<CatDocument> updateCatDocumentById(
            @Valid @RequestBody CatDocument catDocument, @PathVariable("catId") UUID catId) {
        try {
            CatDocument catDocumentRequested = catDocumentService.updateCatDocumentById(catDocument, catId);
            return ResponseEntity.ok(catDocumentRequested);
        } catch (CatDocumentModelNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("name/{catName}/update")
    public ResponseEntity<CatDocument> updateCatDocumentByName(
            @Valid @RequestBody CatDocument catDocument, @PathVariable("catName") String catName) {
        try {
            CatDocument catDocumentRequested = catDocumentService.updateCatDocumentByName(catDocument, catName);
            return ResponseEntity.ok(catDocumentRequested);
        } catch (CatDocumentModelNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
