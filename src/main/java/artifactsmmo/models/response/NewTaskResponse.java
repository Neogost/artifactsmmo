package artifactsmmo.models.response;

import artifactsmmo.models.schema.TaskDataSchema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewTaskResponse {
    private TaskDataSchema taskDataSchema;
}