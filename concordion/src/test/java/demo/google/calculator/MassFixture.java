package demo.google.calculator;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;

@RunWith(ConcordionRunner.class)
public class MassFixture extends GoogleBaseFixture {

    public String getConversionResult() {
        try {
            return resultsPage.getConversionResult();
        } catch (NoSuchElementException e) {
            return resultsPage.getTopResultTitle();
        }
    }
}
