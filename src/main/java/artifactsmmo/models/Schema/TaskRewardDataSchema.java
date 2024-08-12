package artifactsmmo.models.Schema;

import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Reward;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRewardDataSchema {

    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("reward")
    private Reward reward;

    @JsonProperty("character")
    private Character character;
}
