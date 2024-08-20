package artifactsmmo.utils;

import artifactsmmo.controllers.ItemControler;
import artifactsmmo.models.entity.*;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.schema.SingleItemSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ItemUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemUtils.class);

    @Autowired
    private static ItemControler itemControler;

    public static List<ItemFightSimulation> simulateWeaponOnMonster(List<Item> items, Character character, Monster monster) {
        LOGGER.info("{} try to find best items again {}", character.getName(), monster.getName());
        List<ItemFightSimulation> itemFightSimulations = new ArrayList<>();
        List<Item> weaponInRange = items.stream().filter(i -> i.getLevel() <= character.getLevel()).toList();

        for (Item item : weaponInRange) {
            double damageDeal = 0;
            // Simulate each effect of each item on the monster
            for (Effect effect : item.getEffects()) {
                switch (effect.getName()) {
                    case "attack_air":
                        damageDeal += effect.getValue() + (effect.getValue() * (character.getDmgAir() * 0.01)) * (1 - monster.getResAir() * 0.01);
                        break;
                    case "attack_earth":
                        damageDeal += (effect.getValue() + (effect.getValue() * (character.getDmgEarth() * 0.01))) * (1 - monster.getResEarth() * 0.01);
                        break;
                    case "attack_water":
                        damageDeal += (effect.getValue() + (effect.getValue() * (character.getDmgWater() * 0.01))) * (1 - monster.getResWater() * 0.01);
                        break;
                    case "attack_fire":
                        damageDeal += (effect.getValue() + (effect.getValue() * (character.getDmgFire() * 0.01))) * (1 - monster.getResFire() * 0.01);
                        break;
                    default:
                        LOGGER.warn("Effect {} not supported", effect.getName());
                }
            }
            itemFightSimulations.add(new ItemFightSimulation(item, monster, damageDeal));
            LOGGER.info("Item {} deal {} damages against {}.", item.getCode(), damageDeal, monster.getName());
        }
        return itemFightSimulations;
    }

}
