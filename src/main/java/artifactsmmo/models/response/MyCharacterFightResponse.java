package artifactsmmo.models.response;

import artifactsmmo.models.schema.CharacterFightDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterFightResponse {

    @JsonProperty("data")
    private CharacterFightDataSchema characterFightDataSchema;
}