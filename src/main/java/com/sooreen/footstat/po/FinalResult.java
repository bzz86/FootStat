package com.sooreen.footstat.po;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: TOXA
 * Date: 3/8/16
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class FinalResult {
    private Date lastUpdate;
    private List<MatchResult> matchResults;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<MatchResult> getMatchResults() {
        return matchResults;
    }

    public void setMatchResults(List<MatchResult> matchResults) {
        this.matchResults = matchResults;
    }
}
