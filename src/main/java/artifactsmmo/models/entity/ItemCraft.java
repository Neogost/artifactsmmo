package artifactsmmo.models.entity;

import artifactsmmo.enums.Skill;
import artifactsmmo.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemCraft implements Serializable {

    private static final long serialVersionUID = 1L;
    private String code;
    private int level;
    private Skill skill;
    private ItemType type;
    private String subType;
    private int quantityInBank;
    private int quantityInInventory;
    private int quantityTotalNeeded;
    private int priority;

    public int getQuantityTotal() {
        return quantityInBank + quantityInInventory;
    }

    public boolean isComplete() {
        return quantityTotalNeeded <= quantityInInventory + quantityInBank;
    }


    public void transfertFromBank(int quantity) {
        quantityInInventory += quantity;
        quantityInBank -= quantity;

    }
}
