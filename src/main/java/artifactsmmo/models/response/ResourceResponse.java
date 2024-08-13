package artifactsmmo.models.response;

import artifactsmmo.models.entity.Resource;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceResponse {

    @JsonProperty("data")
    private Resource resource;

}
