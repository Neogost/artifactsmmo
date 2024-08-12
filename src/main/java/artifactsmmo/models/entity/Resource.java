package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Resource {
    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("skill")
    private String skill;

    @JsonProperty("level")
    private int level;

    @JsonProperty("drops")
    private List<Drop> drops;
}
