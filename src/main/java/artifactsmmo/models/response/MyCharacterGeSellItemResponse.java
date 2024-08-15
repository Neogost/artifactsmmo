package artifactsmmo.models.response;

import artifactsmmo.models.schema.GeTransactionListSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterGeSellItemResponse {

    @JsonProperty("data")
    private GeTransactionListSchema geTransactionListSchema;
}
