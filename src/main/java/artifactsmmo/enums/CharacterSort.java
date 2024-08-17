package artifactsmmo.enums;

import lombok.Getter;

/**
 * Énumération représentant les types de tri autorisés pour les personnages.
 */
@Getter
public enum CharacterSort {
    WOODCUTTING("woodcutting"),
    MINING("mining"),
    FISHING("fishing"),
    WEAPONCRAFTING("weaponcrafting"),
    GEARCRAFTING("gearcrafting"),
    JEWELRYCRAFTING("jewelrycrafting"),
    COOKING("cooking"),
    GOLD("gold");

    private final String value;

    /**
     * Constructeur pour initialiser la valeur String associée à chaque constante.
     *
     * @param value la valeur en texte associée à l'énumération.
     */
    CharacterSort(String value) {
        this.value = value;
    }

    /**
     * Obtient une constante d'énumération basée sur la valeur String fournie.
     *
     * @param value la valeur en texte pour laquelle obtenir l'énumération.
     * @return la constante d'énumération correspondante.
     * @throws IllegalArgumentException si aucune constante ne correspond à la valeur fournie.
     */
    public static CharacterSort fromValue(String value) {
        for (CharacterSort sort : CharacterSort.values()) {
            if (sort.value.equalsIgnoreCase(value)) {
                return sort;
            }
        }
        throw new IllegalArgumentException("Aucun tri correspondant pour la valeur : " + value);
    }
}
