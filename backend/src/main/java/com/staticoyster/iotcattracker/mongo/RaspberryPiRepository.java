package com.staticoyster.iotcattracker.mongo;

import com.staticoyster.iotcattracker.model.catdrinkwater.CatDrinkWaterSignalModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RaspberryPiRepository extends MongoRepository<CatDrinkWaterSignalModel, Long> {

    CatDrinkWaterSignalModel findTopByOrderByTimeStampAsc();

    List<CatDrinkWaterSignalModel> findByTimeStampAfter(long timeStamp);
}
