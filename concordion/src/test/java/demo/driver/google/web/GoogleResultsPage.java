package demo.driver.google.web;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.concordion.selenium.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A WebDriver Page Object corresponding to the Google Results Page.
 */
public class GoogleResultsPage {

    @FindBy(id = "res")
    private WebElement resultWrapper;

    @FindBy(className = "g")
    private WebElement firstResultLink;

    @FindBy(id = "cwos")
    private WebElement calcResultLink;

    @FindBy(css = ".vk_ans")
    private WebElement constantResultLink;

    @FindBy(css = "#_Cif > input")
    private WebElement conversionResultLink;

    private final WebDriver driver;

    /**
     * Initialises the results page and waits for the page to fully load. Assumes that the results
     * page is already loading.
     */
    public GoogleResultsPage(final Browser browser, final String query) {
        driver = browser.getDriver();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        PageFactory.initElements(driver, this);
        waitForTitleStartingWith(query);
    }

    /**
     * Checks whether the specified text occurs in any result on the results page.
     */
    public boolean resultsContain(final String text) {
        final List<WebElement> resultsText = resultWrapper.findElements(By.className("s"));
        for (final WebElement result : resultsText) {
            if (result.getText().contains(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the text of the topmost result from the results page.
     */
    public String getTopResultTitle() {
        return firstResultLink.getText().trim();
    }

    /**
     * Returns the text of the topmost result from the results page.
     */
    public String getCalculatorResult() {
        return calcResultLink.getText().trim();
    }

    public String getConstantResult() {
        return constantResultLink.getText().trim();
    }

    public String getConversionResult() {
        return conversionResultLink.getAttribute("value").trim();
    }

    private void waitForTitleStartingWith(final String query) {
        final WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(final WebDriver d) {
                return d.getTitle().startsWith(query);
            }
        });
    }
}