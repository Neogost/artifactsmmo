package artifactsmmo.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum ErrorCode {
    MAP_NOT_FOUND(404, "Map not found"),
    MAPS_NOT_FOUND(404, "Maps not found"),
    RESOURCE_NOT_FOUND(404, "Resource not found"),
    RESOURCES_NOT_FOUND(404, "Resources not found"),
    ITEM_NOT_FOUND(404,"Item not found." ),
    ITEMS_NOT_FOUND(404,"Items not found." ),
    MONSTER_NOT_FOUND(404,"Monster not found." ),
    MONSTERS_NOT_FOUND(404,"Monsters not found." ),
    EVENTS_NOT_FOUND(404,"Events not found." ),
    GE_ITEM_NOT_FOUND(404,"Item not found." ),
    GE_ITEMS_NOT_FOUND(404,"Items not found." ),
    CHARACTER_NOT_FOUND(404,"Character not found." ),
    CHARACTERS_NOT_FOUND(404,"Characters not found." ),

    TOKEN_GENERATION_FAIL(455,"Token generation failed." ),
    USERNAME_ALREADY_USED(456, "Username already used."),
    EMAIL_ALREADY_USED(457, "Email already used."),
    USE_DIFFERENT_PASSWORD(458, "Use a different password."),

    TRANSACTION_ALREADY_IN_PROGRESS(461,"A transaction is already in progress with this item/your golds in your bank."),
    MISSING_ITEM(478, "Missing item or insufficient quantity in your inventory."),
    ACTION_ALREADY_IN_PROGRESS(486, "An action is already in progress by your character."),
    CHARACTER_ALREADY_HAS_TASK(489, "Character already has a task."),

    CHARACTER_INVENTORY_FULL(497, "Character inventory is full."),

    NAME_ALREADY_USED(494, "Name already used."),
    MAX_CHARACTER(495, "Maximum characters reached on your account."),
    CHARACTER_AT_DESTINATION(490, "Character already at destination"),
    CHARACTER_DELETE_NOT_FOUND(498, "Character not found"),
    CHARACTER_IN_COOLDOWN(499, "Character in cooldown"),

    ENTITY_NOT_FOUND(598, "Entity not found on this map.");

    private final int code;
    private final String reason;

    ErrorCode(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.code == code) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Unknown error code: " + code);
    }
}
