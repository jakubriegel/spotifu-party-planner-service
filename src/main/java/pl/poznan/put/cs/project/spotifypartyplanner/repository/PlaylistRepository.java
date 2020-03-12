package pl.poznan.put.cs.project.spotifypartyplanner.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Playlist;

import java.util.Optional;

public interface PlaylistRepository extends MongoRepository<Playlist, String> {
    Optional<Playlist> findByEventId(String eventId);
}
