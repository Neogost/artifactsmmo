package artifactsmmo.models.response;

import artifactsmmo.models.schema.DeleteItemSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterDeleteItemResponse {

    @JsonProperty("data")
    private DeleteItemSchema deleteItemSchema;
}
