package com.staticoyster.iotcattracker.mongo;

import com.staticoyster.iotcattracker.model.catdrinkwater.ProcessedSignalModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HasDrunkWaterRepository extends MongoRepository<ProcessedSignalModel, Long> {

    List<ProcessedSignalModel> findByTimeStampBetween(long start, long end);
}
