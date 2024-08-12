package artifactsmmo.models.response;

import artifactsmmo.models.Schema.CharacterFightDataSchema;
import artifactsmmo.models.Schema.CharacterShema;
import artifactsmmo.models.entity.Character;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindCharacterResponse {

    @JsonProperty("data")
    private Character character;
}
