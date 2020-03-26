package pl.poznan.put.cs.project.spotifypartyplanner.rest.model.response.event;

import pl.poznan.put.cs.project.spotifypartyplanner.model.event.Suggestions;

public class SuggestionsResponse {
    public SuggestionsFromResponse fromGuests = new SuggestionsFromResponse();
    public SuggestionsFromResponse fromRecommendations = new SuggestionsFromResponse();

    public static SuggestionsResponse fromSuggestions(final Suggestions suggestions) {
        var response = new SuggestionsResponse();
        response.fromGuests = SuggestionsFromResponse.fromSuggestionsFrom(suggestions.getFromGuests());
        response.fromRecommendations = SuggestionsFromResponse.fromSuggestionsFrom(suggestions.getFromRecommendations());
        return response;
    }
}
