package artifactsmmo.models.response;

import artifactsmmo.models.entity.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapResponse {

    @JsonProperty("data")
    private Map map;
}
