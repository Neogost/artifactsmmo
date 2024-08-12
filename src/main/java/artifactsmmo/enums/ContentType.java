package artifactsmmo.enums;

public enum ContentType {
    MONSTER("monster"),
    RESOURCE("resource"),
    WORKSHOP("workshop"),
    BANK("bank"),
    GRAND_EXCHANGE("grand_exchange"),
    TASKS_MASTER("tasks_master");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ContentType fromValue(String value) {
        for (ContentType type : ContentType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown content type: " + value);
    }
}
