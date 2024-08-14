package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Craft {

    @JsonProperty("skill")
    private String skill;

    @JsonProperty("level")
    private int level;

    @JsonProperty("items")
    private List<ItemCraft> items;

    @JsonProperty("quantity")
    private int quantity;
}
