package artifactsmmo.models.schema;

import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipRequestSchema {

    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("slot")
    private String slot;

    @JsonProperty("item")
    private Item item;

    @JsonProperty("character")
    private Character character;
}
