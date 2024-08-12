package artifactsmmo.models.response;

import artifactsmmo.models.entity.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapResponse {

    private List<Map> data;
    private int total;
    private int page;
    private int size;
    private int pages;


}