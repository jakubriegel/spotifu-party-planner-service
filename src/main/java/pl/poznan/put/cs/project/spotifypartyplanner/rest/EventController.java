package pl.poznan.put.cs.project.spotifypartyplanner.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Event;
import pl.poznan.put.cs.project.spotifypartyplanner.rest.model.response.UserEventsResponse;
import pl.poznan.put.cs.project.spotifypartyplanner.service.EventsService;

import java.net.URI;

@RestController
@RequestMapping("events")
@CrossOrigin
public class EventController {
    private final EventsService service;

    public EventController(EventsService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, params = "userId")
    ResponseEntity<UserEventsResponse> getUserEvents(
            @RequestParam String userId
    ) {
        var events = service.getEventsByUser(userId);
        if (events.isEmpty()) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(new UserEventsResponse(userId, events));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Event> postEvent(
            @RequestBody Event request
    ) {
        var event = service.addEvent(request);
        return ResponseEntity.created(URI.create("/events?userId=" + event.getHostId()))
                .body(event);
    }

    @GetMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Event> getEvent(
            @PathVariable String eventId
    ) {
        var event = service.getEventById(eventId);
        if (event.isEmpty()) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(event.get());
    }

    @DeleteMapping(value = "/{eventId}")
    ResponseEntity<Void> deleteEvent(
            @PathVariable String eventId
    ) {
        service.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
