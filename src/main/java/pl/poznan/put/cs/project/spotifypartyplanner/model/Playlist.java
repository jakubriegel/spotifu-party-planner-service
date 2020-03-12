package pl.poznan.put.cs.project.spotifypartyplanner.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Playlist {
    @Id
    private String id;
    private String eventId;
    private PlaylistPreferences preferences = new PlaylistPreferences();

    public Playlist() {
    }

    public Playlist(String eventId) {
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public PlaylistPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(PlaylistPreferences preferences) {
        this.preferences = preferences;
    }
}
