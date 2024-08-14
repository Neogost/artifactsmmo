package artifactsmmo.models.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Character {
    @JsonProperty("name")
    private String name;

    @JsonProperty("skin")
    private String skin;

    @JsonProperty("level")
    private int level;

    @JsonProperty("xp")
    private int xp;

    @JsonProperty("max_xp")
    private int maxXp;

    @JsonProperty("total_xp")
    private int totalXp;

    @JsonProperty("gold")
    private int gold;

    @JsonProperty("speed")
    private int speed;

    @JsonProperty("mining_level")
    private int miningLevel;

    @JsonProperty("mining_xp")
    private int miningXp;

    @JsonProperty("mining_max_xp")
    private int miningMaxXp;

    @JsonProperty("woodcutting_level")
    private int woodcuttingLevel;

    @JsonProperty("woodcutting_xp")
    private int woodcuttingXp;

    @JsonProperty("woodcutting_max_xp")
    private int woodcuttingMaxXp;

    @JsonProperty("fishing_level")
    private int fishingLevel;

    @JsonProperty("fishing_xp")
    private int fishingXp;

    @JsonProperty("fishing_max_xp")
    private int fishingMaxXp;

    @JsonProperty("weaponcrafting_level")
    private int weaponcraftingLevel;

    @JsonProperty("weaponcrafting_xp")
    private int weaponcraftingXp;

    @JsonProperty("weaponcrafting_max_xp")
    private int weaponcraftingMaxXp;

    @JsonProperty("gearcrafting_level")
    private int gearcraftingLevel;

    @JsonProperty("gearcrafting_xp")
    private int gearcraftingXp;

    @JsonProperty("gearcrafting_max_xp")
    private int gearcraftingMaxXp;

    @JsonProperty("jewelrycrafting_level")
    private int jewelrycraftingLevel;

    @JsonProperty("jewelrycrafting_xp")
    private int jewelrycraftingXp;

    @JsonProperty("jewelrycrafting_max_xp")
    private int jewelrycraftingMaxXp;

    @JsonProperty("cooking_level")
    private int cookingLevel;

    @JsonProperty("cooking_xp")
    private int cookingXp;

    @JsonProperty("cooking_max_xp")
    private int cookingMaxXp;

    @JsonProperty("hp")
    private int hp;

    @JsonProperty("haste")
    private int haste;

    @JsonProperty("critical_strike")
    private int criticalStrike;

    @JsonProperty("stamina")
    private int stamina;

    @JsonProperty("attack_fire")
    private int attackFire;

    @JsonProperty("attack_earth")
    private int attackEarth;

    @JsonProperty("attack_water")
    private int attackWater;

    @JsonProperty("attack_air")
    private int attackAir;

    @JsonProperty("dmg_fire")
    private int dmgFire;

    @JsonProperty("dmg_earth")
    private int dmgEarth;

    @JsonProperty("dmg_water")
    private int dmgWater;

    @JsonProperty("dmg_air")
    private int dmgAir;

    @JsonProperty("res_fire")
    private int resFire;

    @JsonProperty("res_earth")
    private int resEarth;

    @JsonProperty("res_water")
    private int resWater;

    @JsonProperty("res_air")
    private int resAir;

    @JsonProperty("x")
    private int x;

    @JsonProperty("y")
    private int y;

    @JsonProperty("cooldown")
    private int cooldown;

    @JsonProperty("cooldown_expiration")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime cooldownExpiration;

    @JsonProperty("weapon_slot")
    private String weaponSlot;

    @JsonProperty("shield_slot")
    private String shieldSlot;

    @JsonProperty("helmet_slot")
    private String helmetSlot;

    @JsonProperty("body_armor_slot")
    private String bodyArmorSlot;

    @JsonProperty("leg_armor_slot")
    private String legArmorSlot;

    @JsonProperty("boots_slot")
    private String bootsSlot;

    @JsonProperty("ring1_slot")
    private String ring1Slot;

    @JsonProperty("ring2_slot")
    private String ring2Slot;

    @JsonProperty("amulet_slot")
    private String amuletSlot;

    @JsonProperty("artifact1_slot")
    private String artifact1Slot;

    @JsonProperty("artifact2_slot")
    private String artifact2Slot;

    @JsonProperty("artifact3_slot")
    private String artifact3Slot;

    @JsonProperty("consumable1_slot")
    private String consumable1Slot;

    @JsonProperty("consumable1_slot_quantity")
    private int consumable1SlotQuantity;

    @JsonProperty("consumable2_slot")
    private String consumable2Slot;

    @JsonProperty("consumable2_slot_quantity")
    private int consumable2SlotQuantity;

    @JsonProperty("task")
    private String task;

    @JsonProperty("task_type")
    private String taskType;

    @JsonProperty("task_progress")
    private int taskProgress;

    @JsonProperty("task_total")
    private int taskTotal;

    @JsonProperty("inventory_max_items")
    private int inventoryMaxItems;

    @JsonProperty("inventory")
    private List<ItemInventory> inventory;


    public boolean onMap(Map map) {
        if (map == null)
            return false;

        return x == map.getX() && y == map.getY();
    }

    public boolean inventoryFull() {
        int quantity = 0;
        for(ItemInventory itemInventory : inventory) {
            quantity += itemInventory.getQuantity();
        }
        return quantity >= inventoryMaxItems;
    }

    public boolean taskInProgress() {
        return taskProgress < taskTotal;
    }

    public boolean taskComplete() {
        return taskProgress == taskTotal;
    }
}