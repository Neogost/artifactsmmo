package artifactsmmo.models.response;

import artifactsmmo.models.schema.TaskRewardDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRewardResponse {

    @JsonProperty("data")
    private TaskRewardDataSchema taskRewardDataSchema;
}
