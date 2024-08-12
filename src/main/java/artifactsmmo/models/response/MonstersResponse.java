package artifactsmmo.models.response;

import artifactsmmo.models.entity.Monster;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MonstersResponse {

    @JsonProperty("data")
    private List<Monster> monsters;

    @JsonProperty("total")
    private int total;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int size;

    @JsonProperty("pages")
    private int pages;

}
