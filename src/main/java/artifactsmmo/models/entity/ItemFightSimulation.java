package artifactsmmo.models.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemFightSimulation extends Item{

    private Monster target;
    private double damageDealOnTarget;

    public ItemFightSimulation(Item item, Monster target, double damageDealOnTarget) {
        super(item.getName(),
                item.getCode(),
                item.getLevel(),
                item.getItemType(),
                item.getSubtype(),
                item.getDescription(),
                item.getEffects(),
                item.getCraft());
        this.target = target;
        this.damageDealOnTarget = damageDealOnTarget;
    }

}
