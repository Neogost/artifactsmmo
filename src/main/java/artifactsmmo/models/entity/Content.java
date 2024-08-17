package artifactsmmo.models.entity;

import artifactsmmo.enums.ContentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Content {

    @JsonProperty("type")
    private ContentType type;
    @JsonProperty("code")
    private String code;
}
