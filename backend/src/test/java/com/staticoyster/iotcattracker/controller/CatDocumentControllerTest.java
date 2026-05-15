package com.staticoyster.iotcattracker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.staticoyster.iotcattracker.dto.CatDocument;
import com.staticoyster.iotcattracker.dto.ImmutableCatDocument;
import com.staticoyster.iotcattracker.enums.CatBreed;
import com.staticoyster.iotcattracker.enums.CatColorPattern;
import com.staticoyster.iotcattracker.enums.CatGender;
import com.staticoyster.iotcattracker.enums.CatPersonality;
import com.staticoyster.iotcattracker.model.FavoriteFoodModel;
import com.staticoyster.iotcattracker.model.FavoriteToyModel;
import com.staticoyster.iotcattracker.service.CatDocumentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = CatDocumentController.class)
@ContextConfiguration(classes = {CatDocumentController.class, CatDocumentService.class})
public class CatDocumentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CatDocumentService catDocumentService;

    private List<CatDocument> catDocuments;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(CatDocumentControllerTest.class);

    @BeforeEach
    void before() {

        CatDocument catDocument;
        CatDocument catDocument2;
        objectMapper.registerModule(new Jdk8Module());

        FavoriteFoodModel caesarFavoriteFood = new FavoriteFoodModel();
        caesarFavoriteFood.setFavoriteFood("chicken");

        List<FavoriteFoodModel> caesarFavoriteFoods = new ArrayList<>();
        caesarFavoriteFoods.add(caesarFavoriteFood);

        FavoriteFoodModel meiMeiFavoriteFood = new FavoriteFoodModel();
        meiMeiFavoriteFood.setFavoriteFood("freeze dried");

        List<FavoriteFoodModel> meiMeiFavoriteFoods = new ArrayList<>();
        meiMeiFavoriteFoods.add(meiMeiFavoriteFood);

        FavoriteToyModel caesarFavoriteToy = new FavoriteToyModel();
        caesarFavoriteToy.setFavoriteToy("twig");
        FavoriteToyModel caesarFavoriteToy2 = new FavoriteToyModel();
        caesarFavoriteToy2.setFavoriteToy("catnip");

        List<FavoriteToyModel> caesarFavoriteToys = new ArrayList<>();
        caesarFavoriteToys.add(caesarFavoriteToy);
        caesarFavoriteToys.add(caesarFavoriteToy2);

        FavoriteToyModel meiMeiFavoriteToy = new FavoriteToyModel();
        meiMeiFavoriteToy.setFavoriteToy("hair tie");

        List<FavoriteToyModel> meiMeiFavoriteToys = new ArrayList<>();
        meiMeiFavoriteToys.add(meiMeiFavoriteToy);

        catDocuments = new ArrayList<>();
        catDocument = ImmutableCatDocument.builder()
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
                .build();
        catDocuments.add(catDocument);
        catDocument2 = ImmutableCatDocument.builder()
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
                .build();
        catDocuments.add(catDocument2);
    }

    @Test
    public void createWhenSuccessfulShouldReturnEntityWith201() throws Exception {
        Mockito.when(catDocumentService.createCatDocument(ArgumentMatchers.any(CatDocument.class)))
                .thenReturn(catDocuments.get(0));

        String foodJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteFoods());
        List<FavoriteFoodModel> foods = objectMapper.readValue(foodJson, new TypeReference<>() {});

        String toyJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteToys());
        List<FavoriteToyModel> toys = objectMapper.readValue(toyJson, new TypeReference<>() {});

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/cat-document/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catDocuments.get(0))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_name", Matchers.is(catDocuments.get(0).getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_gender",
                        Matchers.is(catDocuments.get(0).getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_age", Matchers.is(catDocuments.get(0).getCatAge())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_weight", Matchers.is(catDocuments.get(0).getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_breed",
                        Matchers.is(catDocuments.get(0).getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_color_pattern",
                        Matchers.is(catDocuments.get(0).getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_personality",
                        Matchers.is(catDocuments.get(0).getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_foods[0].favoriteFood",
                        Matchers.is(foods.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[0].favoriteToy",
                        Matchers.is(toys.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[1].favoriteToy",
                        Matchers.is(toys.get(1).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.best_friend", Matchers.is(catDocuments.get(0).getBestFriend())))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseContent.contains(catDocuments.get(0).getCatName()));
    }

    @Test
    public void createWhenUnsuccessfulShouldReturn500() throws Exception {
        FavoriteFoodModel favoriteFood = new FavoriteFoodModel();
        favoriteFood.setFavoriteFood("fish");
        List<FavoriteFoodModel> favoriteFoods = new ArrayList<>();
        favoriteFoods.add(favoriteFood);

        FavoriteToyModel favoriteToy = new FavoriteToyModel();
        favoriteToy.setFavoriteToy("crinkly");
        List<FavoriteToyModel> favoriteToys = new ArrayList<>();
        favoriteToys.add(favoriteToy);
        CatDocument invalidCatDocument = ImmutableCatDocument.builder()
                .catId(UUID.fromString("052823b7-cb39-40de-9865-690aaaf3eabc"))
                .catName("")
                .catGender(CatGender.MALE)
                .catAge(4)
                .catWeight(4.0)
                .catBreed(CatBreed.ABYSSINIAN)
                .catColorPattern(CatColorPattern.GINGER)
                .catPersonality(CatPersonality.LIVELY)
                .favoriteFoods(favoriteFoods)
                .favoriteToys(favoriteToys)
                .bestFriend("")
                .build();
        Mockito.when(catDocumentService.createCatDocument(invalidCatDocument)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/cat-document/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCatDocument)))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    public void getByIdWhenFoundShouldReturnEntityWith200() throws Exception {
        UUID catId = UUID.randomUUID();
        Mockito.when(catDocumentService.getCatDocumentById(catId)).thenReturn(catDocuments.get(0));

        String foodJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteFoods());
        List<FavoriteFoodModel> foods = objectMapper.readValue(foodJson, new TypeReference<>() {});

        String toyJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteToys());
        List<FavoriteToyModel> toys = objectMapper.readValue(toyJson, new TypeReference<>() {});

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/cat-document/by-id/%s", catId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_name", Matchers.is(catDocuments.get(0).getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_gender",
                        Matchers.is(catDocuments.get(0).getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_age", Matchers.is(catDocuments.get(0).getCatAge())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_weight", Matchers.is(catDocuments.get(0).getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_breed",
                        Matchers.is(catDocuments.get(0).getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_color_pattern",
                        Matchers.is(catDocuments.get(0).getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_personality",
                        Matchers.is(catDocuments.get(0).getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_foods[0].favoriteFood",
                        Matchers.is(foods.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[0].favoriteToy",
                        Matchers.is(toys.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[1].favoriteToy",
                        Matchers.is(toys.get(1).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.best_friend", Matchers.is(catDocuments.get(0).getBestFriend())))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseContent.contains(catDocuments.get(0).getCatName()));
    }

    @Test
    public void getByIdWhenNotFoundShouldReturn404() throws Exception {
        UUID catId = UUID.randomUUID();
        Mockito.when(catDocumentService.getCatDocumentById(catId)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/cat-document/by-id/%s", catId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getByNameWhenFoundShouldReturnEntityWith200() throws Exception {
        String catName = catDocuments.get(0).getCatName();
        Mockito.when(catDocumentService.getCatDocumentByName(catName)).thenReturn(catDocuments.get(0));

        String foodJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteFoods());
        List<FavoriteFoodModel> foods = objectMapper.readValue(foodJson, new TypeReference<>() {});

        String toyJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteToys());
        List<FavoriteToyModel> toys = objectMapper.readValue(toyJson, new TypeReference<>() {});

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(String.format("/cat-document/by-name/%s", catName))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_name", Matchers.is(catDocuments.get(0).getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_gender",
                        Matchers.is(catDocuments.get(0).getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_age", Matchers.is(catDocuments.get(0).getCatAge())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_weight", Matchers.is(catDocuments.get(0).getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_breed",
                        Matchers.is(catDocuments.get(0).getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_color_pattern",
                        Matchers.is(catDocuments.get(0).getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_personality",
                        Matchers.is(catDocuments.get(0).getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_foods[0].favoriteFood",
                        Matchers.is(foods.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[0].favoriteToy",
                        Matchers.is(toys.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[1].favoriteToy",
                        Matchers.is(toys.get(1).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.best_friend", Matchers.is(catDocuments.get(0).getBestFriend())))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseContent.contains(catName));
    }

    @Test
    public void getByNameWhenNotFoundShouldReturn404() throws Exception {
        String catName = "SolNa";
        Mockito.when(catDocumentService.getCatDocumentByName(catName)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/cat-document/by-name/%s", catName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getByIdsWhenFoundShouldReturnEntitiesWith200() throws Exception {
        Set<UUID> catIds =
                Set.of(catDocuments.get(0).getCatId(), catDocuments.get(1).getCatId());
        Mockito.when(catDocumentService.getAllCatDocumentByIds(catIds)).thenReturn(catDocuments);

        String foodJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteFoods());
        List<FavoriteFoodModel> foods = objectMapper.readValue(foodJson, new TypeReference<>() {});

        String toyJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteToys());
        List<FavoriteToyModel> toys = objectMapper.readValue(toyJson, new TypeReference<>() {});

        String foodJson2 = objectMapper.writeValueAsString(catDocuments.get(1).getFavoriteFoods());
        List<FavoriteFoodModel> foods2 = objectMapper.readValue(foodJson2, new TypeReference<>() {});

        String toyJson2 = objectMapper.writeValueAsString(catDocuments.get(1).getFavoriteToys());
        List<FavoriteToyModel> toys2 = objectMapper.readValue(toyJson2, new TypeReference<>() {});

        String catIdsToString = catIds.stream().map(UUID::toString).collect(Collectors.joining(","));
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(String.format("/cat-document/by-ids/%s", catIdsToString))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_name", Matchers.is(catDocuments.get(0).getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_gender",
                        Matchers.is(catDocuments.get(0).getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0]cat_age", Matchers.is(catDocuments.get(0).getCatAge())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0]cat_weight", Matchers.is(catDocuments.get(0).getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_breed",
                        Matchers.is(catDocuments.get(0).getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_color_pattern",
                        Matchers.is(catDocuments.get(0).getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_personality",
                        Matchers.is(catDocuments.get(0).getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].favorite_foods[0].favoriteFood",
                        Matchers.is(foods.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].favorite_toys[0].favoriteToy",
                        Matchers.is(toys.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].favorite_toys[1].favoriteToy",
                        Matchers.is(toys.get(1).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].best_friend", Matchers.is(catDocuments.get(0).getBestFriend())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_name", Matchers.is(catDocuments.get(1).getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_gender",
                        Matchers.is(catDocuments.get(1).getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1]cat_age", Matchers.is(catDocuments.get(1).getCatAge())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1]cat_weight", Matchers.is(catDocuments.get(1).getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_breed",
                        Matchers.is(catDocuments.get(1).getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_color_pattern",
                        Matchers.is(catDocuments.get(1).getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_personality",
                        Matchers.is(catDocuments.get(1).getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].favorite_foods[0].favoriteFood",
                        Matchers.is(foods2.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].favorite_toys[0].favoriteToy",
                        Matchers.is(toys2.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].best_friend", Matchers.is(catDocuments.get(1).getBestFriend())))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseContent.contains(catDocuments.get(0).getCatName()));
        Assertions.assertTrue(responseContent.contains(catDocuments.get(1).getCatName()));
    }

    @Test
    public void getByIdsWhenNotFoundShouldReturn404() throws Exception {
        Set<UUID> catIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        Mockito.when(catDocumentService.getAllCatDocumentByIds(catIds)).thenReturn(null);
        String catIdsToString = catIds.stream().map(UUID::toString).collect(Collectors.joining(","));
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/cat-document/%s", catIdsToString))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getByNamesWhenFoundShouldReturnEntitiesWith200() throws Exception {
        List<String> catNames =
                List.of(catDocuments.get(0).getCatName(), catDocuments.get(1).getCatName());
        Mockito.when(catDocumentService.getAllCatDocumentByNames(catNames)).thenReturn(catDocuments);

        String foodJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteFoods());
        List<FavoriteFoodModel> foods = objectMapper.readValue(foodJson, new TypeReference<>() {});

        String toyJson = objectMapper.writeValueAsString(catDocuments.get(0).getFavoriteToys());
        List<FavoriteToyModel> toys = objectMapper.readValue(toyJson, new TypeReference<>() {});

        String foodJson2 = objectMapper.writeValueAsString(catDocuments.get(1).getFavoriteFoods());
        List<FavoriteFoodModel> foods2 = objectMapper.readValue(foodJson2, new TypeReference<>() {});

        String toyJson2 = objectMapper.writeValueAsString(catDocuments.get(1).getFavoriteToys());
        List<FavoriteToyModel> toys2 = objectMapper.readValue(toyJson2, new TypeReference<>() {});

        String catNamesToString = String.join(",", catNames);
        logger.debug(catNamesToString);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(String.format("/cat-document/by-names/%s", catNamesToString))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_name", Matchers.is(catDocuments.get(0).getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_gender",
                        Matchers.is(catDocuments.get(0).getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0]cat_age", Matchers.is(catDocuments.get(0).getCatAge())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0]cat_weight", Matchers.is(catDocuments.get(0).getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_breed",
                        Matchers.is(catDocuments.get(0).getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_color_pattern",
                        Matchers.is(catDocuments.get(0).getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].cat_personality",
                        Matchers.is(catDocuments.get(0).getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].favorite_foods[0].favoriteFood",
                        Matchers.is(foods.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].favorite_toys[0].favoriteToy",
                        Matchers.is(toys.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].favorite_toys[1].favoriteToy",
                        Matchers.is(toys.get(1).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].best_friend", Matchers.is(catDocuments.get(0).getBestFriend())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_name", Matchers.is(catDocuments.get(1).getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_gender",
                        Matchers.is(catDocuments.get(1).getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1]cat_age", Matchers.is(catDocuments.get(1).getCatAge())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1]cat_weight", Matchers.is(catDocuments.get(1).getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_breed",
                        Matchers.is(catDocuments.get(1).getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_color_pattern",
                        Matchers.is(catDocuments.get(1).getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].cat_personality",
                        Matchers.is(catDocuments.get(1).getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].favorite_foods[0].favoriteFood",
                        Matchers.is(foods2.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].favorite_toys[0].favoriteToy",
                        Matchers.is(toys2.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].best_friend", Matchers.is(catDocuments.get(1).getBestFriend())))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseContent.contains(catDocuments.get(0).getCatName()));
        Assertions.assertTrue(responseContent.contains(catDocuments.get(1).getCatName()));
    }

    @Test
    public void getByNamesWhenNotFoundShouldReturn404() throws Exception {
        List<String> catNames = List.of("SolNa", "ZonGzi");
        Mockito.when(catDocumentService.getAllCatDocumentByNames(catNames)).thenReturn(null);
        String catNamesToString = String.join(",", catNames);
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/cat-document/%s", catNamesToString))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // only test id is found scenario, the not found scenario is the same as getByIdWhenNotFoundShouldReturn404()
    @Test
    public void updateByIdWhenFoundShouldUpdateAndReturnEntityWith2xx() throws Exception {
        UUID catId = catDocuments.get(0).getCatId();
        CatDocument catDocumentPassedIn = catDocuments.get(1);

        String foodJson = objectMapper.writeValueAsString(catDocumentPassedIn.getFavoriteFoods());
        List<FavoriteFoodModel> foods = objectMapper.readValue(foodJson, new TypeReference<>() {});

        String toyJson = objectMapper.writeValueAsString(catDocumentPassedIn.getFavoriteToys());
        List<FavoriteToyModel> toys = objectMapper.readValue(toyJson, new TypeReference<>() {});

        Mockito.when(catDocumentService.updateCatDocumentById(catDocumentPassedIn, catId))
                .thenReturn(catDocumentPassedIn);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put(String.format("/cat-document/id/%s/update", catId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(catDocumentPassedIn)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cat_name", Matchers.is(catDocumentPassedIn.getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_gender",
                        Matchers.is(catDocumentPassedIn.getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cat_age", Matchers.is(catDocumentPassedIn.getCatAge())))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.cat_weight", Matchers.is(catDocumentPassedIn.getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_breed",
                        Matchers.is(catDocumentPassedIn.getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_color_pattern",
                        Matchers.is(catDocumentPassedIn.getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_personality",
                        Matchers.is(catDocumentPassedIn.getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_foods[0].favoriteFood",
                        Matchers.is(foods.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[0].favoriteToy",
                        Matchers.is(toys.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.best_friend", Matchers.is(catDocumentPassedIn.getBestFriend())))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseContent.contains(catDocumentPassedIn.getCatName()));
    }

    // only test name is found scenario, the not found scenario is the same as getByNameWhenNotFoundShouldReturn404()
    @Test
    public void updateByNameWhenFoundShouldUpdateAndReturnEntityWith2xx() throws Exception {
        String catName = catDocuments.get(0).getCatName();
        CatDocument catDocumentPassedIn = catDocuments.get(1);

        String foodJson = objectMapper.writeValueAsString(catDocumentPassedIn.getFavoriteFoods());
        List<FavoriteFoodModel> foods = objectMapper.readValue(foodJson, new TypeReference<>() {});

        String toyJson = objectMapper.writeValueAsString(catDocumentPassedIn.getFavoriteToys());
        List<FavoriteToyModel> toys = objectMapper.readValue(toyJson, new TypeReference<>() {});

        Mockito.when(catDocumentService.updateCatDocumentByName(catDocumentPassedIn, catName))
                .thenReturn(catDocumentPassedIn);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put(String.format("/cat-document/name/%s/update", catName))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(catDocumentPassedIn)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cat_name", Matchers.is(catDocumentPassedIn.getCatName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_gender",
                        Matchers.is(catDocumentPassedIn.getCatGender().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cat_age", Matchers.is(catDocumentPassedIn.getCatAge())))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.cat_weight", Matchers.is(catDocumentPassedIn.getCatWeight())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_breed",
                        Matchers.is(catDocumentPassedIn.getCatBreed().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_color_pattern",
                        Matchers.is(catDocumentPassedIn.getCatColorPattern().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.cat_personality",
                        Matchers.is(catDocumentPassedIn.getCatPersonality().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_foods[0].favoriteFood",
                        Matchers.is(foods.get(0).getFavoriteFood())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.favorite_toys[0].favoriteToy",
                        Matchers.is(toys.get(0).getFavoriteToy())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.best_friend", Matchers.is(catDocumentPassedIn.getBestFriend())))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseContent.contains(catDocumentPassedIn.getCatName()));
    }
}
