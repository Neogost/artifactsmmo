package artifactsmmo.models.response;

import artifactsmmo.models.schema.SkillDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterGatheringResponse {

    @JsonProperty("data")
    private SkillDataSchema skillDataSchema;
}
