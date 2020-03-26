package pl.poznan.put.cs.project.spotifypartyplanner.rest.model.response.event;

import pl.poznan.put.cs.project.spotifypartyplanner.model.Track;
import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Playlist;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class PlaylistResponse {
    public String name = null;
    public String spotifyId = null;
    public SuggestionsResponse suggestions = new SuggestionsResponse();
    public List<Track> tracks = emptyList();

    public static PlaylistResponse fromPlaylist(final Playlist playlist) {
        var response = new PlaylistResponse();
        response.name = playlist.getName();
        response.spotifyId = playlist.getName();
        response.suggestions = SuggestionsResponse.fromSuggestions(playlist.getSuggestions());
        response.tracks = playlist.getTracks()
                .stream()
                .map(id -> {
                    var track = new Track();
                    track.setId(id);
                    return track;
                })
                .collect(Collectors.toList());
        return response;
    }
}
