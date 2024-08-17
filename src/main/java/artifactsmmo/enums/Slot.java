package artifactsmmo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Représente les différents slots d'équipement disponibles pour un item, avec des valeurs associées sous forme de chaînes de caractères.
 * <p>
 * Cette énumération définit les slots autorisés pour l'équipement d'un personnage. Chaque slot est associé à une valeur string
 * correspondant à son type spécifique.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum Slot {
    WEAPON("weapon"),
    SHIELD("shield"),
    HELMET("helmet"),
    BODY_ARMOR("body_armor"),
    LEG_ARMOR("leg_armor"),
    BOOTS("boots"),
    RING1("ring1"),
    RING2("ring2"),
    AMULET("amulet"),
    ARTIFACT1("artifact1"),
    ARTIFACT2("artifact2"),
    ARTIFACT3("artifact3"),
    CONSUMABLE1("consumable1"),
    CONSUMABLE2("consumable2");

    private final String value;

    public static Slot fromValue(String value) {
        for (Slot slot : Slot.values()) {
            if (slot.value.equals(value)) {
                return slot;
            }
        }
        throw new IllegalArgumentException("Valeur non valide pour Slot: " + value);
    }
}

