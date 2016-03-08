package com.sooreen.footstat.po;

public class MatchResult {
    private String champId;
    private String champName;
    private String leagueId;
    private String leagueName;
    private String seasonName;
    private String seasonId;
    private String gameDate;
    private String team1;
    private String team2;
    private String score1;
    private String score2;

    public String getChampId() {
        return champId;
    }

    public void setChampId(String champId) {
        this.champId = champId;
    }

    public String getChampName() {
        return champName;
    }

    public void setChampName(String champName) {
        this.champName = champName;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchResult that = (MatchResult) o;

        if (champId != null ? !champId.equals(that.champId) : that.champId != null) return false;
        if (gameDate != null ? !gameDate.equals(that.gameDate) : that.gameDate != null) return false;
        if (leagueId != null ? !leagueId.equals(that.leagueId) : that.leagueId != null) return false;
        if (score1 != null ? !score1.equals(that.score1) : that.score1 != null) return false;
        if (score2 != null ? !score2.equals(that.score2) : that.score2 != null) return false;
        if (seasonId != null ? !seasonId.equals(that.seasonId) : that.seasonId != null) return false;
        if (team1 != null ? !team1.equals(that.team1) : that.team1 != null) return false;
        if (team2 != null ? !team2.equals(that.team2) : that.team2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = champId != null ? champId.hashCode() : 0;
        result = 31 * result + (leagueId != null ? leagueId.hashCode() : 0);
        result = 31 * result + (seasonId != null ? seasonId.hashCode() : 0);
        result = 31 * result + (gameDate != null ? gameDate.hashCode() : 0);
        result = 31 * result + (team1 != null ? team1.hashCode() : 0);
        result = 31 * result + (team2 != null ? team2.hashCode() : 0);
        result = 31 * result + (score1 != null ? score1.hashCode() : 0);
        result = 31 * result + (score2 != null ? score2.hashCode() : 0);
        return result;
    }
}
