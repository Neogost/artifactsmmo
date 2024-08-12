package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Destination {
    @JsonProperty("name")
    private String name;

    @JsonProperty("skin")
    private String skin;

    @JsonProperty("x")
    private int x;

    @JsonProperty("y")
    private int y;

    @JsonProperty("content")
    private Content content;

    // Getters and setters
}
