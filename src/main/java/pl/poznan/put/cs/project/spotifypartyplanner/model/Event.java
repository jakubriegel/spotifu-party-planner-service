package pl.poznan.put.cs.project.spotifypartyplanner.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Document
public class Event {
    @Id
    private String id;
    private String name;
    private String location;
    private Instant date;
    private String hostId;
    private List<String> tracks;
    private boolean open;

    public Event(String name, String location, Instant date, String hostId) {
        this.id = null;
        this.name = name;
        this.hostId = hostId;
        this.location = location;
        this.date = date;
        this.tracks = Collections.emptyList();
        this.open = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
