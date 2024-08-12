package artifactsmmo.models.response;

import artifactsmmo.models.Schema.BankItemSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankItemResponse {

    @JsonProperty("data")
    private BankItemSchema bankItemSchema;
}
