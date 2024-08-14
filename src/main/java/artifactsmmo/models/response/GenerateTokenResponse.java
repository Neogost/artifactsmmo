package artifactsmmo.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenerateTokenResponse {

    @JsonProperty("message")
    private String message;
}
