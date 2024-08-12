package artifactsmmo.enums;

import lombok.Getter;

@Getter
public enum CraftSkill {

    WEAPONCRAFTING("weaponcrafting"),
    GEARCRAFTING("gearcrafting"),
    JEWELRYCRAFTING("jewelrycrafting"),
    COOKING("cooking"),
    WOODCUTTING("woodcutting"),
    MINING("mining");

    private final String value;

    CraftSkill(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}
