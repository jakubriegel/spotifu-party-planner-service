package pl.poznan.put.cs.project.spotifypartyplanner.rest.model.response.event;

import pl.poznan.put.cs.project.spotifypartyplanner.model.Track;
import pl.poznan.put.cs.project.spotifypartyplanner.model.event.SuggestionsFrom;

import java.util.List;

import static java.util.Collections.emptyList;

public class SuggestionsFromResponse {
    public List<SuggestionsElementResponse<String>> genres = emptyList();
    public List<SuggestionsElementResponse<Track>> tracks = emptyList();

    public static SuggestionsFromResponse fromSuggestionsFrom(final SuggestionsFrom suggestionsFrom) {
        var response = new SuggestionsFromResponse();
        response.genres = SuggestionsElementResponse.fromMap(suggestionsFrom.getGenres(), id -> id);
        response.tracks = SuggestionsElementResponse.fromMap(suggestionsFrom.getTracks(), id -> {
            var track = new Track();
            track.setId(id);
            return track;
        });
        return response;
    }
}
