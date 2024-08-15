package artifactsmmo.models.response;

import artifactsmmo.models.schema.GoldTransactionSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterDepositGoldBankResponse {


    @JsonProperty("data")
    private GoldTransactionSchema goldTransactionSchema;
}
