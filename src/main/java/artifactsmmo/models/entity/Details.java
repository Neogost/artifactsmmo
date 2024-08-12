package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class Details {

    @JsonProperty("xp")
    private int xp;

    @JsonProperty("items")
    private List<ItemGathering> items;


}
