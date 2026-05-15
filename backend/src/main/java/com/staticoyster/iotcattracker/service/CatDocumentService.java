package com.staticoyster.iotcattracker.service;

import com.staticoyster.iotcattracker.dto.CatDocument;
import com.staticoyster.iotcattracker.dto.ImmutableCatDocument;
import com.staticoyster.iotcattracker.exception.CatDocumentModelNotFoundException;
import com.staticoyster.iotcattracker.model.CatDocumentModel;
import com.staticoyster.iotcattracker.repository.CatDocumentRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CatDocumentService {

    private final CatDocumentRepository catDocumentRepository;

    public CatDocumentService(CatDocumentRepository catDocumentRepository) {
        this.catDocumentRepository = catDocumentRepository;
    }

    public CatDocument createCatDocument(CatDocument catDocument) {
        CatDocumentModel model = convertToCatDocumentModel(catDocument);
        CatDocumentModel modelCreated = catDocumentRepository.save(model);
        return convertToCatDocument(modelCreated);
    }

    public CatDocument getCatDocumentById(UUID catId) {
        return catDocumentRepository
                .findById(catId)
                .map(this::convertToCatDocument)
                .orElseThrow(() -> new CatDocumentModelNotFoundException(catId));
    }

    public CatDocument getCatDocumentByName(String catName) {
        return catDocumentRepository
                .findByCatName(catName.toLowerCase())
                .map(this::convertToCatDocument)
                .orElseThrow(() -> new CatDocumentModelNotFoundException(catName));
    }

    public List<CatDocument> getAllCatDocumentByIds(Set<UUID> catIds) {
        List<CatDocumentModel> modelsRequested = catDocumentRepository.findByCatIdIn(catIds);
        Set<UUID> idsFound =
                modelsRequested.stream().map(CatDocumentModel::getCatId).collect(Collectors.toSet());
        return catDocumentRepository.findByCatIdIn(idsFound).stream()
                .map(this::convertToCatDocument)
                .toList();
    }

    public List<CatDocument> getAllCatDocumentByNames(List<String> catNames) {
        List<String> catNamesToLowerCase =
                catNames.stream().map(String::toLowerCase).toList();
        List<CatDocumentModel> modelsRequested = catDocumentRepository.findByCatNameIn(catNamesToLowerCase);
        List<String> namesFound = modelsRequested.stream()
                .map(CatDocumentModel::getCatName)
                .map(String::toLowerCase)
                .toList();
        return catDocumentRepository.findByCatNameIn(namesFound).stream()
                .map(this::convertToCatDocument)
                .toList();
    }

    //    parameter1: catDocument passed in which has the updated information;
    //    parameter2: catId requested to retrieve the model
    public CatDocument updateCatDocumentById(CatDocument catDocument, UUID catId) {
        Optional<CatDocumentModel> modelRequested = catDocumentRepository.findById(catId);
        if (modelRequested.isPresent()) {
            CatDocumentModel modelUpdated = getModelUpdated(catDocument);
            modelUpdated = catDocumentRepository.save(modelUpdated);
            return convertToCatDocument(modelUpdated);
        } else {
            throw new CatDocumentModelNotFoundException(catId);
        }
    }

    public CatDocument updateCatDocumentByName(CatDocument catDocument, String catName) {
        Optional<CatDocumentModel> modelRequested = catDocumentRepository.findByCatName(catName.toLowerCase());
        if (modelRequested.isPresent()) {
            CatDocumentModel modelUpdated = getModelUpdated(catDocument);
            modelUpdated = catDocumentRepository.save(modelUpdated);
            return convertToCatDocument(modelUpdated);
        } else {
            throw new CatDocumentModelNotFoundException(catName);
        }
    }

    public CatDocumentModel getModelUpdated(CatDocument catDocument) {
        CatDocumentModel modelUpdated = new CatDocumentModel();
        modelUpdated.setCatId(catDocument.getCatId());
        modelUpdated.setCatName(catDocument.getCatName());
        modelUpdated.setCatGender(catDocument.getCatGender());
        modelUpdated.setCatAge(catDocument.getCatAge());
        modelUpdated.setCatWeight(catDocument.getCatWeight());
        modelUpdated.setCatBreed(catDocument.getCatBreed());
        modelUpdated.setCatColorPattern(catDocument.getCatColorPattern());
        modelUpdated.setCatPersonality(catDocument.getCatPersonality());
        modelUpdated.setFavoriteFoods(catDocument.getFavoriteFoods());
        modelUpdated.setFavoriteToys(catDocument.getFavoriteToys());
        modelUpdated.setBestFriend(catDocument.getBestFriend());
        return modelUpdated;
    }

    public CatDocumentModel convertToCatDocumentModel(CatDocument catDocument) {
        return CatDocumentModel.Builder.newBuilder()
                .withCatName(catDocument.getCatName())
                .withCatGender(catDocument.getCatGender())
                .withCatAge(catDocument.getCatAge())
                .withCatWeight(catDocument.getCatWeight())
                .withCatBreed(catDocument.getCatBreed())
                .withCatColorPattern(catDocument.getCatColorPattern())
                .withCatPersonality(catDocument.getCatPersonality())
                .withFavoriteFoods(catDocument.getFavoriteFoods())
                .withFavoriteToys(catDocument.getFavoriteToys())
                .withBestFriend(catDocument.getBestFriend())
                .build();
    }

    public CatDocument convertToCatDocument(CatDocumentModel catDocumentModel) {
        return ImmutableCatDocument.builder()
                .catId(catDocumentModel.getCatId())
                .catName(catDocumentModel.getCatName())
                .catGender(catDocumentModel.getCatGender())
                .catAge(catDocumentModel.getCatAge())
                .catWeight(catDocumentModel.getCatWeight())
                .catBreed(catDocumentModel.getCatBreed())
                .catColorPattern(catDocumentModel.getCatColorPattern())
                .catPersonality(catDocumentModel.getCatPersonality())
                .favoriteFoods(catDocumentModel.getFavoriteFoods())
                .favoriteToys(catDocumentModel.getFavoriteToys())
                .bestFriend(catDocumentModel.getBestFriend())
                .build();
    }
}
