package artifactsmmo.models.response;

import artifactsmmo.models.entity.Server;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusResponse {

    @JsonProperty("data")
    private Server server;
}
