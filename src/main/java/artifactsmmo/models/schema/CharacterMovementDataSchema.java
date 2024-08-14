package artifactsmmo.models.schema;

import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Destination;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CharacterMovementDataSchema {

    @JsonProperty("cooldown")
    private Cooldown cooldown;
    @JsonProperty("destination")
    private Destination destination;
    @JsonProperty("character")
    private Character character;

    // Getters and setters
}