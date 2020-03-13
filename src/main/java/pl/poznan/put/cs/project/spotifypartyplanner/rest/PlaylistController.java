package pl.poznan.put.cs.project.spotifypartyplanner.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Playlist;
import pl.poznan.put.cs.project.spotifypartyplanner.rest.model.request.PlaylistPreferencesRequest;
import pl.poznan.put.cs.project.spotifypartyplanner.service.PlaylistService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("playlists")
public class PlaylistController {

    private final PlaylistService service;

    public PlaylistController(PlaylistService service) {
        this.service = service;
    }

    // to be removed after implementing events
    @PostMapping("/{eventId}")
    public Playlist postPlaylist(
            @PathVariable String eventId
    ) {
        return service.createPlaylist(eventId);
    }

    @PostMapping("/{eventId}/preferences")
    public ResponseEntity<Playlist> postGenres(
            @RequestBody PlaylistPreferencesRequest request,
            @PathVariable String eventId
    ) {
        try {
            var playlist = service.addPreferences(eventId, request.genres, request.tracks);
            return ResponseEntity.ok(playlist);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
