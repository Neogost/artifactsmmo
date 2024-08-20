package artifactsmmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum TaskType {

    MONSTERS("monsters"),
    RESOURCES("resources"),
    CRAFTS("crafts");

    private final String value;


    @JsonCreator
    public static TaskType fromValue(String value) {
        for (TaskType type : TaskType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown content type: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

