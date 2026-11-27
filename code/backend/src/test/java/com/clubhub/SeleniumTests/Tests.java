package com.clubhub.SeleniumTests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class Tests {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        driver.get("https://clubhub-production-07456829.up.railway.app/");
        System.out.println("Opening up page...");
        System.out.println("Title: " + driver.getTitle());

        // Testing profile 
        StudentTest jordan = new StudentTest("11983460", "Jordan", "Nguyen-Vo Herbert", "j.nguyen-voherbert@wsu.edu", "Computer Science", "123456");
        createProfile(driver, jordan);
        System.out.println("Profile creation completed successfully.");
        signOut(driver);
        System.out.println("Sign-out completed successfully.");
        homePage(driver);

        signIn(driver, jordan);
        System.out.println("Sign-in completed successfully.");
        driver.quit();
        System.out.println("All web tests completed successfully");

    }

    public static void createProfile(WebDriver driver, StudentTest student) {
        // Click Create Student profile
        System.out.println("Creating a Student Profile: ");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.findElement(By.linkText("Create a student profile")).click();

        driver.findElement(By.id("studentNumber")).sendKeys(student.getStudentNumber());
        driver.findElement(By.id("firstName")).sendKeys(student.getFirstName());
        driver.findElement(By.id("lastName")).sendKeys(student.getLastName());
        driver.findElement(By.id("email")).sendKeys((student.getEmail()));
        driver.findElement(By.id("major")).sendKeys(student.getMajor());

        driver.findElement(By.id("password")).sendKeys(student.getPassword());
        

        // To select from Options
        Select gender = new Select(driver.findElement(By.id("gender")));
        gender.selectByVisibleText("Man");

        Select pronouns = new Select(driver.findElement(By.id("pronouns")));
        pronouns.selectByVisibleText("he/him");

        driver.findElement(By.xpath("//button[text()='Create profile']")).click();
    }

    public static void homePage(WebDriver driver) {
        driver.findElement(By.linkText("Home")).click();
    }

    public static void signOut(WebDriver driver) {
        driver.findElement(By.xpath("//button[text()='Sign out']")).click();
    }

    public static void signIn(WebDriver driver, StudentTest student) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sign in"))).click();

        // Fill the form once the sign-in page has loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(student.getEmail());
        driver.findElement(By.id("password")).sendKeys(student.getPassword());
        // Click sign in button
        driver.findElement(By.xpath("//button[text()='Sign in']")).click();
    }


}