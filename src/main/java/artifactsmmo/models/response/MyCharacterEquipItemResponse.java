package artifactsmmo.models.response;

import artifactsmmo.models.schema.EquipRequestSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterEquipItemResponse {
    @JsonProperty("data")
    private EquipRequestSchema equipRequestSchema;
}
