package artifactsmmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ContentType {
    MONSTER("monster"),
    RESOURCE("resource"),
    WORKSHOP("workshop"),
    BANK("bank"),
    GRAND_EXCHANGE("grand_exchange"),
    TASKS_MASTER("tasks_master");

    private final String value;


    @JsonCreator
    public static ContentType fromValue(String value) {
        for (ContentType type : ContentType.values()) {
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
