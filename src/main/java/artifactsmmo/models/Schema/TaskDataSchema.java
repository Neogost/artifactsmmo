package artifactsmmo.models.Schema;


import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDataSchema {
    private Cooldown cooldown;
    private Task task;
    private Character character;
}