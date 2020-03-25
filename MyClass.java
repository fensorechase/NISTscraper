package newpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/*Dependencies
 * ChromeDriver file path
 * .jar files- must be added to project library
 */

public class MyClass {

    public static void main(String[] args) throws IOException {

        //FILE REQUIREMENTS:
        //File Format: .txt (text file)
        //Data: \n or compound name separating each NIST ID number
        //eg.) Na \n 7647-14-5 \n Ca \n 10043-52-4

        /* Read CAS IDs from input file */
        System.out.print("Enter name of .txt file to read: ");
        Scanner in = new Scanner(System.in);
        //COMPOUND FILE: (name \n CASid)
        BufferedReader checkFile;
        boolean checkError = false;
        HashMap<String, String> compoundList = new HashMap<>();
        try {
            checkFile = new BufferedReader(new FileReader(in.nextLine())); //FileReader

        } catch (IOException e) {
            checkError = true;
            System.err.print("Error opening file");
            in.close();
            return;
        }
        if (!checkError) { //load into reference Set
            String name = checkFile.readLine(); // may be \n

            String casID = checkFile.readLine().replaceAll("[^0-9]", "");
            /*
             * if (name.isEmpty() && (!casID.isEmpty())) { //If empty name, but
             * has IDnum name = "Unnamed Compound"; //Default value }
             */
            while ((name != null)) { // TODO: ***FIX SO ITLL READ NEWLINE AS NAME
                compoundList.put(name, casID);
                name = checkFile.readLine();
                if (name != null) {
                    casID = checkFile.readLine().replaceAll("[^0-9]", "");
                }

            }
        }
        checkFile.close(); // Close compound file
        in.close(); // Close Scanner

        /* Begin Selenium operations */
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory",
                System.getProperty("user.dir"));

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        //TODO: modify Chrome driver file path
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\cmf20\\Downloads\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(options);

        Iterator<Entry<String, String>> it = compoundList.entrySet().iterator();
        /* Iterate over List while there's more files to download */
        System.out.println(compoundList.size());

        while (it.hasNext()) {
            Entry<String, String> comp = it.next();
            driver.navigate().to("https://webbook.nist.gov/cgi/cbook.cgi?ID=C"
                    + comp.getValue() + "&Mask=80#IR-Spec");

            // Find download link
            List<WebElement> list = driver.findElements(
                    By.cssSelector("div.indented > p:nth-child(3) > a")); //[a href^='/cgi/cbook.cgi?JCAMP=']
            //*[@id="main"]/div[3]/p[3]/a
            //#main > div.indented > p:nth-child(3) > a

            // Click to download file
            if (list.size() > 0) {
                WebElement el = list.get(list.size() - 1);
                el.click();
                String compName = comp.getKey();
                System.out.println("File downloaded for " + compName + ".");
            } else {
                System.err.println("Error on NIST click download.");
            }
            list.clear(); // Clear for next iteration
        } // END WHILE

        // TODO: SAVE FILE loop
        //File folder = new File(System.getProperty("user.dir"));

        /* Populate list of CAS id's */
        //TODO: make this file IO with csv file? alternate name, id

    }

}
