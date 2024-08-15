package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LogContent {

    @JsonProperty("fight")
    private Fight fight;

    @JsonProperty("drops")
    private List<Drop> drops;
}
