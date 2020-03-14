package pl.poznan.put.cs.project.spotifypartyplanner.service;

import org.springframework.stereotype.Service;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Playlist;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Track;
import pl.poznan.put.cs.project.spotifypartyplanner.repository.PlaylistRepository;
import pl.poznan.put.cs.project.spotifypartyplanner.spotify.SpotifyConnector;
import pl.poznan.put.cs.project.spotifypartyplanner.spotify.exception.SpotifyException;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.poznan.put.cs.project.spotifypartyplanner.spotify.SpotifyHelper.emptyIfAuthorizationErrorOrThrow;

@Service
public class PlaylistService {

    private final PlaylistRepository repository;
    private final SpotifyConnector spotifyConnector;

    private static Map<String, Float> defaultTunableParameters = Collections.singletonMap("max_liveness", .3f);

    public PlaylistService(PlaylistRepository repository, SpotifyConnector spotifyConnector) {
        this.repository = repository;
        this.spotifyConnector = spotifyConnector;
    }

    public Playlist createPlaylist(String eventId) {
        var playlist = new Playlist(eventId);
        return repository.insert(playlist);
    }

    public Playlist addPreferences(String eventId, List<String> genres, List<String> tracks) throws NoSuchElementException {
        var playlist = repository.findByEventId(eventId).orElseThrow(NoSuchElementException::new);
        addPlaylistPreference(playlist.getPreferences().getGenres(), genres);
        addPlaylistPreference(playlist.getPreferences().getTracks(), tracks);
        return repository.save(playlist);
    }

    private void addPlaylistPreference(HashMap<String, Integer> data, List<String> source) {
        source.forEach(g -> data.put(g, data.computeIfAbsent(g, k -> 0)+1));
    }

    public Stream<Track> getTracksProposal(String eventId) throws NoSuchElementException, SpotifyException {
        var playlist = repository.findByEventId(eventId).orElseThrow(NoSuchElementException::new);
        var tracks = getTopPreferences(playlist.getPreferences().getTracks());
        var genres = getTopPreferences(playlist.getPreferences().getGenres());

        return emptyIfAuthorizationErrorOrThrow(
                () -> spotifyConnector.getRecommendations(Collections.emptyList(), genres, defaultTunableParameters)
        );
    }

    private static List<String> getTopPreferences(HashMap<String, Integer> preferences) {
        return preferences.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


}
