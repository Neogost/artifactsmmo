package artifactsmmo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ItemType {
    CONSUMABLE("consumable"),
    BODY_ARMOR("body_armor"),
    WEAPON("weapon"),
    RESOURCE("resource"),
    LEG_ARMOR("leg_armor"),
    HELMET("helmet"),
    BOOTS("boots"),
    SHIELD("shield"),
    AMULET("amulet"),
    RING("ring"),
    ARTIFACT("artifact"),
    CURRENCY("currency");

    private final String value;


    @JsonCreator
    public static ItemType fromValue(String value) {
        for (ItemType skill : ItemType.values()) {
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
