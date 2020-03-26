package pl.poznan.put.cs.project.spotifypartyplanner.model;

public class Playlist {
    private String name;
    private String spotifyId;
    private PlaylistPreferences preferences = new PlaylistPreferences();

    public Playlist() {
        this.name = null;
        this.spotifyId = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public PlaylistPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(PlaylistPreferences preferences) {
        this.preferences = preferences;
    }
}
