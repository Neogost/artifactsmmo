package artifactsmmo.models.response;

import artifactsmmo.models.entity.Monster;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonsterResponse {

    @JsonProperty("data")
    private Monster monster;
}
