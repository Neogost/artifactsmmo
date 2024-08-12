package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockedHits {

    @JsonProperty("fire")
    private int fire;

    @JsonProperty("earth")
    private int earth;

    @JsonProperty("water")
    private int water;

    @JsonProperty("air")
    private int air;

    @JsonProperty("total")
    private int total;

}
