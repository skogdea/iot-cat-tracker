package com.staticoyster.iotcattracker.service;

import com.staticoyster.iotcattracker.dto.CatDocument;
import com.staticoyster.iotcattracker.dto.ImmutableCatDocument;
import com.staticoyster.iotcattracker.enums.CatBreed;
import com.staticoyster.iotcattracker.enums.CatColorPattern;
import com.staticoyster.iotcattracker.enums.CatGender;
import com.staticoyster.iotcattracker.enums.CatPersonality;
import com.staticoyster.iotcattracker.exception.CatDocumentModelNotFoundException;
import com.staticoyster.iotcattracker.model.CatDocumentModel;
import com.staticoyster.iotcattracker.model.FavoriteFoodModel;
import com.staticoyster.iotcattracker.model.FavoriteToyModel;
import com.staticoyster.iotcattracker.repository.CatDocumentRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CatDocumentServiceTest {

    @Mock
    private CatDocumentRepository catDocumentRepository;

    @InjectMocks
    private CatDocumentService catDocumentService;

    private List<CatDocumentModel> catDocumentModels;
    private List<CatDocument> catDocuments;

    @BeforeEach
    void before() {

        List<FavoriteFoodModel> meiMeiFavoriteFoods;
        List<FavoriteFoodModel> caesarFavoriteFoods;
        List<FavoriteToyModel> meiMeiFavoriteToys;
        List<FavoriteToyModel> caesarFavoriteToys;

        FavoriteFoodModel meiMeiFavoriteFood = new FavoriteFoodModel();
        meiMeiFavoriteFood.setFavoriteFood("freeze dried");

        meiMeiFavoriteFoods = new ArrayList<>();
        meiMeiFavoriteFoods.add(meiMeiFavoriteFood);

        FavoriteFoodModel caesarFavoriteFood = new FavoriteFoodModel();
        caesarFavoriteFood.setFavoriteFood("chicken");

        caesarFavoriteFoods = new ArrayList<>();
        caesarFavoriteFoods.add(caesarFavoriteFood);

        FavoriteToyModel meiMeiFavoriteToy = new FavoriteToyModel();
        meiMeiFavoriteToy.setFavoriteToy("hair tie");

        meiMeiFavoriteToys = new ArrayList<>();
        meiMeiFavoriteToys.add(meiMeiFavoriteToy);

        FavoriteToyModel caesarFavoriteToy = new FavoriteToyModel();
        caesarFavoriteToy.setFavoriteToy("twig");
        FavoriteToyModel caesarFavoriteToy2 = new FavoriteToyModel();
        caesarFavoriteToy2.setFavoriteToy("catnip");

        caesarFavoriteToys = new ArrayList<>();
        caesarFavoriteToys.add(caesarFavoriteToy);
        caesarFavoriteToys.add(caesarFavoriteToy2);

        catDocumentModels = Arrays.asList(
                CatDocumentModel.Builder.newBuilder()
                        .withCatId(UUID.fromString("5f88acc7-eab3-42b5-b95d-b256f2b6339b"))
                        .withCatName("Mei Mei")
                        .withCatGender(CatGender.FEMALE)
                        .withCatAge(2)
                        .withCatWeight(4.5)
                        .withCatBreed(CatBreed.MIX)
                        .withCatColorPattern(CatColorPattern.TORTOISESHELL)
                        .withCatPersonality(CatPersonality.LIVELY)
                        .withFavoriteFoods(meiMeiFavoriteFoods)
                        .withFavoriteToys(meiMeiFavoriteToys)
                        .withBestFriend("her younger brother")
                        .build(),
                CatDocumentModel.Builder.newBuilder()
                        .withCatId(UUID.fromString("69f4f59f-fa16-4d07-bc0e-d2a7499c8fd8"))
                        .withCatName("Caesar")
                        .withCatGender(CatGender.MALE)
                        .withCatAge(9)
                        .withCatWeight(9.0)
                        .withCatBreed(CatBreed.MIX)
                        .withCatColorPattern(CatColorPattern.GINGER)
                        .withCatPersonality(CatPersonality.SEDENTARY)
                        .withFavoriteFoods(caesarFavoriteFoods)
                        .withFavoriteToys(caesarFavoriteToys)
                        .withBestFriend("my mom")
                        .build());
        catDocuments = Arrays.asList(
                ImmutableCatDocument.builder()
                        .catId(UUID.fromString("5f88acc7-eab3-42b5-b95d-b256f2b6339b"))
                        .catName("Mei Mei")
                        .catGender(CatGender.FEMALE)
                        .catAge(2)
                        .catWeight(4.5)
                        .catBreed(CatBreed.MIX)
                        .catColorPattern(CatColorPattern.TORTOISESHELL)
                        .catPersonality(CatPersonality.LIVELY)
                        .favoriteFoods(meiMeiFavoriteFoods)
                        .favoriteToys(meiMeiFavoriteToys)
                        .bestFriend("her younger brother")
                        .build(),
                ImmutableCatDocument.builder()
                        .catId(UUID.fromString("69f4f59f-fa16-4d07-bc0e-d2a7499c8fd8"))
                        .catName("Caesar")
                        .catGender(CatGender.MALE)
                        .catAge(9)
                        .catWeight(9.0)
                        .catBreed(CatBreed.MIX)
                        .catColorPattern(CatColorPattern.GINGER)
                        .catPersonality(CatPersonality.SEDENTARY)
                        .favoriteFoods(caesarFavoriteFoods)
                        .favoriteToys(caesarFavoriteToys)
                        .bestFriend("my mom")
                        .build());
    }

    @Test
    public void createOneShouldReturnTheSameData() {
        Mockito.when(catDocumentRepository.save(Mockito.any())).thenReturn(catDocumentModels.get(0));
        CatDocument oneCreated = catDocumentService.createCatDocument(catDocuments.get(0));

        Assertions.assertThat(oneCreated.getCatName())
                .isEqualTo(catDocumentModels.get(0).getCatName());
        Assertions.assertThat(oneCreated.getCatGender())
                .isEqualTo(catDocumentModels.get(0).getCatGender());
        Assertions.assertThat(oneCreated.getCatAge())
                .isEqualTo(catDocumentModels.get(0).getCatAge());
        Assertions.assertThat(oneCreated.getCatWeight())
                .isEqualTo(catDocumentModels.get(0).getCatWeight());
        Assertions.assertThat(oneCreated.getCatBreed())
                .isEqualTo(catDocumentModels.get(0).getCatBreed());
        Assertions.assertThat(oneCreated.getCatColorPattern())
                .isEqualTo(catDocumentModels.get(0).getCatColorPattern());
        Assertions.assertThat(oneCreated.getCatPersonality())
                .isEqualTo(catDocumentModels.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(oneCreated);
        Assertions.assertThat(oneCreated.getBestFriend())
                .isEqualTo(catDocumentModels.get(0).getBestFriend());
        Mockito.verify(catDocumentRepository, Mockito.times(1)).save(Mockito.<CatDocumentModel>any());
    }

    @Test
    public void getByIdWhenFoundShouldBeRetrieved() {
        UUID catId = UUID.randomUUID();
        Mockito.when(catDocumentRepository.findById(catId)).thenReturn(Optional.of(catDocumentModels.get(0)));
        CatDocument oneRequested = catDocumentService.getCatDocumentById(catId);

        Assertions.assertThat(oneRequested.getCatName())
                .isEqualTo(catDocumentModels.get(0).getCatName());
        Assertions.assertThat(oneRequested.getCatGender())
                .isEqualTo(catDocumentModels.get(0).getCatGender());
        Assertions.assertThat(oneRequested.getCatAge())
                .isEqualTo(catDocumentModels.get(0).getCatAge());
        Assertions.assertThat(oneRequested.getCatWeight())
                .isEqualTo(catDocumentModels.get(0).getCatWeight());
        Assertions.assertThat(oneRequested.getCatBreed())
                .isEqualTo(catDocumentModels.get(0).getCatBreed());
        Assertions.assertThat(oneRequested.getCatColorPattern())
                .isEqualTo(catDocumentModels.get(0).getCatColorPattern());
        Assertions.assertThat(oneRequested.getCatPersonality())
                .isEqualTo(catDocumentModels.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(oneRequested);
        Assertions.assertThat(oneRequested.getBestFriend())
                .isEqualTo(catDocumentModels.get(0).getBestFriend());
        Mockito.verify(catDocumentRepository, Mockito.times(1)).findById(catId);
    }

    @Test
    public void getByIdWhenNotFoundShouldThrowException() {
        UUID catId = UUID.randomUUID();
        Mockito.when(catDocumentRepository.findById(catId)).thenReturn(Optional.empty());
        CatDocumentModelNotFoundException exception = Assert.assertThrows(
                CatDocumentModelNotFoundException.class, () -> catDocumentService.getCatDocumentById(catId));
        Assertions.assertThat("The cat document model with id: " + catId + " is not found.")
                .isEqualTo(exception.getMessage());
        Mockito.verify(catDocumentRepository, Mockito.times(1)).findById(catId);
    }

    @Test
    public void getByNameWhenFoundShouldBeRetrieved() {
        String catName = catDocumentModels.get(1).getCatName().toLowerCase();
        Mockito.when(catDocumentRepository.findByCatName(catName)).thenReturn(Optional.of(catDocumentModels.get(1)));
        CatDocument oneRequested =
                catDocumentService.getCatDocumentByName(catDocumentModels.get(1).getCatName());
        Assertions.assertThat(oneRequested.getCatName())
                .isEqualTo(catDocumentModels.get(1).getCatName());
        Assertions.assertThat(oneRequested.getCatGender())
                .isEqualTo(catDocumentModels.get(1).getCatGender());
        Assertions.assertThat(oneRequested.getCatAge())
                .isEqualTo(catDocumentModels.get(1).getCatAge());
        Assertions.assertThat(oneRequested.getCatWeight())
                .isEqualTo(catDocumentModels.get(1).getCatWeight());
        Assertions.assertThat(oneRequested.getCatBreed())
                .isEqualTo(catDocumentModels.get(1).getCatBreed());
        Assertions.assertThat(oneRequested.getCatColorPattern())
                .isEqualTo(catDocumentModels.get(1).getCatColorPattern());
        Assertions.assertThat(oneRequested.getCatPersonality())
                .isEqualTo(catDocumentModels.get(1).getCatPersonality());
        assertListOfCaesarFoodsAndToys(oneRequested);
        Assertions.assertThat(oneRequested.getBestFriend())
                .isEqualTo(catDocumentModels.get(1).getBestFriend());

        Mockito.verify(catDocumentRepository, Mockito.times(1)).findByCatName(catName);
    }

    @Test
    public void getByNameWhenNotFoundShouldThrowException() {
        String catName = "SolNa";
        Mockito.when(catDocumentRepository.findByCatName(catName.toLowerCase())).thenReturn(Optional.empty());
        CatDocumentModelNotFoundException exception = Assert.assertThrows(
                CatDocumentModelNotFoundException.class, () -> catDocumentService.getCatDocumentByName(catName));
        Assertions.assertThat("The cat document model with name: " + catName + " is not found.")
                .isEqualTo(exception.getMessage());
        Mockito.verify(catDocumentRepository, Mockito.times(1)).findByCatName(catName.toLowerCase());
    }

    @Test
    public void getByIdsWhenFoundShouldBeRetrieved() {
        Set<UUID> catIds = Set.of(
                catDocumentModels.get(0).getCatId(), catDocumentModels.get(1).getCatId());
        Mockito.when(catDocumentRepository.findByCatIdIn(catIds)).thenReturn(catDocumentModels);
        List<CatDocument> allRequested = catDocumentService.getAllCatDocumentByIds(catIds);
        Assertions.assertThat(allRequested).hasSize(2);
        Assertions.assertThat(allRequested.get(0).getCatName())
                .isEqualTo(catDocuments.get(0).getCatName());
        Assertions.assertThat(allRequested.get(0).getCatGender())
                .isEqualTo(catDocuments.get(0).getCatGender());
        Assertions.assertThat(allRequested.get(0).getCatAge())
                .isEqualTo(catDocuments.get(0).getCatAge());
        Assertions.assertThat(allRequested.get(0).getCatWeight())
                .isEqualTo(catDocuments.get(0).getCatWeight());
        Assertions.assertThat(allRequested.get(0).getCatBreed())
                .isEqualTo(catDocuments.get(0).getCatBreed());
        Assertions.assertThat(allRequested.get(0).getCatColorPattern())
                .isEqualTo(catDocuments.get(0).getCatColorPattern());
        Assertions.assertThat(allRequested.get(0).getCatPersonality())
                .isEqualTo(catDocuments.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(allRequested.get(0));
        Assertions.assertThat(allRequested.get(0).getBestFriend())
                .isEqualTo(catDocuments.get(0).getBestFriend());

        Assertions.assertThat(allRequested.get(1).getCatName())
                .isEqualTo(catDocuments.get(1).getCatName());
        Assertions.assertThat(allRequested.get(1).getCatGender())
                .isEqualTo(catDocuments.get(1).getCatGender());
        Assertions.assertThat(allRequested.get(1).getCatAge())
                .isEqualTo(catDocuments.get(1).getCatAge());
        Assertions.assertThat(allRequested.get(1).getCatWeight())
                .isEqualTo(catDocuments.get(1).getCatWeight());
        Assertions.assertThat(allRequested.get(1).getCatBreed())
                .isEqualTo(catDocuments.get(1).getCatBreed());
        Assertions.assertThat(allRequested.get(1).getCatColorPattern())
                .isEqualTo(catDocuments.get(1).getCatColorPattern());
        Assertions.assertThat(allRequested.get(1).getCatPersonality())
                .isEqualTo(catDocuments.get(1).getCatPersonality());
        assertListOfCaesarFoodsAndToys(allRequested.get(1));
        Assertions.assertThat(allRequested.get(1).getBestFriend())
                .isEqualTo(catDocuments.get(1).getBestFriend());
    }

    @Test
    public void getByIdsWhenNotFoundShouldReturnEmpty() {
        Set<UUID> catIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        Mockito.when(catDocumentRepository.findByCatIdIn(catIds)).thenReturn(Collections.emptyList());
        Assertions.assertThat(catDocumentService.getAllCatDocumentByIds(catIds)).isEmpty();
        Mockito.verify(catDocumentRepository, Mockito.times(1)).findByCatIdIn(catIds);
    }

    @Test
    public void getByNamesWhenFoundShouldBeRetrieved() {
        List<String> catNames = List.of(
                catDocumentModels.get(0).getCatName(), catDocumentModels.get(1).getCatName());
        List<String> catNamesToLowerCase =
                catNames.stream().map(String::toLowerCase).toList();
        Mockito.when(catDocumentRepository.findByCatNameIn(catNamesToLowerCase)).thenReturn(catDocumentModels);
        List<CatDocument> allRequested = catDocumentService.getAllCatDocumentByNames(catNames);
        Assertions.assertThat(allRequested.get(0).getCatName())
                .isEqualTo(catDocumentModels.get(0).getCatName());
        Assertions.assertThat(allRequested.get(0).getCatGender())
                .isEqualTo(catDocuments.get(0).getCatGender());
        Assertions.assertThat(allRequested.get(0).getCatAge())
                .isEqualTo(catDocuments.get(0).getCatAge());
        Assertions.assertThat(allRequested.get(0).getCatWeight())
                .isEqualTo(catDocuments.get(0).getCatWeight());
        Assertions.assertThat(allRequested.get(0).getCatBreed())
                .isEqualTo(catDocuments.get(0).getCatBreed());
        Assertions.assertThat(allRequested.get(0).getCatColorPattern())
                .isEqualTo(catDocuments.get(0).getCatColorPattern());
        Assertions.assertThat(allRequested.get(0).getCatPersonality())
                .isEqualTo(catDocuments.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(allRequested.get(0));
        Assertions.assertThat(allRequested.get(0).getBestFriend())
                .isEqualTo(catDocuments.get(0).getBestFriend());

        Assertions.assertThat(allRequested.get(1).getCatName())
                .isEqualTo(catDocuments.get(1).getCatName());
        Assertions.assertThat(allRequested.get(1).getCatGender())
                .isEqualTo(catDocuments.get(1).getCatGender());
        Assertions.assertThat(allRequested.get(1).getCatAge())
                .isEqualTo(catDocuments.get(1).getCatAge());
        Assertions.assertThat(allRequested.get(1).getCatWeight())
                .isEqualTo(catDocuments.get(1).getCatWeight());
        Assertions.assertThat(allRequested.get(1).getCatBreed())
                .isEqualTo(catDocuments.get(1).getCatBreed());
        Assertions.assertThat(allRequested.get(1).getCatColorPattern())
                .isEqualTo(catDocuments.get(1).getCatColorPattern());
        Assertions.assertThat(allRequested.get(1).getCatPersonality())
                .isEqualTo(catDocuments.get(1).getCatPersonality());
        assertListOfCaesarFoodsAndToys(allRequested.get(1));
        Assertions.assertThat(allRequested.get(1).getBestFriend())
                .isEqualTo(catDocuments.get(1).getBestFriend());
        Mockito.verify(catDocumentRepository, Mockito.times(2)).findByCatNameIn(catNamesToLowerCase);
    }

    @Test
    public void getByNamesWhenNotFoundShouldReturnEmpty() {
        List<String> catNames = List.of("SolNa", "ZonGzi");
        List<String> catNamesToLowerCase =
                catNames.stream().map(String::toLowerCase).toList();
        Mockito.when(catDocumentRepository.findByCatNameIn(catNamesToLowerCase)).thenReturn(Collections.emptyList());
        Assertions.assertThat(catDocumentService.getAllCatDocumentByNames(catNames))
                .isEmpty();
        Mockito.verify(catDocumentRepository, Mockito.times(1)).findByCatNameIn(catNamesToLowerCase);
    }

    // only test id is found scenario, the not found scenario is the same as getByIdWhenNotFoundShouldThrowException().
    @Test
    public void getByIdWhenFoundShouldUpdateTheCatDocumentAccordingToTheOnePassedIn() {
        UUID catId = catDocumentModels.get(0).getCatId();
        Mockito.when(catDocumentRepository.findById(catId)).thenReturn(Optional.of(catDocumentModels.get(0)));
        Assertions.assertThat(catId).isNotNull();

        Optional<CatDocumentModel> modelRequested = catDocumentRepository.findById(catId);
        Mockito.verify(catDocumentRepository, Mockito.times(1)).findById(catId);

        CatDocumentModel modelPassedIn = catDocumentModels.get(1);
        CatDocument catDocumentPassedIn = catDocumentService.convertToCatDocument(modelPassedIn);

        CatDocumentModel modelUpdated = catDocumentService.getModelUpdated(catDocumentPassedIn);
        Mockito.when(catDocumentRepository.save(modelUpdated)).thenReturn(modelUpdated);

        CatDocument catDocumentUpdated = catDocumentService.updateCatDocumentById(catDocumentPassedIn, catId);
        Assertions.assertThat(catDocumentUpdated.getCatName()).isEqualTo(catDocumentPassedIn.getCatName());
        Assertions.assertThat(catDocumentUpdated.getCatGender()).isEqualTo(catDocumentPassedIn.getCatGender());
        Assertions.assertThat(catDocumentUpdated.getCatAge()).isEqualTo(catDocumentPassedIn.getCatAge());
        Assertions.assertThat(catDocumentUpdated.getCatWeight()).isEqualTo(catDocumentPassedIn.getCatWeight());
        Assertions.assertThat(catDocumentUpdated.getCatBreed()).isEqualTo(catDocumentPassedIn.getCatBreed());
        Assertions.assertThat(catDocumentUpdated.getCatColorPattern())
                .isEqualTo(catDocumentPassedIn.getCatColorPattern());
        Assertions.assertThat(catDocumentUpdated.getCatPersonality())
                .isEqualTo(catDocumentPassedIn.getCatPersonality());
        assertListOfCaesarFoodsAndToys(catDocumentUpdated);
        Assertions.assertThat(catDocumentUpdated.getBestFriend()).isEqualTo(catDocumentPassedIn.getBestFriend());
        Mockito.verify(catDocumentRepository, Mockito.times(1)).save(modelUpdated);
    }

    // only test name is found scenario, the not found scenario is
    // the same as getByNameWhenNotFoundShouldThrowException().
    @Test
    public void getByNameWhenFoundShouldUpdateTheCatDocumentAccordingToTheOnePassedIn() {
        String catName = (catDocumentModels.get(0).getCatName()).toLowerCase();
        Mockito.when(catDocumentRepository.findByCatName(catName)).thenReturn(Optional.of(catDocumentModels.get(0)));
        Assertions.assertThat(catName).isNotNull();
        Optional<CatDocumentModel> modelRequested = catDocumentRepository.findByCatName(catName);
        Mockito.verify(catDocumentRepository, Mockito.times(1)).findByCatName(catName);

        CatDocumentModel modelPassedIn = catDocumentModels.get(1);
        CatDocument catDocumentPassedIn = catDocumentService.convertToCatDocument(modelPassedIn);

        CatDocumentModel modelUpdated = catDocumentService.getModelUpdated(catDocumentPassedIn);
        Mockito.when(catDocumentRepository.save(modelUpdated)).thenReturn(modelUpdated);

        CatDocument catDocumentUpdated = catDocumentService.updateCatDocumentByName(catDocumentPassedIn, catName);
        Assertions.assertThat(catDocumentUpdated.getCatName()).isEqualTo(catDocumentPassedIn.getCatName());
        Assertions.assertThat(catDocumentUpdated.getCatGender()).isEqualTo(catDocumentPassedIn.getCatGender());
        Assertions.assertThat(catDocumentUpdated.getCatAge()).isEqualTo(catDocumentPassedIn.getCatAge());
        Assertions.assertThat(catDocumentUpdated.getCatWeight()).isEqualTo(catDocumentPassedIn.getCatWeight());
        Assertions.assertThat(catDocumentUpdated.getCatBreed()).isEqualTo(catDocumentPassedIn.getCatBreed());
        Assertions.assertThat(catDocumentUpdated.getCatColorPattern())
                .isEqualTo(catDocumentPassedIn.getCatColorPattern());
        Assertions.assertThat(catDocumentUpdated.getCatPersonality())
                .isEqualTo(catDocumentPassedIn.getCatPersonality());
        assertListOfCaesarFoodsAndToys(catDocumentUpdated);
        Assertions.assertThat(catDocumentUpdated.getBestFriend()).isEqualTo(catDocumentPassedIn.getBestFriend());
        Mockito.verify(catDocumentRepository, Mockito.times(1)).save(modelUpdated);
    }

    private void assertListOfMeiMeiFoodsAndToys(CatDocument catDocument) {
        List<FavoriteFoodModel> foods = catDocument.getFavoriteFoods();
        Assertions.assertThat(foods)
                .hasSize(catDocumentModels.get(0).getFavoriteFoods().size());
        Assertions.assertThat(foods)
                .usingElementComparator(Comparator.comparing(FavoriteFoodModel::getFavoriteFood))
                .containsExactlyElementsOf(catDocumentModels.get(0).getFavoriteFoods());
        List<FavoriteToyModel> toys = catDocument.getFavoriteToys();
        Assertions.assertThat(toys)
                .hasSize(catDocumentModels.get(0).getFavoriteToys().size());
        Assertions.assertThat(toys)
                .usingElementComparator(Comparator.comparing(FavoriteToyModel::getFavoriteToy))
                .containsExactlyElementsOf(catDocumentModels.get(0).getFavoriteToys());
    }

    private void assertListOfCaesarFoodsAndToys(CatDocument catDocument) {
        List<FavoriteFoodModel> foods = catDocument.getFavoriteFoods();
        Assertions.assertThat(foods)
                .hasSize(catDocumentModels.get(1).getFavoriteFoods().size());
        Assertions.assertThat(foods)
                .usingElementComparator(Comparator.comparing(FavoriteFoodModel::getFavoriteFood))
                .containsExactlyElementsOf(catDocumentModels.get(1).getFavoriteFoods());
        List<FavoriteToyModel> toys = catDocument.getFavoriteToys();
        Assertions.assertThat(toys)
                .hasSize(catDocumentModels.get(1).getFavoriteToys().size());
        Assertions.assertThat(toys)
                .usingElementComparator(Comparator.comparing(FavoriteToyModel::getFavoriteToy))
                .containsExactlyElementsOf(catDocumentModels.get(1).getFavoriteToys());
    }
}
