package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

    @JsonProperty("code")
    private String code;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("price")
    private int price;

    @JsonProperty("total_price")
    private int totalPrice;
}
