package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GE {

    @JsonProperty("code")
    private String code;

    @JsonProperty("stock")
    private int stock;

    @JsonProperty("sell_price")
    private int sellPrice;

    @JsonProperty("buy_price")
    private int buyPrice;
}
