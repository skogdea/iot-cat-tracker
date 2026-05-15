package com.staticoyster.iotcattracker.model.catdrinkwater;

import jakarta.persistence.Id;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CatDrinkWaterSignalModel {

    @Id
    private Long signalId;

    private String sensorId;

    private String location;

    private long timeStamp;

    public CatDrinkWaterSignalModel() {}

    public Long getSignalId() {
        return signalId;
    }

    public void setSignalId(Long signalId) {
        this.signalId = signalId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private CatDrinkWaterSignalModel(Builder builder) {
        signalId = builder.signalId;
        sensorId = builder.sensorId;
        location = builder.location;
        timeStamp = builder.timeStamp;
    }

    public static final class Builder {
        private Long signalId;
        private String sensorId;
        private String location;
        private long timeStamp;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withSignalId(Long val) {
            signalId = val;
            return this;
        }

        public Builder withSensorId(String val) {
            sensorId = val;
            return this;
        }

        public Builder withLocation(String val) {
            location = val;
            return this;
        }

        public Builder withTimeStamp(long val) {
            timeStamp = val;
            return this;
        }

        public CatDrinkWaterSignalModel build() {
            return new CatDrinkWaterSignalModel(this);
        }
    }

    @Override
    public String toString() {
        return "CatDrinkWaterSignalModel{"
                + "signalId=" + signalId
                + ", sensorId='" + sensorId + '\''
                + ", location='" + location + '\''
                + ", timeStamp=" + timeStamp
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
        CatDrinkWaterSignalModel that = (CatDrinkWaterSignalModel) object;
        return Objects.equals(signalId, that.signalId)
                && Objects.equals(sensorId, that.sensorId)
                && Objects.equals(location, that.location)
                && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signalId, sensorId, location, timeStamp);
    }
}
