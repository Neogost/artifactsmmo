package artifactsmmo.models.schema;


import artifactsmmo.models.entity.ItemBank;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BankItemSchema {

    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("item")
    private Item item;

    @JsonProperty("bank")
    private List<ItemBank> bank;

    @JsonProperty("character")
    private Character character;
}
