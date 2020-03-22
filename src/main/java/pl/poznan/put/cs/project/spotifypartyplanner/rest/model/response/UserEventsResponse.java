package pl.poznan.put.cs.project.spotifypartyplanner.rest.model.response;

import pl.poznan.put.cs.project.spotifypartyplanner.model.Event;

import java.util.List;

public class UserEventsResponse {
    public String userId;
    public List<Event> events;

    public UserEventsResponse(String userId, List<Event> events) {
        this.userId = userId;
        this.events = events;
    }
}
