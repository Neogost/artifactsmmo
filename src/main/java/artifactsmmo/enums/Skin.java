package artifactsmmo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Skin {
    MEN1("men1"),
    MEN2("men2"),
    MEN3("men3"),
    WOMEN1("women1"),
    WOMEN2("women2"),
    WOMEN3("women3");

    private final String value;

    public static Skin fromValue(String value) {
        for (Skin skin : Skin.values()) {
            if (skin.value.equals(value)) {
                return skin;
            }
        }
        throw new IllegalArgumentException("Valeur non valide pour Skin: " + value);
    }
}
