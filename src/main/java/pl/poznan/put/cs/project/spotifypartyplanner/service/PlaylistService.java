package pl.poznan.put.cs.project.spotifypartyplanner.service;

import org.springframework.stereotype.Service;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Playlist;
import pl.poznan.put.cs.project.spotifypartyplanner.repository.PlaylistRepository;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PlaylistService {
    private final PlaylistRepository repository;


    public PlaylistService(PlaylistRepository repository) {
        this.repository = repository;
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


}
