package pl.poznan.put.cs.project.spotifypartyplanner.model;

public class Track {
    private String id;
    private String name;
    private String artist;
    private Album album;
    private String duration;

    public Track() {}

    public Track(String id) {
        this.id = id;
    }

    public Track(String id, String name, String artist, Album album, String duration) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
