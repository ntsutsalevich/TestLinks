import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//import java.util.stream.Collectors;

public class Top20LinksChecker {

    WebDriver driver;
    WebDriverWait wait;

@BeforeTest
    public void startDriver() {
        System.setProperty(
                "webdriver.chrome.driver",
                "D:/Test/chromedriver.exe"); //F:\ChromeDriver

        driver = new ChromeDriver();

    }


    @AfterTest
    public void stopDriver() {
        driver.close();
    }

    @Test
    public void findTrivagoLink() throws IOException {

        ArrayList<String> pages = new ArrayList<String>();
        ArrayList<String> pagelinks = new ArrayList<String>();
        ArrayList<String> links = new ArrayList<String>();
        ArrayList<String> results = new ArrayList<String>();
        String inputFile = "D:/Test/top10.txt";
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String str;
        while ((str = reader.readLine()) != null) {
            pagelinks.add(str);
        }
        for (int k = 0; k < pagelinks.size(); k++) {
            String url = pagelinks.get(k);
            try {
                driver.manage().window().maximize();
                driver.get(url);
                //close modal prompts
//                if(url.contains("funcustomcreations")) {
//                    driver.findElement(By.xpath("//*[@id='appModalModal']/button/i")).click();
//                    driver.findElement(By.xpath("//*[@id='ProductSearchOnBoardingModalModal']/button/i")).click();
//                }
            } catch (InvalidArgumentException e) {
                System.out.println("invalid url " + url);
            } catch (StaleElementReferenceException a) {
                System.out.println(url + " stale element happened here get url");
            }

            try {
               // Thread.sleep(10000);
                wait=new WebDriverWait(driver,10);

            }  catch (StaleElementReferenceException a) {
                System.out.println(url + " stale element happened here maximixe");
            }
            try {
                WebElement toolbarSet = driver.findElement(By.cssSelector("div.outlet.clearfix.topOutlet"));
                List<WebElement> toolbarButtons = toolbarSet.findElements(By.cssSelector("a"));
                System.out.println(url + " toolbarButtons: " + toolbarButtons.size());
                for (WebElement element : toolbarButtons) {
                    try {
                        System.out.println(element.getAttribute("href"));// simple links
                        links.add(element.getAttribute("href") + " " + url);
                        //element.click();
                    } catch (ElementNotInteractableException e) {
                        System.out.println(element.getText() + " is not interactible.");
                    } catch (StaleElementReferenceException a) {
                        System.out.println(url + " stale element happened here while clicking");
                    }

                }
                List<WebElement> toolbarSubMenues = toolbarSet.findElements(By.cssSelector("div.menu-button > div.toolbar-button"));
                // driver.findElement(By.cssSelector("div.menu-button > div.toolbar-button")).click();
                //Thread.sleep(1000);
                System.out.println("submenue items " + toolbarSubMenues.size());

                for (WebElement element : toolbarSubMenues) { //#root > div > div > div.outlet.clearfix.topOutlet > div > div > div.toolbar-menu-container > div > div > div > div > div > div:nth-child(1) > div > a
                    try {
                        element.click();
                        Thread.sleep(1000);
                        WebElement items = driver.findElement(By.cssSelector("div.menu-items"));
                        List<WebElement> toolbarSubMenueItems = items.findElements(By.cssSelector("a"));
                        for (WebElement elem : toolbarSubMenueItems) {
                            String href = elem.getAttribute("href");
                            System.out.println(href); // first level submenu links
                            links.add(href + " " + url);
                        }
                    } catch (Exception e) {

                        System.out.println("no href");
                    }
                }

            } catch (NoSuchElementException e) {
                System.out.println(url + " does not have a toolbar ");
            }

// *************Ready Code***************************

            links.addAll(CollectTilesLinks(url, "ul.shows-list.cf", "li.shows-list-item"));


            links.addAll(CollectTilesLinks(url, "ul.tiles-list", "div.chiclet-button"));

            links.addAll(CollectChicletLinks(url));
//******************************************************
//            String a = driver.findElement(By.cssSelector("div.outlet.clearfix.chicletsOutlet.chiclets > a:nth-child(6)")).getAttribute("href");
//            if (a.contains("amp")) {
//                pages.add(a);
//            }

        }
        System.out.println("All links:");
        System.out.println("Total number of links: " + links.size());
        int count = 0;
        for (String link : links) {

            if (!link.contains("amp") && !link.contains("redirect") &&
                    !link.contains("kqzy") && !link.contains("jdoq")) {
                //System.out.println(link);
                getResponseCode(link);
                count++;
            }
        }
        System.out.println("Links without redirect: " + count);
        //  String baseUrl = "https://hp.myway.com/propdfconverter/ttab02chr/";


//        for (int i = 0; i < pages.size(); i++) {
//            System.out.println(pages.get(i));
//        }

    }

