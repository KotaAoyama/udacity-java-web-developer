package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private WebDriverWait wait;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		this.driver.manage().window().maximize();
		this.wait = new WebDriverWait(driver, 1000);
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	@Order(1)
	public void testUserUnAuthorized() {
		goToPage("/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(2)
	public void testUserSignUpAndLoginAndLogout() {
		signUp();
		login();
		goToPage("/home");
		Assertions.assertEquals("Home", driver.getTitle());
		this.clickButton("logoutButton");
		this.goToPage("/home");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	@Order(3)
	public void testCreateNote() {
		signUp();
		login();
		goToPage("/home");
		this.clickButton("nav-notes-tab");
		WebElement tableBodyBeforeCreatingNote = driver.findElement(By.id("noteTableBody"));
		List<WebElement> rowsBeforeCreatingNote = tableBodyBeforeCreatingNote.findElements(By.tagName("th"));
		Assertions.assertEquals(0, rowsBeforeCreatingNote.size());
		this.clickButton("addNoteButton");
		this.setValueToInput("todo", "note-title");
		this.setValueToInput("test", "note-description");
		this.clickButton("saveNoteButton");
		goToPage("/home");
		WebElement tableBodyAfterCreatingNote = driver.findElement(By.id("noteTableBody"));
		List<WebElement> rowsAfterCreatingNote = tableBodyAfterCreatingNote.findElements(By.tagName("th"));
		Assertions.assertEquals(1, rowsAfterCreatingNote.size());
	}

	private void goToPage(String path) {
		driver.get("http://localhost:" + this.port + path);
	}

	private void signUp() {
		goToPage("/signup");
		this.setValueToInput("kota", "inputFirstName");
		this.setValueToInput("aoyama", "inputLastName");
		this.setValueToInput("kota811", "inputUsername");
		this.setValueToInput("password", "inputPassword");
		this.clickButton("signUpButton");
	}

	private void login() {
		goToPage("/login");
		this.setValueToInput("kota811", "inputUsername");
		this.setValueToInput("password", "inputPassword");
		this.clickButton("loginButton");
	}

	private void setValueToInput(String value, String inputId) {
		WebElement input = driver.findElement(By.id(inputId));
		wait.until(ExpectedConditions.elementToBeClickable(input)).click();
		input.sendKeys(value);
	}

	private void clickButton(String id) {
		WebElement element = driver.findElement(By.id(id));
		wait.until(ExpectedConditions.elementToBeClickable(element)).click();
	}
}
