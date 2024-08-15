package artifactsmmo.models.response;

import artifactsmmo.models.schema.BankItemSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MyCharacterWithdrawBankResponse {

    @JsonProperty("data")
    private BankItemSchema bankItemSchema;
}
