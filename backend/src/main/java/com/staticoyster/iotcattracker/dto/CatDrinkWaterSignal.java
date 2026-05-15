package com.staticoyster.iotcattracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCatDrinkWaterSignal.class)
@JsonDeserialize(as = ImmutableCatDrinkWaterSignal.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CatDrinkWaterSignal {

    @JsonProperty("signal_id")
    Long getSignalId();

    @JsonProperty("sensor_id")
    String getSensorId();

    @JsonProperty("location")
    String getLocation();

    @JsonProperty("timestamp")
    long getTimeStamp();
}
