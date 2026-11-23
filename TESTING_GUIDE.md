# Goibibo Selenium Testing Guide

This guide describes how to create, configure, execute, and review UI tests in the
`GoibiboSeleniumJava` Maven project.

## 1. Prerequisites

- JDK 21
- Maven 3.9 or later
- Chrome, Edge, or Firefox, depending on the selected browser
- Internet access to open Goibibo

The project uses Maven to download dependencies and WebDriverManager to set up
browser drivers.

## 2. Understand the test structure

- `src/test/java/com/sl/GoibiboSeleniumJava/testcases/` contains TestNG test
  classes and `BaseTest`.
- `src/main/java/com/sl/GoibiboSeleniumJava/pages/` contains page objects and
  reusable page interactions.
- `src/main/java/com/sl/GoibiboSeleniumJava/factory/DriverFactory.java` creates
  and manages browser drivers.
- `src/main/java/com/sl/GoibiboSeleniumJava/listeners/TestListener.java`
  handles test reporting and failure artifacts.
- `src/main/resources/config/config.properties` contains default browser and
  site configuration.
- `testng.xml` lists the suite's test classes and listener.

## 3. Create a test case

1. Add a Java class under
   `src/test/java/com/sl/GoibiboSeleniumJava/testcases/`.
2. Name it with the `Test_` prefix, for example `Test_GoibiboFlightSearch`.
3. Extend `BaseTest` to reuse browser setup and teardown.
4. Add a TestNG `@Test` method.
5. Use page objects from `src/main/java/.../pages/` for UI actions. Add or
   update page-object methods rather than putting long locator sequences in a
   test method.
6. Add assertions for observable outcomes, such as a results heading or
   selected route. Avoid treating a URL-only check as proof that a booking
   completed.

Example:

```java
package com.sl.GoibiboSeleniumJava.testcases;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Test_GoibiboFlightSearch extends BaseTest {

    @Test(description = "Verify flight search results are displayed")
    public void searchFlights() {
        Assert.assertTrue(
            driver.getTitle().toLowerCase().contains("goibibo"),
            "The Goibibo page title was not displayed"
        );
    }
}
```

Use the relevant page objects to perform the flight search, then replace the
page-title assertion with a check for the actual results the test is intended
to verify.

## 4. Configure the test

Edit `src/main/resources/config/config.properties` to set defaults:

```properties
baseUrl=https://www.goibibo.com/
browser=chrome
headless=false
```

`BaseTest` reads these values before each test. The browser can be `chrome`,
`edge`, or `firefox`. The suite can also provide `browser` and `headless`
parameters in `testng.xml`.

Tests interact with Goibibo's live website, so page changes, availability, or
network conditions can affect results. Use only dummy test data; do not enter
real payment details or complete a real booking.

## 5. Add the test to the suite

To include the new test in the standard suite, add its fully qualified class
name inside the `<classes>` element in `testng.xml`:

```xml
<class name="com.sl.GoibiboSeleniumJava.testcases.Test_GoibiboFlightSearch"/>
```

The existing suite includes `Test_GoibiboQuickLaunch` and
`Test_GoibiboFlightBooking`.

## 6. Execute tests

Run commands from the project root:

```powershell
cd D:\Simplilearn\Java-Projects\GoibiboSeleniumJava
```

Run one test class:

```powershell
mvn test -Dtest=Test_GoibiboFlightBooking
```

Run the configured TestNG suite:

```powershell
mvn test
```

If `mvn` is not available on `PATH`, run Maven using its installed
`bin\mvn.cmd` path instead.

## 7. Review results

- Maven prints the test totals and build status in the terminal.
- Surefire results are written under `target/surefire-reports/`.
- The TestNG listener saves failure screenshots and page source under
  `target/screenshots/` when capture succeeds.
- TestNG/ExtentReports output may also be written under `test-output/` or the
  configured report location.

Investigate the first failing assertion or Selenium exception, then update the
relevant page object or test expectation and rerun the affected test.

## Current coverage note

`Test_GoibiboFlightBooking` exercises the flight-search and booking-page
navigation with dummy passenger/payment inputs. Its final assertion currently
checks that the URL contains `goibibo.com`; it does not verify a real booking
confirmation. The project currently has no REST API module or flight/seat
database because Goibibo does not publish a universal, open public REST API with a single standard base URL; 
instead, access is typically handled through approved partner/channel manager integrations or third-party data/API wrappers.
So have done it using Postman.

## Current test algorithms

These are descriptions of the behavior currently implemented; they do not
guarantee that Goibibo will complete a real booking.

### Browser setup and cleanup

1. Load `baseUrl`, `browser`, and `headless` from `config.properties`.
2. Create the selected browser driver using `DriverFactory`.
3. Open the configured site and wait for the departure-city input. If it does
   not appear, refresh once; the test flow reports any remaining UI problem.
4. After the test method finishes, close the browser and remove its
   thread-local driver.

### Flight search and selection

1. Look for and close the optional login popup.
2. Enter the departure city and select a matching suggestion. If suggestions
   are unavailable, use keyboard selection.
3. Enter and select the destination in the same way.
4. Wait for navigation from the home page; if it remains on the home page,
   attempt the visible search control. If the departure input is unavailable,
   navigate to the flight-search URL directly.
5. On the results page, wait for a “View Fares” or “Book” control. Click the
   first fare option and then the first booking control when they are present.
   If live results are unavailable on the flight-search route, log that state
   and continue with navigation-level verification.

### Passenger, payment, and final verification

1. Look for passenger name fields and fill them when available; otherwise log
   that the form is not available.
2. Use the supplied dummy card value only if a matching payment field exists.
   Click a payment button only if one is present; never use real payment
   details.
3. Assert that the current URL belongs to `goibibo.com`.

The last check confirms only site navigation. It does not prove that a flight
was selected, payment succeeded, or a booking was confirmed.

## Push the project to GitHub from Eclipse

1. Run the relevant test and review the changes before publishing.
2. Create an empty repository on GitHub. Since this project already has a
   local Git repository, do not initialize the GitHub repository with another
   README, license, or `.gitignore`.
3. In Eclipse, open **Window → Show View → Other… → Git → Git Staging** and
   refresh the view.
4. Review **Unstaged Changes**. Stage only project source, configuration that
   contains no secrets, build files, and documentation. Do not stage generated
   output, screenshots, logs, credentials, or tokens. The root `.gitignore`
   excludes Maven `target/` output and TestNG `test-output/`.
5. Enter a commit message such as `Add Selenium flight booking tests and guide`
   and select **Commit**.
6. In **Git Repositories**, configure a remote named `origin` using the HTTPS
   URL shown by GitHub, for example
   `https://github.com/<your-account>/<your-repository>.git`.
7. Push the current branch using **Team → Remote → Push Branch…** (menu names
   can vary by Eclipse version). Authenticate through Eclipse/GitHub's
   supported sign-in flow. Do not put an access token in source files or
   documentation.
8. Open the GitHub repository in a browser and confirm the commit, source
   files, and `TESTING_GUIDE.md` are present.
