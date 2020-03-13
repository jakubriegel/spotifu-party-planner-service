package pl.poznan.put.cs.project.spotifypartyplanner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Track;
import pl.poznan.put.cs.project.spotifypartyplanner.spotify.SpotifyConnector;

import java.util.stream.Stream;

@Service
public class ExploreService {

    private final SpotifyConnector spotifyConnector;

    public ExploreService(SpotifyConnector spotifyConnector) {
        this.spotifyConnector = spotifyConnector;
    }

    public Stream<Track> search(String text) {
        try {
            return spotifyConnector.search(text);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Stream.empty();
        }
    }

    public Stream<String> getGenres() {
        try {
            return spotifyConnector.getGenreSeeds();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Stream.empty();
        }
    }

    private static Logger logger = LoggerFactory.getLogger(ExploreService.class);
}
