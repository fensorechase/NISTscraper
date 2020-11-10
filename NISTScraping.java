package newpackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/*Dependencies:
 * ChromeDriver file path
 * .jar files- must be added to project library
 */
// TEST FILE: D:\testCAS.txt

public class NISTScraping {

    public static void irScraping(Scanner in) throws IOException {

        final long startTime = System.currentTimeMillis();

        //FILE REQUIREMENTS:
        //File Format: .txt (text file)
        //Data: one \n separating each NIST compound ID number
        //eg.) 7647-14-5 \n 10043-52-4

        /* Read CAS IDs from input file */
        System.out.print("Enter path of (.txt) file to read: ");
        //COMPOUND FILE FORMAT: ( CASid \n CASid)
        BufferedReader checkFile;
        boolean checkError = false;
        HashSet<String> compoundList = new HashSet<>();
        try {
            checkFile = new BufferedReader(new FileReader(in.nextLine())); //FileReader

        } catch (IOException e) {
            checkError = true;
            System.err.print("Error opening file");
            in.close();
            return;
        }
        if (!checkError) { //load into reference Set

            String casID = checkFile.readLine().replaceAll("[^0-9]", "");
            /*
             * if (name.isEmpty() && (!casID.isEmpty())) { //If empty name, but
             * has IDnum name = "Unnamed Compound"; //Default value }
             */
            while ((casID != null)) {
                compoundList.add(casID);
                casID = checkFile.readLine();
                if (casID != null) {
                    casID.replaceAll("[^0-9]", "");
                }
            }
        }
        checkFile.close(); // Close compound file

        //Create output file of CAS ID's downloaded.
        // if (success click to download) then {write line to file\n}

        System.out.println();
        System.out.print("Enter path of output (.txt) file: ");

        String outputPath = in.nextLine(); //path of output file
        BufferedWriter bw = null;
        try {
            FileWriter outputCASFile = new FileWriter(outputPath);
            bw = new BufferedWriter(outputCASFile);

            /* Begin Selenium operations */
            HashMap<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory",
                    System.getProperty("user.dir"));

            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePrefs);
            //NEW USER: modify Chrome driver file path based on OS, and Chrome version.
            //Link to download Chrome Driver: https://chromedriver.storage.googleapis.com/index.html
            System.setProperty("webdriver.chrome.driver",
                    "C:\\Users\\cmf20\\Downloads\\chromedriver_win32 (2)\\chromedriver.exe");
            //OLD CHROMEDRIVER PATH: "C:\\Users\\cmf20\\Downloads\\chromedriver_win32\\chromedriver.exe"
            WebDriver driver = new ChromeDriver(options);

            Iterator<String> it = compoundList.iterator();
            /* Iterate over List while there's more files to download */
            //System.out.println(compoundList.size());

            while (it.hasNext()) {
                String comp = it.next();
                driver.navigate()
                        .to("https://webbook.nist.gov/cgi/cbook.cgi?ID=C" + comp
                                + "&Mask=80#IR-Spec");

                // Find download link
                List<WebElement> list = driver.findElements(
                        By.cssSelector("div.indented > p:nth-child(3) > a"));
                //#main > div.indented > p:nth-child(3) > a
                //LIST: contains 0 or 1 compound WebElement to click on.

                // Click to download file
                if (list.size() > 0) {
                    WebElement el = list.get(list.size() - 1);
                    el.click();
                    //OPTIONAL TO ADD: could print out compName from web page.
                    System.out.println("File downloaded for compound.");
                    bw.write(comp + "\n"); // Write each downloaded compound to output file.

                } else {
                    /* TODO: here, use 2ndary URL approach */
                    driver.navigate()
                            .to("https://webbook.nist.gov/cgi/cbook.cgi?ID="
                                    + comp + "&Type=IR-SPEC&Index=1#IR-SPEC");

//Find download link
                    List<WebElement> listSecondary = driver.findElements(By
                            .cssSelector("div.indented > p:nth-child(4) > a"));
                    // Append 3rd <p>, sometimes click must be here.
                    //listSecondary.add(driver.findElement(By.cssSelector("div.indented > p:nth-child(3) > a")));
                    if (listSecondary.size() > 0) {

                        WebElement eL = listSecondary
                                .get(listSecondary.size() - 1);
                        eL.click();

                        System.out.println(
                                "File downloaded for compound (Secondary click).");
                        bw.write(comp + "\n"); //Write downloaded compound to output file.
                    }

                    else { // ERROR: No NIST IR spectra exists for this compound.

                        System.err.println("Error on NIST click download.");
                    }
                }
                list.clear(); // Clear for next iteration
            } // END WHILE

