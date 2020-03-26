package pl.poznan.put.cs.project.spotifypartyplanner.rest.model.response;

import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Event;
import pl.poznan.put.cs.project.spotifypartyplanner.rest.model.response.event.EventResponse;

import java.util.List;
import java.util.stream.Collectors;


public class UserEventsResponse {
    public String userId;
    public List<EventResponse> events;

    public UserEventsResponse(String userId, List<Event> events) {
        this.userId = userId;
        this.events = events.stream().map(EventResponse::fromEvent).collect(Collectors.toList());
    }
}
