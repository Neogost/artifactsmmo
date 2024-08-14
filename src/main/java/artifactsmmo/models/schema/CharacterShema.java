package artifactsmmo.models.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterShema {


    @JsonProperty("data")
    private Character character;
}
