package com.staticoyster.iotcattracker.repository;

import com.staticoyster.iotcattracker.model.CatDocumentModel;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CatDocumentRepository extends JpaRepository<CatDocumentModel, UUID> {

    @Query(
            value = "SELECT * FROM iot_cat_tracker_schema.cat_document_model WHERE LOWER(cat_name) = (:catName)",
            nativeQuery = true)
    Optional<CatDocumentModel> findByCatName(@Param("catName") String catName);

    @Query(
            value = "SELECT * FROM iot_cat_tracker_schema.cat_document_model WHERE LOWER(cat_name) IN (:catNames)",
            nativeQuery = true)
    List<CatDocumentModel> findByCatNameIn(@Param("catNames") List<String> catNames);

    List<CatDocumentModel> findByCatIdIn(Set<UUID> catIds);
}
