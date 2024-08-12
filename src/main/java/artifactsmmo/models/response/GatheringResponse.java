package artifactsmmo.models.response;

import artifactsmmo.models.Schema.SkillDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GatheringResponse {

    @JsonProperty("data")
    private SkillDataSchema skillDataSchema;
}
