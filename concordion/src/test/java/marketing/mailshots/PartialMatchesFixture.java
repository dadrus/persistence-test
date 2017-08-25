package marketing.mailshots;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class PartialMatchesFixture {

    private List<String> names = new ArrayList<>();

    public void setUpUser(final String user) {
        names.add(user);
    }

    public List<String> getSearchResultsFor(final String str) {
        return names.stream().filter(e -> e.contains(str)).sorted().collect(Collectors.toList());
    }
}
