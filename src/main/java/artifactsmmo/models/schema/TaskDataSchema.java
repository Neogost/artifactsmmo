package artifactsmmo.models.schema;


import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Task;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDataSchema {

    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("task")
    private Task task;

    @JsonProperty("character")
    private Character character;
}