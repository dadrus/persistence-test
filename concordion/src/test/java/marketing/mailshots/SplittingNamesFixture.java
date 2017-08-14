package marketing.mailshots;

import static org.concordion.api.MultiValueResult.multiValueResult;

import org.concordion.api.MultiValueResult;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

@RunWith(ConcordionRunner.class)
public class SplittingNamesFixture {

    public MultiValueResult split(final String fullName) {
        final String[] words = fullName.split(" ");
        return multiValueResult().with("firstName", words[0]).with("lastName", words[1]);
    }
}
