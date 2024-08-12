package artifactsmmo.enums;

public enum Job {

    WEAPONCRAFTING("weaponcrafting"),
    GEARCRAFTING("gearcrafting"),
    JEWELRYCRAFTING("jewelrycrafting"),
    COOKING("cooking"),
    WOODCUTTING("woodcutting"),
    MINING("mining"),
    FIGHTER("fighter");

    private final String value;

    Job(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}
