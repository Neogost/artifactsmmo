package artifactsmmo.enums;

import lombok.Getter;

@Getter
public enum Type {
    CONSUMABLE("consumable"),
    BODY_ARMOR("body_armor"),
    WEAPON("weapon"),
    RESOURCE("resource"),
    LEG_ARMOR("leg_armor"),
    HELMET("helmet"),
    BOOTS("boots"),
    SHIELD("shield"),
    AMULET("amulet"),
    RING("ring");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
