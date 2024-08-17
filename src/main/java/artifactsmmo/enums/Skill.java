package artifactsmmo.enums;

import lombok.Getter;

@Getter
public enum Skill {

    WEAPONCRAFTING("weaponcrafting"),
    GEARCRAFTING("gearcrafting"),
    JEWELRYCRAFTING("jewelrycrafting"),
    COOKING("cooking"),
    WOODCUTTING("woodcutting"),
    MINING("mining"),
    FISHING("fishing");

    private final String value;

    Skill(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}
