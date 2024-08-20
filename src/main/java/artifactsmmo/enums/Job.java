package artifactsmmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public enum Job implements Serializable {

    WEAPONCRAFTING("weaponcrafting"),
    GEARCRAFTING("gearcrafting"),
    JEWELRYCRAFTING("jewelrycrafting"),
    COOKING("cooking"),
    WOODCUTTING("woodcutting"),
    MINING("mining"),
    FIGHTER("fighter"),
    TASK_FIGHTER("task_fighter"),
    FISHING("fishing");

    private final String value;
    private static final long serialVersionUID = 1L;

    @JsonCreator
    public static Job fromValue(String value) {
        for (Job skill : Job.values()) {
            if (skill.value.equalsIgnoreCase(value)) {
                return skill;
            }
        }
        throw new IllegalArgumentException("Unknown skill : " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
