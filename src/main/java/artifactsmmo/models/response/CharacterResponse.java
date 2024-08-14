package artifactsmmo.models.response;

import artifactsmmo.models.schema.CharacterMovementDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CharacterResponse {

    @JsonProperty("data")
    private CharacterMovementDataSchema characterMovementDataSchema;
}