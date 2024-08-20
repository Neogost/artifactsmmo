package artifactsmmo.models.entity;

import artifactsmmo.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("level")
    private int level;

    @JsonProperty("type")
    private ItemType itemType;

    @JsonProperty("subtype")
    private String subtype;

    @JsonProperty("description")
    private String description;

    @JsonProperty("effects")
    private List<Effect> effects;

    @JsonProperty("craft")
    private Craft craft;
}
