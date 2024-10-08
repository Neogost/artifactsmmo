package artifactsmmo.models.response;

import artifactsmmo.models.entity.ItemSimple;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BankItemsResponse {

    @JsonProperty("data")
    private List<ItemSimple> items;

    @JsonProperty("total")
    private int total;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int size;

    @JsonProperty("pages")
    private int pages;
}
