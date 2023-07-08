package testutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.ValueMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventMatcher implements ValueMatcher<Request> {

    private final List<Map<String, Object>> expectedEvents;

    private final String idKey;

    private EventMatcher(final List<Map<String, Object>> expectedEvents, final String idKey) {
        this.expectedEvents = expectedEvents;
        this.idKey = idKey;
    }

    public static EventMatcher of(final List<Map<String, Object>> expectedEvents) {
        return new EventMatcher(expectedEvents, null);
    }

    public static EventMatcher of(final Map<String, Object> expectedEvent) {
        return new EventMatcher(List.of(expectedEvent), null);
    }

    // the same idKey will be expected in all events.
    public static EventMatcher of(final Map<String, Object> expectedEvent, final String idKey) {
        return new EventMatcher(List.of(expectedEvent), idKey);
    }

    @Override
    public MatchResult match(final Request request) {
        ObjectMapper om = new ObjectMapper();
        TypeReference<List<Map<String,Object>>> tr = new TypeReference<>(){};

        List<Map<String, Object>> bodyData;
        try {
            bodyData = om.readValue(request.getBody(), tr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<MatchResult> matchers = new ArrayList<>();

        if (bodyData.size() != expectedEvents.size()) {
            return MatchResult.noMatch();
        }

        for (int i = 0; i < expectedEvents.size(); i++) {
            Map<String, Object> event = expectedEvents.get(i);
            Map<String, Object> data = bodyData.get(i);

            // TODO: allow to match ID
            // +1 from auto inserted ID.
            matchers.add(MatchResult.of(event.size() + 1 == data.size()));
            event.forEach((key, value) -> {
                matchers.add(MatchResult.of(data.containsKey(key)));
                if (data.containsKey(key)) {
                    matchers.add(MatchResult.of(data.get(key).equals(value)));
                }
            });

            if (idKey != null) {
                matchers.add(MatchResult.of(data.containsKey(idKey)));
            }
        }

        return MatchResult.aggregate(matchers);
    }
}
