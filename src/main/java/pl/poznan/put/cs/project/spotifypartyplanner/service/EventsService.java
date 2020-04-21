package pl.poznan.put.cs.project.spotifypartyplanner.service;

import org.springframework.stereotype.Service;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Track;
import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Event;
import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Playlist;
import pl.poznan.put.cs.project.spotifypartyplanner.repository.EventRepository;
import pl.poznan.put.cs.project.spotifypartyplanner.spotify.SpotifyConnector;
import pl.poznan.put.cs.project.spotifypartyplanner.spotify.exception.SpotifyException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;

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
        fetchHostname(event);
        return repository.insert(event);
    }

    private void fetchHostname(Event event) {
        Optional.of(event)
                .map(Event::getHostId)
                .flatMap(hostId -> {
                    try {
                        return spotifyConnector.getDisplayname(hostId);
                    } catch (SpotifyException ignored) {
                        return Optional.empty();
                    }
                })
                .ifPresent(event::setHostname);
    }

    public Optional<Event> getEventById(String eventId) {return repository.findById(eventId); }

    public void deleteEvent(String eventId) { repository.deleteById(eventId); }

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

    public Event addTracks(String eventId, List<String> trackIds) {
        return getEventById(eventId).map(e -> {
            var tracks = new HashSet<>(e.getPlaylist().getTracks());
            tracks.addAll(trackIds);
            e.getPlaylist().setTracks(new ArrayList<>(tracks));
            return e;
        }).map(repository::save).orElseThrow(NoSuchElementException::new);
    }

    public Event removeTracks(String eventId, List<String> trackIds) {
        return getEventById(eventId).map(e -> {
            var tracks = new HashSet<>(e.getPlaylist().getTracks());
            tracks.removeAll(trackIds);
            e.getPlaylist().setTracks(new ArrayList<>(tracks));
            return e;
        }).map(repository::save).orElseThrow(NoSuchElementException::new);
    }

    public void synchronizePlaylistWithSpotify(String eventId, String userToken) throws SpotifyException {
        var event = getEventById(eventId).orElseThrow(NoSuchElementException::new);

        if (event.getPlaylist().getSpotifyId() == null) {
            var name = String.format("%s Playlist", event.getName());
            var playlistId = spotifyConnector.createSpotifyPlaylist(
                    event.getHostId(), userToken, name, ""
            ).orElseThrow(NullPointerException::new);
            event.getPlaylist().setSpotifyId(playlistId);
            event.getPlaylist().setName(name);
        }

        var tracks = Optional.of(event)
                .map(Event::getPlaylist)
                .map(Playlist::getTracks)
                .map(HashSet::new)
                .map(spotifyConnector::getTracksById)
                .get()
                .map(Track::getUri)
                .collect(toUnmodifiableList());

        if (tracks.size() > 0) {
            spotifyConnector.replaceTracksOnPlaylist(event.getPlaylist().getSpotifyId(), tracks, userToken);
        }

        repository.save(event);
    }

}