    private ArrayList CollectChicletLinks(String url) {
        ArrayList<String> links = null;
        try {
            links = new ArrayList<String>();
            WebElement chicletSet = driver.findElement(By.cssSelector("div.outlet.clearfix.chicletsOutlet.chiclets"));
            List<WebElement> chiclets = chicletSet.findElements(By.cssSelector("a"));
            System.out.println(url + " chiclets: " + chiclets.size());
            for (WebElement element : chiclets) {
                String chicletUrl = element.getAttribute("href");

                if ((!chicletUrl.contains("amp") || (!chicletUrl.contains("redirect") ||
                        (!chicletUrl.contains("kqzy") || (!chicletUrl.contains("jdoq")))))) {
                    System.out.println(url + " chiclets: " + chicletUrl); // chiclet link
                    links.add(chicletUrl + " " + url);
                    //element.click();
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("no chiclets on this product " + url);
        }
        return links;
    }

    private ArrayList CollectTilesLinks(String url, String selector, String sel) {
        ArrayList<String> links = null;
        try {
            links = new ArrayList<String>();
            WebElement tilesSet = driver.findElement(By.cssSelector(selector));

            List<WebElement> tiles = tilesSet.findElements(By.cssSelector(sel));
            System.out.println(url + " tiles: " + tiles.size());
            for (WebElement element : tiles) {
                element.click();

            }
            ArrayList<String> tabs = new ArrayList(driver.getWindowHandles());//Получение списка табов
            for (int i = 1; i < tabs.size(); i++) {
                driver.switchTo().window(tabs.get(i));
                System.out.println(driver.getCurrentUrl()); // tiles link
                links.add(driver.getCurrentUrl() + " " + url);
                driver.close();
            }
            driver.switchTo().window(tabs.get(0));
        } catch (NoSuchElementException e) {
            System.out.println("no tiles on this product " + url);

        }

        return links;
    }


    public static void getResponseCode(String urlString) throws IOException {
        //test results for top 20 _date_ dd-mm-yyyy _time_ hh-MM

        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date currentDay = new Date();
        FileWriter fw = new FileWriter("D:results for top 20 " + df.format(currentDay) + ".txt", true);

        try {
            String substr = urlString.substring(0, urlString.indexOf(" "));
            String page = urlString.substring(urlString.indexOf(" "));
            URL u = new URL(substr);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            huc.connect();
            if (huc.getResponseCode() > 399) {
                System.out.println(substr + " response code = " + huc.getResponseCode() + " on page: " + page);
                fw.write(substr + " response code = " + huc.getResponseCode() + " on page: " + page);
                fw.write("\n");
            }
            //System.out.println(substr + " response code = " + huc.getResponseCode());
        } catch (IOException e) {
            System.out.println("failed to get a response code TIMEOUT: " + urlString);
            fw.write("failed to get a response code TIMEOUT: " + urlString);
            fw.write("\n");
        }
        fw.close();

    }
}




