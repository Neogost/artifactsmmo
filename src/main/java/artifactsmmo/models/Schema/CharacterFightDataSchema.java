package artifactsmmo.models.Schema;


import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Fight;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterFightDataSchema {


    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("fight")
    private Fight fight;

    @JsonProperty("character")
    private Character character;
}
