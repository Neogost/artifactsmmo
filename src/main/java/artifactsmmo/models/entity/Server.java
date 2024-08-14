package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class Server {

    @JsonProperty("status")
    private String status;

    @JsonProperty("version")
    private String version;

    @JsonProperty("characters_online")
    private int charactersOnline;

    @JsonProperty("server_time")
    private ZonedDateTime serverTime;

    @JsonProperty("announcements")
    private List<Announcement> announcements;

    @JsonProperty("last_wipe")
    private String lastWipe;

    @JsonProperty("next_wipe")
    private String nextWipe;
}
