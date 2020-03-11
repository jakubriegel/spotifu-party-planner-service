package pl.poznan.put.cs.project.spotifypartyplanner.spotify;

public class SpotifyAuthorizationException extends Exception {
    public SpotifyAuthorizationException(String msg) {
        super("exception during spotify auth " + msg);
    }
}
