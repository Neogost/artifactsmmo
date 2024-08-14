package artifactsmmo.models.response;

import artifactsmmo.models.entity.GE;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GeItemResponse {

    @JsonProperty("data")
    private GE geItem;

}
