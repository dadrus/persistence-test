package demo.google.calculator;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;

@RunWith(ConcordionRunner.class)
public class ConstantsFixture extends GoogleBaseFixture {

    public String getConstantResult() {
        try {
            return resultsPage.getConstantResult();
        } catch (NoSuchElementException e) {
            return resultsPage.getCalculatorResult();
        }
    }
}
