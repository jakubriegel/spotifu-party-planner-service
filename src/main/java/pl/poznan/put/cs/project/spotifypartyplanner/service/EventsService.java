package pl.poznan.put.cs.project.spotifypartyplanner.service;

import org.springframework.stereotype.Service;
import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Event;
import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Playlist;
import pl.poznan.put.cs.project.spotifypartyplanner.repository.EventRepository;
import pl.poznan.put.cs.project.spotifypartyplanner.spotify.SpotifyConnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    public Event addGuestsSuggestions(String eventId, List<String> genres, List<String> tracks) throws NoSuchElementException {
        var event = repository.findById(eventId).orElseThrow(NoSuchElementException::new);
        addGuestsSuggestions(event.getPlaylist(), genres, tracks);
        return repository.save(event);
    }

    private void addGuestsSuggestions(Playlist playlist, List<String> genres, List<String> tracks) throws NoSuchElementException {
        addSuggestionsFrom(playlist.getSuggestions().getFromGuests().getTracks(), tracks);
        addSuggestionsFrom(playlist.getSuggestions().getFromGuests().getGenres(), genres);
        updateTracksWithGuestsSuggestions(playlist, tracks);
    }

    private void addSuggestionsFrom(HashMap<String, Integer> data, List<String> source) {
        source.forEach(i -> data.put(i, data.computeIfAbsent(i, k -> 0)+1));
    }

    private void updateTracksWithGuestsSuggestions(Playlist playlist, List<String> suggestions) {
        var updated = new HashSet<String>();
        updated.addAll(playlist.getTracks());
        updated.addAll(suggestions);
        playlist.setTracks(new ArrayList<>(updated));
    }

    public Event removeGuestsSuggestions(String eventId, List<String> genres, List<String> tracks) throws NoSuchElementException {
        var event = repository.findById(eventId).orElseThrow(NoSuchElementException::new);
        removeGuestsSuggestions(event.getPlaylist(), genres, tracks);
        return repository.save(event);
    }

    private void removeGuestsSuggestions(Playlist playlist, List<String> genres, List<String> tracks) throws NoSuchElementException {
        removeSuggestionsFrom(playlist.getSuggestions().getFromGuests().getTracks(), tracks);
        removeSuggestionsFrom(playlist.getSuggestions().getFromGuests().getGenres(), genres);
        var tracksToRemove = playlist.getSuggestions()
                .getFromGuests()
                .getTracks()
                .entrySet()
                .stream()
                .filter(t -> t.getValue() <= 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        removeTracksFromPlaylist(playlist, tracksToRemove);
    }

    private void removeSuggestionsFrom(HashMap<String, Integer> data, List<String> source) {
        source.forEach(i -> data.computeIfPresent(i, (key, val) -> val > 0 ? val-1 : 0));
    }

    private void removeTracksFromPlaylist(Playlist playlist, List<String> tracksIds) {
        playlist.getTracks().removeAll(tracksIds);
    }

//    public Stream<Track> getTracksProposal(String eventId) throws NoSuchElementException, SpotifyException {
//        var event = repository.findById(eventId).orElseThrow(NoSuchElementException::new);
//        var tracks = getTopPreferences(event.getPlaylist().getSuggestions().getTracks());
//        var genres = getTopPreferences(event.getPlaylist().getSuggestions().getGenres());
//
//        return emptyIfAuthorizationErrorOrThrow(
//                () -> spotifyConnector.getRecommendations(tracks, emptyList(), defaultTunableParameters)
//        );
//    }

    private static List<String> getTopPreferences(HashMap<String, Integer> preferences) {
        return preferences.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
