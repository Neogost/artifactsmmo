package artifactsmmo.models.response;

import artifactsmmo.models.schema.LogSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyCharactersLogsResponse {

    @JsonProperty("data")
    private List<LogSchema> logs;

    @JsonProperty("total")
    private int total;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int size;

    @JsonProperty("pages")
    private int pages;
}
