package pl.poznan.put.cs.project.spotifypartyplanner.spotify.model;

import java.util.List;

public class RecommendationsResponse extends SpotifyResponse {
    public List<Item> tracks;

    public List<Item> getTracks() {
        return tracks;
    }
}
