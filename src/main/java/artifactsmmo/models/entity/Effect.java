package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Effect {

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private int value;
}
