package pl.poznan.put.cs.project.spotifypartyplanner.model;

import java.util.HashMap;

public class PlaylistPreferences {
    private HashMap<String, Integer> genres = new HashMap<>();
    private HashMap<String, Integer> tracks = new HashMap<>();

    public HashMap<String, Integer> getGenres() {
        return genres;
    }

    public void setGenres(HashMap<String, Integer> genres) {
        this.genres = genres;
    }

    public HashMap<String, Integer> getTracks() {
        return tracks;
    }

    public void setTracks(HashMap<String, Integer> tracks) {
        this.tracks = tracks;
    }
}
