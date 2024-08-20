package artifactsmmo.models.entity;

import artifactsmmo.enums.Skill;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Craft {

    @JsonProperty("skill")
    private Skill skill;

    @JsonProperty("level")
    private int level;

    @JsonProperty("items")
    private List<ItemSimple> items;

    @JsonProperty("quantity")
    private int quantity;
}
