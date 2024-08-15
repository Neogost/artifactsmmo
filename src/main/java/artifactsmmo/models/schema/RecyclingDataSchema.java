package artifactsmmo.models.schema;

import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Details;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecyclingDataSchema {


    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("details")
    private Details details;

    @JsonProperty("character")
    private Character character;
}
