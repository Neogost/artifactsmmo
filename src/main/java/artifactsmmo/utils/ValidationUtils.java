package artifactsmmo.utils;


import artifactsmmo.enums.CharacterSort;
import artifactsmmo.enums.CraftSkill;
import artifactsmmo.enums.Skill;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Map;
import ch.qos.logback.core.util.StringUtil;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern DROP_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[^\\s]+$");
    private static final Pattern ITEMCODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern CRAFT_MATERIAL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    private static final Pattern MONSTERCODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern CONTENTCODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    private ValidationUtils() {
        // Private constructor
    }

    public static int validatePageSize(int pageSize) {
        if (pageSize <= 0 || pageSize > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        return pageSize;
    }

    public static int validatePage(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        return page;
    }


    /**
     * Validates the provided drop code against a predefined pattern.
     * <p>
     * This method checks if the given drop code matches the required format defined by the regular expression pattern.
     * If the drop code does not match the pattern, an {@link IllegalArgumentException} is thrown with a descriptive message.
     * </p>
     *
     * @param drop the drop code to be validated
     * @return the original drop code if it is valid
     * @throws IllegalArgumentException if the drop code does not match the pattern defined by {@code DROP_PATTERN}
     * @see java.util.regex.Pattern
     */
    public static String validateDrop(String drop) {
        if (StringUtil.isNullOrEmpty(drop)) {
            return drop;
        }

        if (!DROP_PATTERN.matcher(drop).matches()) {
            throw new IllegalArgumentException(String.format("Drop should be in {} format.", DROP_PATTERN.pattern()));
        }
        return drop;
    }

    public static int validateMinLevel(int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("Level min must be greater than 0");
        }
        return level;
    }


    public static int validateMaxLevel(int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("Level max must be greater than 0");
        }
        return level;
    }

    public static void validateLevelsParam(int minLevel, int maxLevel) {
        if (minLevel > maxLevel) {
            throw new IllegalArgumentException("Level maximum must be greater than minimum Level");
        }
    }


    public static Skill validateSkillForResources(Skill skill) {
        if (skill == null) {
            return null;
        }

        if (Skill.MINING != skill
                && Skill.WOODCUTTING != skill
                && Skill.FISHING != skill) {
            throw new IllegalArgumentException(String.format("Skill must be one of : {}, {} or {}",
                    Skill.MINING.getValue(),
                    Skill.WOODCUTTING.getValue(),
                    Skill.FISHING.getValue()));
        }
        return skill;
    }

    public static String validateUsername(String username) {
        if (username.isEmpty() || username.isBlank()) {
            throw new IllegalArgumentException("Username must be defined");
        }
        if (username.length() < 6 || username.length() > 50) {
            throw new IllegalArgumentException("Username length must be between 6 and 50 characters");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(String.format("Username should be in {} format.", USERNAME_PATTERN.pattern()));
        }
        return username;
    }

    public static String validatePassword(String password) {
        if (password.isEmpty() || password.isBlank()) {
            throw new IllegalArgumentException("Password must be defined");
        }
        if (password.length() < 5 || password.length() > 50) {
            throw new IllegalArgumentException("Password length must be between 5 and 50 characters");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(String.format("Password should be in {} format.", PASSWORD_PATTERN.pattern()));
        }
        return password;
    }

    public static String validateItemCode(String code) {
        if (!ITEMCODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException(String.format("Item code should be in {} format.", ITEMCODE_PATTERN.pattern()));
        }
        return code;
    }

    public static String validateMonsterCode(String code) {
        if (!MONSTERCODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException(String.format("Monster code should be in {} format.", MONSTERCODE_PATTERN.pattern()));
        }
        return code;
    }

    public static int validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException(String.format("Quantity should be greater than 0", MONSTERCODE_PATTERN.pattern()));
        }
        return quantity;
    }

    public static int validateQuantityForGrandExchangeTransaction(int quantity) {
        if (quantity < 1 || quantity > 50) {
            throw new IllegalArgumentException(String.format("Quantity should be between 1 and 50", MONSTERCODE_PATTERN.pattern()));
        }
        return quantity;
    }

    public static int validatePrice(int price) {
        if (price < 1) {
            throw new IllegalArgumentException(String.format("Price should be greater than 0", MONSTERCODE_PATTERN.pattern()));
        }
        return price;
    }

    public static String validateContentCode(String code) {
        if(code == null) {
            return null;
        }
        if (!CONTENTCODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException(String.format("Monster code should be in {} format.", CONTENTCODE_PATTERN.pattern()));
        }
        return code;
    }

    public static String validateCraftMaterialCode(String code) {
        if(code == null) {
            return null;
        }
        if (!CRAFT_MATERIAL_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException(String.format("Monster code should be in {} format.", CRAFT_MATERIAL_PATTERN.pattern()));
        }
        return code;
    }


    public static String validateCharacterName(String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(String.format("Name should be in {} format.", NAME_PATTERN.pattern()));
        }
        return name;
    }

    public static CharacterSort validateCharacterSort(CharacterSort sort) {
        if (sort == null) {
            return null;
        }
        if (sort != CharacterSort.FISHING
                && sort != CharacterSort.COOKING
                && sort != CharacterSort.MINING
                && sort != CharacterSort.GOLD
                && sort != CharacterSort.WOODCUTTING
                && sort != CharacterSort.GEARCRAFTING
                && sort != CharacterSort.JEWELRYCRAFTING
                && sort != CharacterSort.WEAPONCRAFTING) {
            throw new IllegalArgumentException(String.format("Sorting not allowed with {} .", sort.getValue()));
        }
        return sort;
    }

    public static boolean validateIsRequireMovement(Character character, Map map) {
        return character.getX() != map.getX() || character.getY() != map.getY();
    }
}

