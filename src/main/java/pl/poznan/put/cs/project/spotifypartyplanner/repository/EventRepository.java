package pl.poznan.put.cs.project.spotifypartyplanner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Event;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByHostId(String hostId);
}
