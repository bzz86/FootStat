package com.sooreen.footstat;

import com.sooreen.footstat.po.Championate;
import com.sooreen.footstat.po.MatchResult;
import com.sooreen.footstat.po.ResultLine;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public abstract class AbstractStatParser {
    public abstract String prepareUrl(String ... args);
    public abstract List<MatchResult> parseResults(List<Championate> champs) throws IOException;
    public abstract List<ResultLine> parseDocument(Document doc);
}
