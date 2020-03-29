package pl.poznan.put.cs.project.spotifypartyplanner.service;

import org.springframework.stereotype.Service;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Event;
import pl.poznan.put.cs.project.spotifypartyplanner.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventsService {
    private final EventRepository repository;

    public EventsService(EventRepository repository) {
        this.repository = repository;
    }

    public List<Event> getEventsByUser(String userId) {
        return repository.findByHostId(userId);
    }

    public Event addEvent(Event event) {
        return repository.insert(event);
    }

    public Optional<Event> getEventById(String eventId) {return repository.findById(eventId); }

    public void deleteEvent(String eventId) { repository.deleteById(eventId); }
}
