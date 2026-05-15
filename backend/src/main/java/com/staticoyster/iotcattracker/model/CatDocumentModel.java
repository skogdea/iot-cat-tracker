package com.staticoyster.iotcattracker.model;

import com.staticoyster.iotcattracker.enums.CatBreed;
import com.staticoyster.iotcattracker.enums.CatColorPattern;
import com.staticoyster.iotcattracker.enums.CatGender;
import com.staticoyster.iotcattracker.enums.CatPersonality;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "cat_document_model", schema = "iot_cat_tracker_schema")
public class CatDocumentModel {

    @Id
    @Column(name = "cat_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID catId;

    @Column(name = "cat_name")
    private String catName;

    @Column(name = "cat_gender")
    private CatGender catGender;

    @Column(name = "cat_age")
    private int catAge;

    // Units: kg
    @Column(name = "cat_weight")
    private double catWeight;

    @Column(name = "cat_breed")
    private CatBreed catBreed;

    @Column(name = "cat_color_pattern")
    private CatColorPattern catColorPattern;

    @Column(name = "cat_personality")
    private CatPersonality catPersonality;

    @OneToMany(mappedBy = "catDocumentModel")
    private List<FavoriteFoodModel> favoriteFoods;

    @OneToMany(mappedBy = "catDocumentModel")
    private List<FavoriteToyModel> favoriteToys;

    @Column(name = "best_friend")
    private String bestFriend;

    public CatDocumentModel() {}

    public UUID getCatId() {
        return catId;
    }

    public void setCatId(UUID catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public CatGender getCatGender() {
        return catGender;
    }

    public void setCatGender(CatGender catGender) {
        this.catGender = catGender;
    }

    public int getCatAge() {
        return catAge;
    }

    public void setCatAge(int catAge) {
        this.catAge = catAge;
    }

    public double getCatWeight() {
        return catWeight;
    }

    public void setCatWeight(double catWeight) {
        this.catWeight = catWeight;
    }

    public CatBreed getCatBreed() {
        return catBreed;
    }

    public void setCatBreed(CatBreed catBreed) {
        this.catBreed = catBreed;
    }

    public CatColorPattern getCatColorPattern() {
        return catColorPattern;
    }

    public void setCatColorPattern(CatColorPattern catColorPattern) {
        this.catColorPattern = catColorPattern;
    }

    public CatPersonality getCatPersonality() {
        return catPersonality;
    }

    public void setCatPersonality(CatPersonality catPersonality) {
        this.catPersonality = catPersonality;
    }

    public List<FavoriteFoodModel> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(List<FavoriteFoodModel> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    public List<FavoriteToyModel> getFavoriteToys() {
        return favoriteToys;
    }

    public void setFavoriteToys(List<FavoriteToyModel> favoriteToys) {
        this.favoriteToys = favoriteToys;
    }

    public String getBestFriend() {
        return bestFriend;
    }

    public void setBestFriend(String bestFriend) {
        this.bestFriend = bestFriend;
    }

    private CatDocumentModel(Builder builder) {
        catId = builder.catId;
        catName = builder.catName;
        catGender = builder.catGender;
        catAge = builder.catAge;
        catWeight = builder.catWeight;
        catBreed = builder.catBreed;
        catColorPattern = builder.catColorPattern;
        catPersonality = builder.catPersonality;
        favoriteFoods = builder.favoriteFoods;
        favoriteToys = builder.favoriteToys;
        bestFriend = builder.bestFriend;
    }

    public static final class Builder {

        private UUID catId;
        private String catName;
        private CatGender catGender;
        private int catAge;
        private double catWeight;
        private CatBreed catBreed;
        private CatColorPattern catColorPattern;
        private CatPersonality catPersonality;
        private List<FavoriteFoodModel> favoriteFoods;
        private List<FavoriteToyModel> favoriteToys;
        private String bestFriend;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withCatId(UUID val) {
            catId = val;
            return this;
        }

        public Builder withCatName(String val) {
            catName = val;
            return this;
        }

        public Builder withCatGender(CatGender val) {
            catGender = val;
            return this;
        }

        public Builder withCatAge(int val) {
            catAge = val;
            return this;
        }

        public Builder withCatWeight(double val) {
            catWeight = val;
            return this;
        }

        public Builder withCatBreed(CatBreed val) {
            catBreed = val;
            return this;
        }

        public Builder withCatColorPattern(CatColorPattern val) {
            catColorPattern = val;
            return this;
        }

        public Builder withCatPersonality(CatPersonality val) {
            catPersonality = val;
            return this;
        }

        public Builder withFavoriteFoods(List<FavoriteFoodModel> val) {
            favoriteFoods = val;
            return this;
        }

        public Builder withFavoriteToys(List<FavoriteToyModel> val) {
            favoriteToys = val;
            return this;
        }

        public Builder withBestFriend(String val) {
            bestFriend = val;
            return this;
        }

        public CatDocumentModel build() {
            return new CatDocumentModel(this);
        }
    }

    @Override
    public String toString() {
        return "CatDocumentModel{"
                + "catId=" + catId
                + ", catName='" + catName + '\''
                + ", catGender=" + catGender
                + ", catAge=" + catAge
                + ", catWeight=" + catWeight
                + ", catBreed=" + catBreed
                + ", catColorPattern=" + catColorPattern
                + ", catPersonality=" + catPersonality
                + ", favoriteFoods=" + favoriteFoods
                + ", favoriteToys=" + favoriteToys
                + ", bestFriend='" + bestFriend + '\''
                + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        CatDocumentModel that = (CatDocumentModel) object;
        return catAge == that.catAge
                && Double.compare(catWeight, that.catWeight) == 0
                && Objects.equals(catId, that.catId)
                && Objects.equals(catName, that.catName)
                && catGender == that.catGender
                && catBreed == that.catBreed
                && catColorPattern == that.catColorPattern
                && catPersonality == that.catPersonality
                && Objects.equals(favoriteFoods, that.favoriteFoods)
                && Objects.equals(favoriteToys, that.favoriteToys)
                && Objects.equals(bestFriend, that.bestFriend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                catId,
                catName,
                catGender,
                catAge,
                catWeight,
                catBreed,
                catColorPattern,
                catPersonality,
                favoriteFoods,
                favoriteToys,
                bestFriend);
    }
}
