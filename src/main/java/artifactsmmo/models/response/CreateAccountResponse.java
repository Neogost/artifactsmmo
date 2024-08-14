package artifactsmmo.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountResponse {

    @JsonProperty("message")
    private String message;
}
