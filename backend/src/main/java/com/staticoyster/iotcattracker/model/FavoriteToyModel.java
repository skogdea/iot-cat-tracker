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
@Table(name = "favorite_toy_model", schema = "iot_cat_tracker_schema")
public class FavoriteToyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "cat_id", nullable = false)
    private CatDocumentModel catDocumentModel;

    @Column(name = "favorite_toy")
    private String favoriteToy;

    public FavoriteToyModel() {}

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

    public String getFavoriteToy() {
        return favoriteToy;
    }

    public void setFavoriteToy(String favoriteToy) {
        this.favoriteToy = favoriteToy;
    }

    private FavoriteToyModel(Builder builder) {
        id = builder.id;
        catDocumentModel = builder.catDocumentModel;
        favoriteToy = builder.favoriteToy;
    }

    public static final class Builder {
        private long id;
        private CatDocumentModel catDocumentModel;
        private String favoriteToy;

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

        public Builder withFavoriteToy(String val) {
            favoriteToy = val;
            return this;
        }

        public FavoriteToyModel build() {
            return new FavoriteToyModel(this);
        }
    }

    @Override
    public String toString() {
        return "FavoriteToyModel{"
                + "id=" + id
                + ", catDocumentModel=" + catDocumentModel
                + ", favoriteToy='" + favoriteToy
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
        FavoriteToyModel that = (FavoriteToyModel) object;
        return id == that.id
                && Objects.equals(catDocumentModel, that.catDocumentModel)
                && Objects.equals(favoriteToy, that.favoriteToy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, catDocumentModel, favoriteToy);
    }
}
