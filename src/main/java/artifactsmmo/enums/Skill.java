package artifactsmmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum Skill {

    WEAPONCRAFTING("weaponcrafting"),
    GEARCRAFTING("gearcrafting"),
    JEWELRYCRAFTING("jewelrycrafting"),
    COOKING("cooking"),
    WOODCUTTING("woodcutting"),
    MINING("mining"),
    FISHING("fishing");

    private final String value;

    @JsonCreator
    public static Skill fromValue(String value) {
        for (Skill skill : Skill.values()) {
            if (skill.value.equalsIgnoreCase(value)) {
                return skill;
            }
        }
        throw new IllegalArgumentException("Unknown skill : " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
