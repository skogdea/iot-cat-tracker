package com.staticoyster.iotcattracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEmailContent.class)
@JsonDeserialize(as = ImmutableEmailContent.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface EmailContent {

    @JsonProperty("subject")
    String getSubject();

    @JsonProperty("text")
    String getText();
}
