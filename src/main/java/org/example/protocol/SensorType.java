package org.example.protocol;

import java.util.Arrays;

public enum SensorType {
    UNKNOWN(0),
    TEMPERATURE(1),
    AIR_HUMIDITY(2),
    SOIL_MOISTURE(3);

    private final int value;

    SensorType(int value) {
        this.value = value;
    }

    int toInt() {
        return value;
    }

    static SensorType from(int value) {
        return Arrays.stream(SensorType.values())
                .filter((variant) -> variant.toInt() == value)
                .findFirst().orElse(SensorType.UNKNOWN);
    }
}
