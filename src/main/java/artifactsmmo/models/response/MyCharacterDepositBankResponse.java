package artifactsmmo.models.response;

import artifactsmmo.models.schema.BankItemSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterDepositBankResponse {

    @JsonProperty("data")
    private BankItemSchema bankItemSchema;
}
