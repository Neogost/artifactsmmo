package artifactsmmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemSubType {


    PLANK("plank"),
    BAR("bar"),
    MOB("mob");

    private final String value;
    @JsonCreator
    public static ItemSubType fromValue(String value) {
        for (ItemSubType skill : ItemSubType.values()) {
            if (skill.value.equalsIgnoreCase(value)) {
                return skill;
            }
        }
        throw new IllegalArgumentException("Unknown item type : " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
