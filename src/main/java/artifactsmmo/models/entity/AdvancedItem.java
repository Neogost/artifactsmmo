package artifactsmmo.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvancedItem extends Item{

    private Monster target;
    private double damageDealOnTarget;

    public AdvancedItem(Item item, Monster target, double damageDealOnTarget) {
        super(item.getName(),
                item.getCode(),
                item.getLevel(),
                item.getType(),
                item.getSubtype(),
                item.getDescription(),
                item.getEffects(),
                item.getCraft());
        this.target = target;
        this.damageDealOnTarget = damageDealOnTarget;
    }

}
