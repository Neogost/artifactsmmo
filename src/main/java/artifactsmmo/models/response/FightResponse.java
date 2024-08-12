package artifactsmmo.models.response;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.Schema.CharacterFightDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FightResponse {

    @JsonProperty("data")
    private CharacterFightDataSchema characterFightDataSchema;
}