            /* END OF OUTPUT FILE BLOCK */
            System.out.println("File written successfully.");
        } catch (IOException e) {
            in.close(); // Close Scanner
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                System.out.print("Error in closing the BufferedWriter" + ex);
                in.close(); // Close Scanner
            }
        }
        in.close(); // Close Scanner

        final long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        System.out.println(elapsedTimeMillis);

    }

    public static void massSpecScraping(Scanner in) throws IOException {
        final long startTime = System.currentTimeMillis();

        //FILE REQUIREMENTS:
        //File Format: .txt (text file)
        //Data: one \n separating each NIST compound ID number
        //eg.) 7647-14-5 \n 10043-52-4

        /* Read CAS IDs from input file */
        System.out.print("Enter path of (.txt) file to read: ");
        //Scanner in = new Scanner(System.in);
        //COMPOUND FILE FORMAT: ( CASid \n CASid)
        BufferedReader checkFile;
        boolean checkError = false;
        HashSet<String> compoundList = new HashSet<>();
        try {
            checkFile = new BufferedReader(new FileReader(in.nextLine())); //FileReader

        } catch (IOException e) {
            checkError = true;
            System.err.print("Error opening file");
            in.close();
            return;
        }
        if (!checkError) { //load into reference Set

            String casID = checkFile.readLine().replaceAll("[^0-9]", "");
            /*
             * if (name.isEmpty() && (!casID.isEmpty())) { //If empty name, but
             * has IDnum name = "Unnamed Compound"; //Default value }
             */
            while ((casID != null)) {
                compoundList.add(casID);
                casID = checkFile.readLine();
                if (casID != null) {
                    casID.replaceAll("[^0-9]", "");
                }
            }
        }
        checkFile.close(); // Close compound file

        // Here, create output file of CAS ID's downloaded.
        // if (success click to download) then {write line to file\n}

        System.out.println();
        System.out.print("Enter path of output (.txt) file: ");

        String outputPath = in.nextLine(); //path of output file
        BufferedWriter bw = null;
        try {
            FileWriter outputCASFile = new FileWriter(outputPath);
            bw = new BufferedWriter(outputCASFile);

            /* Begin Selenium operations */
            HashMap<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory",
                    System.getProperty("user.dir"));

            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePrefs);
            //TODO: modify Chrome driver file path based on OS, and Chrome version.
            //Link to download Chrome Driver: https://chromedriver.storage.googleapis.com/index.html
            System.setProperty("webdriver.chrome.driver",
                    "C:\\Users\\cmf20\\Downloads\\chromedriver_win32 (2)\\chromedriver.exe");
            //OLD CHROMEDRIVER PATH: "C:\\Users\\cmf20\\Downloads\\chromedriver_win32\\chromedriver.exe"
            WebDriver driver = new ChromeDriver(options);

            Iterator<String> it = compoundList.iterator();
            /* Iterate over List while there's more files to download */
            //System.out.println(compoundList.size());

            while (it.hasNext()) {
                String comp = it.next();
                driver.navigate()
                        .to("https://webbook.nist.gov/cgi/cbook.cgi?ID=C" + comp
                                + "&Mask=200#Mass-Spec");

                // Find download link
                List<WebElement> list = driver.findElements(
                        //TODO MASS SPEC

                        By.cssSelector("div.indented > p:nth-child(3) > a"));
                //#main > div.indented > p:nth-child(3) > a
                //LIST: contains 0 or 1 compound WebElement to click on.

                // Click to download file
                if (list.size() > 0) {
                    WebElement el = list.get(list.size() - 1);
                    el.click();
                    //OPTIONAL TO ADD: could print out compName from web page.
                    System.out.println("File downloaded for compound.");
                    bw.write(comp + "\n"); // Write each downloaded compound to output file.

                } else {

                    System.err.println(
                            "No 2ndary: Error on NIST click download.");
                    /* Below, use 2ndary URL approach */
                    //Chase: Leaving this out. No 2ndary approach for Mass Spec.
                    /*
                     * driver.navigate()
                     * .to("https://webbook.nist.gov/cgi/cbook.cgi?ID=" + comp +
                     * "&Type=IR-SPEC&Index=1#IR-SPEC");
                     *
                     * //Find download link List<WebElement> listSecondary =
                     * driver.findElements(By
                     * .cssSelector("div.indented > p:nth-child(4) > a")); //
                     * Append 3rd <p>, sometimes click must be here.
                     * //listSecondary.add(driver.findElement(By.
                     * cssSelector("div.indented > p:nth-child(3) > a"))); if
                     * (listSecondary.size() > 0) {
                     *
                     * WebElement eL = listSecondary .get(listSecondary.size() -
                     * 1); eL.click();
                     *
                     * System.out.println(
                     * "File downloaded for compound (Secondary click).");
                     * bw.write(comp + "\n"); //Write downloaded compound to
                     * output file. }
                     *
                     * else { // ERROR: No NIST IR spectra exists for this
                     * compound.
                     *
                     * System.err.println("Error on NIST click download."); }
                     */

                }
                list.clear(); // Clear for next iteration
            } // END WHILE

            /* END OF OUTPUT FILE BLOCK */
            System.out.println("File written successfully.");
        } catch (IOException e) {
            in.close(); // Close Scanner
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                System.out.print("Error in closing the BufferedWriter" + ex);
                in.close(); // Close Scanner
            }
        }
        in.close(); // Close Scanner

        final long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        System.out.println(elapsedTimeMillis);

    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        System.out.print(
                "Would you like to run IR Scraping(enter 1), or Mass Spec Scraping(enter 2)?: ");

        String userChoice = in.nextLine();

        String regex = "[+]?[1-2]"; // integer 1 or 2

        // compiles regex, and creates a pattern matcher
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(userChoice);

        boolean validInput = false;
        while (validInput != true) {
            if (m.find() && m.group().equals(userChoice)) {
                // valid user inputs
                validInput = true;
            } else {
                System.out.print("Invalid choice. Enter 1 or 2: ");

            }
        }
        // Valid user input, run appropriate method
        int userNum = Integer.parseInt(userChoice);
        if (userNum == 1) {
            irScraping(in); // begin scraping procedure for IR data from NIST webbook
        }
        if (userNum == 2) {
            massSpecScraping(in); // begin scraping procedure for mass spec data from NIST webbook
        } else {
            System.out.println("Enter a valid number.");
        }

    }

}
