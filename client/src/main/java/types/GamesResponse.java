package types;

import java.util.Collection;

public class GamesResponse {
    private Collection<ListGamesResult> games;

    public Collection<ListGamesResult> getGames() {
        return games;
    }
}
