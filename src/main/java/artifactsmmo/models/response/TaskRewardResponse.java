package artifactsmmo.models.response;

import artifactsmmo.models.Schema.TaskRewardDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRewardResponse {

    @JsonProperty("data")
    private TaskRewardDataSchema taskRewardDataSchema;
}
