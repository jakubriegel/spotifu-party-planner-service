package pl.poznan.put.cs.project.spotifypartyplanner.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Album;
import pl.poznan.put.cs.project.spotifypartyplanner.model.CoverImage;
import pl.poznan.put.cs.project.spotifypartyplanner.model.Track;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SpotifyConnector {

    private static final String AUTH_LINK = "https://accounts.spotify.com/api/token";
    private static final String API_LINK = "https://api.spotify.com/v1";

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    private final String authorizationToken;

    public SpotifyConnector(@Value("${spotify.auth}") String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    // TODO: @Cacheable
    public String authorize() throws Exception {
        var headers = new HttpHeaders();
        headers.setBasicAuth(authorizationToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var map= new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "client_credentials");

        var response = restTemplate.exchange(
                AUTH_LINK,
                HttpMethod.POST,
                new HttpEntity<>(map, headers),
                AuthorizationResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(response.getBody()).accessToken;
        }
        else {
            throw new SpotifyAuthorizationException(response.toString());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class AuthorizationResponse {
        public String accessToken;
    }

    public Stream<Track> search(String text) throws Exception {
        var headers = new HttpHeaders();
        headers.setBearerAuth(authorize());
        return Stream.of(restTemplate.exchange(
                URI.create(API_LINK + "/search?q=" + text + "&type=track&market=PL"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                SearchResponse.class
        )).map(HttpEntity::getBody)
                .filter(Objects::nonNull)
                .map(SearchResponse::getTracks)
                .map(Tracks::getItems)
                .flatMap(Collection::stream)
                .map(i ->  new Track(
                        i.id,
                        i.name,
                        mapArtists(i.artists),
                        new Album(i.album.id, i.album.name, mapArtists(i.album.artists), i.album.images),
                        mapDuration(i.durationMs)
                ));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SearchResponse {
        public Tracks tracks;
        public Tracks getTracks() {
            return tracks;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Tracks {
        public List<Item> items;
        public List<Item> getItems() {
            return items;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private static class Item {
        public String id;
        public String name;
        public ItemsAlbum album;
        public List<ItemsArtist> artists;
        public int durationMs;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ItemsAlbum {
        public String id;
        public List<ItemsArtist> artists;
        public String name;
        public List<CoverImage> images;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ItemsArtist {
        public String name;
    }

    private static String mapArtists(List<ItemsArtist> artists) {
        return artists.stream()
                .map(a -> a.name)
                .collect(Collectors.joining(" & "));
    }

    private static String mapDuration(int ms) {
        int min = ms / 60_000;
        int s = (ms % 60_000) / 1000;
        return min + ":" + s;
    }
}
