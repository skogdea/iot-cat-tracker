package com.staticoyster.iotcattracker.mongo;

import com.staticoyster.iotcattracker.model.catdrinkwater.ProcessedSignalModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedSignalRepository extends MongoRepository<ProcessedSignalModel, Long> {

    ProcessedSignalModel findTopByOrderByTimeStampDesc();
}
