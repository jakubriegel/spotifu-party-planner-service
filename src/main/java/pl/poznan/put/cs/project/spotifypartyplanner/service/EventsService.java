package pl.poznan.put.cs.project.spotifypartyplanner.service;

import org.springframework.stereotype.Service;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Track;
import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Event;
import pl.poznan.put.cs.project.spotifypartyplanner.repository.EventRepository;
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

import static java.util.Collections.emptyList;
import static pl.poznan.put.cs.project.spotifypartyplanner.spotify.SpotifyHelper.emptyIfAuthorizationErrorOrThrow;

@Service
public class EventsService {

    private final EventRepository repository;
    private final SpotifyConnector spotifyConnector;

    private static Map<String, Float> defaultTunableParameters = Collections.singletonMap("max_liveness", .3f);

    public EventsService(EventRepository repository, SpotifyConnector spotifyConnector) {
        this.repository = repository;
        this.spotifyConnector = spotifyConnector;
    }

    public List<Event> getEventsByUser(String userId) {
        return repository.findByHostId(userId);
    }

    public Event addEvent(Event event) {
        return repository.insert(event);
    }

    public Event addPlaylistPreference(String eventId, List<String> genres, List<String> tracks) throws NoSuchElementException {
        var event = repository.findById(eventId).orElseThrow(NoSuchElementException::new);
        addPlaylistPreference(event.getPlaylist().getSuggestions().getGenres(), genres);
        addPlaylistPreference(event.getPlaylist().getSuggestions().getTracks(), tracks);
        return repository.save(event);
    }

    private void addPlaylistPreference(HashMap<String, Integer> data, List<String> source) {
        source.forEach(g -> data.put(g, data.computeIfAbsent(g, k -> 0)+1));
    }

    public Stream<Track> getTracksProposal(String eventId) throws NoSuchElementException, SpotifyException {
        var event = repository.findById(eventId).orElseThrow(NoSuchElementException::new);
        var tracks = getTopPreferences(event.getPlaylist().getSuggestions().getTracks());
        var genres = getTopPreferences(event.getPlaylist().getSuggestions().getGenres());

        return emptyIfAuthorizationErrorOrThrow(
                () -> spotifyConnector.getRecommendations(tracks, emptyList(), defaultTunableParameters)
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
