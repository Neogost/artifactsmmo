package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Content {
    @JsonProperty("type")
    private String type;
    @JsonProperty("code")
    private String code;
}
