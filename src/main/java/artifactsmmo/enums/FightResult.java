package artifactsmmo.enums;

public enum FightResult {

    WIN("win"),
    LOSE("lose");


    private final String value;

    FightResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static FightResult fromValue(String value) {
        for (FightResult type : FightResult.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown content type: " + value);
    }

}
