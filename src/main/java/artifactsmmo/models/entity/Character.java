package artifactsmmo.models.entity;


import artifactsmmo.controllers.CharacterController;
import artifactsmmo.enums.TaskType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    private  static final Logger LOGGER = LoggerFactory.getLogger(Character.class);
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
    private TaskType taskType;

    @JsonProperty("task_progress")
    private int taskProgress;

    @JsonProperty("task_total")
    private int taskTotal;

    @JsonProperty("inventory_max_items")
    private int inventoryMaxItems;

    @JsonProperty("inventory")
    private List<ItemInventory> inventory;

    public boolean simulateFight(Monster monster) {
        double windDamage = (attackAir + (attackAir * (dmgAir * 0.01))) * (1 - monster.getResAir() * 0.01);
        double fireDamage = (attackFire + (attackFire * (dmgFire * 0.01))) * (1 - monster.getResFire() * 0.01);
        double earthDamage = (attackEarth + (attackEarth * (dmgEarth * 0.01))) * (1 - monster.getResEarth() * 0.01);
        double waterDamage = (attackWater + (attackWater * (dmgWater * 0.01))) * (1 - monster.getResWater() * 0.01);

        double monsterWindDamage = monster.getAttackAir() * (1 + resWater);
        double monsterFireDamage = monster.getAttackFire() * (1 + resFire);
        double monsterEarthDamage = monster.getAttackEarth() * (1 + resEarth);
        double monsterWaterDamage = monster.getAttackWater() * (1 + resWater);

        double damageDeal = earthDamage + windDamage + waterDamage + fireDamage;
        double monsterDamageDeal = monsterEarthDamage + monsterWindDamage + monsterFireDamage + monsterWaterDamage;

        double nbTurn = Math.floor(monster.getHp() / damageDeal);
        double monsterNbTurn = Math.floor(hp / monsterDamageDeal);
        LOGGER.info("Simulation : Character {} lvl {} ({} hp) fight {} level {} monster ({} hp) = {} deal {} vs {} deal {}. {} need {} to win, {} need {} to win",
        name, level, hp, monster.getName(),monster.getLevel(), monster.getHp(), name, damageDeal, monster.getName(), monsterDamageDeal,name, nbTurn, monster.getName(), monsterNbTurn);
       return nbTurn < monsterNbTurn;
    }

    /**
     * Vérifie si une tâche est assignée.
     *
     * <p>Cette méthode détermine si une tâche est actuellement assignée en vérifiant si la
     * variable de tâche n'est pas null. Elle retourne {@code true} si une tâche est présente,
     * sinon elle retourne {@code false}.</p>
     *
     * @return {@code true} si une tâche est présente, {@code false} sinon.
     */
    public boolean haveTask() {
        return task != null;
    }

    /**
     * Vérifie si les coordonnées actuelles correspondent à celles de la carte spécifiée.
     *
     * <p>Cette méthode détermine si l'objet actuel se trouve sur la carte spécifiée
     * en comparant ses coordonnées avec celles de la carte donnée. Si la carte passée
     * en paramètre est nulle, la méthode retourne {@code false}.</p>
     *
     * @param map la carte à vérifier contre les coordonnées actuelles.
     * @return {@code true} si les coordonnées actuelles correspondent à celles de la carte donnée,
     * {@code false} si les coordonnées ne correspondent pas ou si la carte est nulle.
     */
    public boolean onMap(Map map) {
        if (map == null)
            return false;

        return x == map.getX() && y == map.getY();
    }

    /**
     * Vérifie si l'inventaire est plein.
     *
     * <p>Cette méthode calcule le nombre total d'objets dans l'inventaire en
     * additionnant les quantités de chaque objet présent dans l'inventaire.
     * Si ce total est supérieur ou égal à la capacité maximale de l'inventaire
     * ({@code inventoryMaxItems}), la méthode retourne {@code true}, indiquant que
     * l'inventaire est plein. Sinon, elle retourne {@code false}.</p>
     *
     * @return {@code true} si l'inventaire est plein, {@code false} sinon.
     */
    public boolean inventoryFull() {
        int quantity = 0;
        for (ItemInventory itemInventory : inventory) {
            quantity += itemInventory.getQuantity();
        }
        return quantity >= inventoryMaxItems;
    }

    /**
     * Vérifie si une tâche est en cours.
     *
     * <p>Cette méthode détermine si une tâche est actuellement en cours en comparant
     * la progression actuelle de la tâche ({@code taskProgress}) au total requis
     * pour compléter la tâche ({@code taskTotal}). Si la progression est inférieure
     * au total requis, la méthode retourne {@code true}, indiquant que la tâche
     * est encore en cours. Sinon, elle retourne {@code false}.</p>
     *
     * @return {@code true} si la tâche est en cours, {@code false} sinon.
     */
    public boolean taskInProgress() {
        return taskProgress < taskTotal;
    }


    /**
     * Vérifie si une tâche est complétée.
     *
     * <p>Cette méthode détermine si une tâche est terminée en comparant
     * la progression actuelle de la tâche ({@code taskProgress}) au total requis
     * pour la compléter ({@code taskTotal}). Si la progression est égale
     * au total requis, la méthode retourne {@code true}, indiquant que la tâche
     * est terminée. Sinon, elle retourne {@code false}.</p>
     *
     * @return {@code true} si la tâche est terminée, {@code false} sinon.
     */
    public boolean taskComplete() {
        return taskProgress == taskTotal;
    }


    public boolean isInInventory(Craft craft, int itemQuantity) {
        boolean result = false;
        for(ItemSimple craftPart : craft.getItems()) {
            ItemInventory inventory = getInventory().stream().filter(i -> i.getCode().equals(craftPart.getCode())).findFirst().orElse(null);
            if(inventory != null) {
                if(inventory.getQuantity() >= (craftPart.getQuantity() * itemQuantity)) {
                    result = true;
                } else {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}