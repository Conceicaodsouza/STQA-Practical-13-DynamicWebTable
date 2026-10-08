package practical;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DynamicWebTableTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void dynamicWebTableTest() {

        String filePath = System.getProperty("user.dir")
                + "/src/test/resources/dynamic-table.html";

        driver.get("file://" + filePath);

        // Wait until dynamically generated rows appear
        wait.until(ExpectedConditions
                .numberOfElementsToBeMoreThan(
                        By.cssSelector("#tableBody tr"), 0));

        // -------------------------------------------------
        // 1. Extract table data
        // -------------------------------------------------

        List<WebElement> rows =
                driver.findElements(By.cssSelector("#tableBody tr"));

        System.out.println("====================================");
        System.out.println("DYNAMIC WEB TABLE DATA");
        System.out.println("====================================");

        System.out.println("Total rows: " + rows.size());

        assertEquals("Table should contain 10 students",
                10, rows.size());

        for (WebElement row : rows) {

            List<WebElement> cells =
                    row.findElements(By.tagName("td"));

            System.out.println(
                    cells.get(0).getText() + " | "
                    + cells.get(1).getText() + " | "
                    + cells.get(2).getText() + " | "
                    + cells.get(3).getText() + " | "
                    + cells.get(4).getText()
            );
        }

        // -------------------------------------------------
        // 2. Validate ascending sorting
        // -------------------------------------------------

        driver.findElement(By.id("sortMarks")).click();

        wait.until(ExpectedConditions
                .numberOfElementsToBe(
                        By.cssSelector("#tableBody tr"), 10));

        List<Integer> ascendingMarks =
                getMarksFromTable();

        System.out.println();
        System.out.println("Ascending marks:");
        System.out.println(ascendingMarks);

        assertTrue(
                "Marks should be sorted in ascending order",
                isAscending(ascendingMarks)
        );

        // -------------------------------------------------
        // 3. Validate descending sorting
        // -------------------------------------------------

        driver.findElement(By.id("sortMarks")).click();

        List<Integer> descendingMarks =
                getMarksFromTable();

        System.out.println();
        System.out.println("Descending marks:");
        System.out.println(descendingMarks);

        assertTrue(
                "Marks should be sorted in descending order",
                isDescending(descendingMarks)
        );

        // -------------------------------------------------
        // 4. Apply Pass filter
        // -------------------------------------------------

        WebElement filter =
                driver.findElement(By.id("resultFilter"));

        filter.click();

        filter.findElement(
                By.cssSelector("option[value='Pass']")
        ).click();

        wait.until(ExpectedConditions
                .numberOfElementsToBe(
                        By.cssSelector("#tableBody tr"), 6));

        List<WebElement> filteredRows =
                driver.findElements(By.cssSelector("#tableBody tr"));

        System.out.println();
        System.out.println("====================================");
        System.out.println("FILTERED RESULTS - PASS");
        System.out.println("====================================");

        System.out.println(
                "Filtered rows: " + filteredRows.size()
        );

        assertEquals(
                "Pass filter should display 6 students",
                6,
                filteredRows.size()
        );

        // -------------------------------------------------
        // 5. Verify every filtered row is Pass
        // -------------------------------------------------

        for (WebElement row : filteredRows) {

            List<WebElement> cells =
                    row.findElements(By.tagName("td"));

            String result = cells.get(4).getText();

            System.out.println(
                    cells.get(0).getText()
                    + " - "
                    + cells.get(1).getText()
                    + " - Result: "
                    + result
            );

            assertEquals(
                    "Every filtered row must have Pass result",
                    "Pass",
                    result
            );
        }

        System.out.println();
        System.out.println("====================================");
        System.out.println("ALL VALIDATIONS PASSED");
        System.out.println("====================================");
    }

    // -------------------------------------------------
    // Extract marks from table
    // -------------------------------------------------

    private List<Integer> getMarksFromTable() {

        List<Integer> marks = new ArrayList<>();

        List<WebElement> rows =
                driver.findElements(By.cssSelector("#tableBody tr"));

        for (WebElement row : rows) {

            List<WebElement> cells =
                    row.findElements(By.tagName("td"));

            int mark =
                    Integer.parseInt(cells.get(3).getText());

            marks.add(mark);
        }

        return marks;
    }

    // -------------------------------------------------
    // Check ascending order
    // -------------------------------------------------

    private boolean isAscending(List<Integer> values) {

        for (int i = 0; i < values.size() - 1; i++) {

            if (values.get(i) > values.get(i + 1)) {
                return false;
            }
        }

        return true;
    }

    // -------------------------------------------------
    // Check descending order
    // -------------------------------------------------

    private boolean isDescending(List<Integer> values) {

        for (int i = 0; i < values.size() - 1; i++) {

            if (values.get(i) < values.get(i + 1)) {
                return false;
            }
        }

        return true;
    }

    @After
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}