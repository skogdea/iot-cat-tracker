package com.staticoyster.iotcattracker.mongo;

import com.staticoyster.iotcattracker.model.catdrinkwater.ProcessedSignalModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedSignalRepository extends MongoRepository<ProcessedSignalModel, Long> {

    ProcessedSignalModel findTopByOrderByTimeStampDesc();

    ProcessedSignalModel findTopByDrunkWaterTrueOrderByTimeStampDesc();

    List<ProcessedSignalModel> findByTimeStampBetweenOrderByTimeStampAsc(long start, long end);
}
