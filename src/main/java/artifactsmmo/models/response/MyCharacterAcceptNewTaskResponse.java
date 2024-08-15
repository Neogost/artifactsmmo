package artifactsmmo.models.response;

import artifactsmmo.models.schema.TaskDataSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterAcceptNewTaskResponse {

    @JsonProperty("data")
    private TaskDataSchema taskDataSchema;
}