package artifactsmmo.models.response;

import artifactsmmo.models.schema.RecyclingDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterRecyclingResponse {

    @JsonProperty("data")
    private RecyclingDataSchema recyclingDataSchema;
}
