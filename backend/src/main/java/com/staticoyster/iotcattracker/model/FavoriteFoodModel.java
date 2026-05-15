package com.staticoyster.iotcattracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "favorite_food_model", schema = "iot_cat_tracker_schema")
public class FavoriteFoodModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "cat_id", nullable = false)
    private CatDocumentModel catDocumentModel;

    @Column(name = "favorite_food")
    private String favoriteFood;

    public FavoriteFoodModel() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CatDocumentModel getCatDocumentModel() {
        return catDocumentModel;
    }

    public void setCatDocumentModel(CatDocumentModel catDocumentModel) {
        this.catDocumentModel = catDocumentModel;
    }

    public String getFavoriteFood() {
        return favoriteFood;
    }

    public void setFavoriteFood(String favoriteFood) {
        this.favoriteFood = favoriteFood;
    }

    private FavoriteFoodModel(Builder builder) {
        id = builder.id;
        catDocumentModel = builder.catDocumentModel;
        favoriteFood = builder.favoriteFood;
    }

    public static final class Builder {
        private long id;
        private CatDocumentModel catDocumentModel;
        private String favoriteFood;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withId(long val) {
            id = val;
            return this;
        }

        public Builder withCatDocumentModel(CatDocumentModel val) {
            catDocumentModel = val;
            return this;
        }

        public Builder withFavoriteFood(String val) {
            favoriteFood = val;
            return this;
        }

        public FavoriteFoodModel build() {
            return new FavoriteFoodModel(this);
        }
    }

    @Override
    public String toString() {
        return "FavoriteFoodModel{"
                + "id=" + id
                + ", catDocumentModel=" + catDocumentModel
                + ", favoriteFood='" + favoriteFood
                + '\'' + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        FavoriteFoodModel that = (FavoriteFoodModel) object;
        return id == that.id
                && Objects.equals(catDocumentModel, that.catDocumentModel)
                && Objects.equals(favoriteFood, that.favoriteFood);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, catDocumentModel, favoriteFood);
    }
}
