package com.staticoyster.iotcattracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.staticoyster.iotcattracker.enums.CatBreed;
import com.staticoyster.iotcattracker.enums.CatColorPattern;
import com.staticoyster.iotcattracker.enums.CatGender;
import com.staticoyster.iotcattracker.enums.CatPersonality;
import com.staticoyster.iotcattracker.model.FavoriteFoodModel;
import com.staticoyster.iotcattracker.model.FavoriteToyModel;
import java.util.List;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCatDocument.class)
@JsonDeserialize(as = ImmutableCatDocument.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CatDocument {

    @JsonProperty("cat_id")
    UUID getCatId();

    @JsonProperty("cat_name")
    String getCatName();

    @JsonProperty("cat_gender")
    CatGender getCatGender();

    @JsonProperty("cat_age")
    int getCatAge();

    // Units: kg
    @JsonProperty("cat_weight")
    double getCatWeight();

    @JsonProperty("cat_breed")
    CatBreed getCatBreed();

    @JsonProperty("cat_color_pattern")
    CatColorPattern getCatColorPattern();

    @JsonProperty("cat_personality")
    CatPersonality getCatPersonality();

    @JsonProperty("favorite_foods")
    List<FavoriteFoodModel> getFavoriteFoods();

    @JsonProperty("favorite_toys")
    List<FavoriteToyModel> getFavoriteToys();

    @JsonProperty("best_friend")
    String getBestFriend();
}
