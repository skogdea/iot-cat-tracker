package com.staticoyster.iotcattracker.model.catdrinkwater;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ProcessedSignalModel {

    @Id
    private Long signalId;

    private long timeStamp;

    private boolean drunkWater;

    public ProcessedSignalModel() {}

    public Long getSignalId() {
        return signalId;
    }

    public void setSignalId(Long signalId) {
        this.signalId = signalId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isDrunkWater() {
        return drunkWater;
    }

    public void setDrunkWater(boolean drunkWater) {
        this.drunkWater = drunkWater;
    }

    private ProcessedSignalModel(Builder builder) {
        signalId = builder.signalId;
        timeStamp = builder.timeStamp;
        drunkWater = builder.drunkWater;
    }

    public static final class Builder {
        private Long signalId;
        private long timeStamp;
        private boolean drunkWater;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withSignalId(Long val) {
            signalId = val;
            return this;
        }

        public Builder withTimeStamp(long val) {
            timeStamp = val;
            return this;
        }

        public Builder withDrunkWater(boolean val) {
            drunkWater = val;
            return this;
        }

        public ProcessedSignalModel build() {
            return new ProcessedSignalModel(this);
        }
    }

    @Override
    public String toString() {
        return "ProcessedSignalModel{"
                + "signalId=" + signalId
                + ", timeStamp=" + timeStamp
                + ", drunkWater=" + drunkWater
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
        ProcessedSignalModel that = (ProcessedSignalModel) object;
        return drunkWater == that.drunkWater
                && Objects.equals(signalId, that.signalId)
                && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signalId, timeStamp, drunkWater);
    }
}
