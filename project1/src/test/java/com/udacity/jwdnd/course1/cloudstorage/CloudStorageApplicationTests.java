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

	@Test
	@Order(4)
	public void testEditNote() {
		signUp();
		login();
		goToPage("/home");
		this.clickButton("nav-notes-tab");
		this.clickButton("addNoteButton");
		this.setValueToInput("todo", "note-title");
		this.setValueToInput("test", "note-description");
		this.clickButton("saveNoteButton");
		goToPage("/home");
		this.clickButton("editNoteButton_1");
		this.setValueToInput("!", "note-title");
		this.clickButton("saveNoteButton");
		goToPage("/home");
		WebElement tableBodyAfterCreatingNote = driver.findElement(By.id("noteTableBody"));
		List<WebElement> rowsAfterCreatingNote = tableBodyAfterCreatingNote.findElements(By.tagName("th"));
		wait.until(ExpectedConditions.textToBePresentInElement(rowsAfterCreatingNote.get(0), "todo!"));
		Assertions.assertEquals("todo!", rowsAfterCreatingNote.get(0).getText());
	}

	@Test
	@Order(5)
	public void testDeleteNote() {
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
		this.clickButton("deleteNoteButton_1");
		goToPage("/home");
		WebElement tableBodyAfterDeletingNote = driver.findElement(By.id("noteTableBody"));
		List<WebElement> rowsAfterDeletingNote = tableBodyAfterDeletingNote.findElements(By.tagName("th"));
		Assertions.assertEquals(0, rowsAfterDeletingNote.size());
	}

	@Test
	@Order(6)
	public void testCreateCredential() {
		signUp();
		login();
		goToPage("/home");
		this.clickButton("nav-credentials-tab");
		List<WebElement> rowsBeforeCreatingCredential = driver.findElement(By.id("credentialTable")).findElements(By.className("url"));
		Assertions.assertEquals(0, rowsBeforeCreatingCredential.size());
		this.clickButton("addCredentialButton");
		this.setValueToInput("https://udacity.com", "credential-url");
		this.setValueToInput("kota", "credential-username");
		this.setValueToInput("password", "credential-password");
		this.clickButton("saveCredentialButton");
		goToPage("/home");
		WebElement tableBodyAfterCreatingCredential = driver.findElement(By.id("credentialTable"));
		List<WebElement> passwordsAfterCreatingCredential = tableBodyAfterCreatingCredential.findElements(By.className("password"));
		List<WebElement> urlsAfterCreatingCredential = tableBodyAfterCreatingCredential.findElements(By.className("url"));
		Assertions.assertEquals(1, urlsAfterCreatingCredential.size());
		wait.until(ExpectedConditions.textToBePresentInElement(urlsAfterCreatingCredential.get(0), "https://udacity.com"));
		Assertions.assertNotNull(passwordsAfterCreatingCredential.get(0).getText());
		Assertions.assertNotEquals("password", passwordsAfterCreatingCredential.get(0).getText());
	}

	@Test
	@Order(7)
	public void testUpdateCredential() {
		signUp();
		login();
		goToPage("/home");
		this.clickButton("nav-credentials-tab");
		this.clickButton("addCredentialButton");
		this.setValueToInput("https://udacity.com", "credential-url");
		this.setValueToInput("kota", "credential-username");
		this.setValueToInput("password", "credential-password");
		this.clickButton("saveCredentialButton");
		goToPage("/home");

		List<WebElement> urls = driver.findElement(By.id("credentialTable")).findElements(By.className("url"));
		List<WebElement> passwords = driver.findElement(By.id("credentialTable")).findElements(By.className("password"));

		wait.until(ExpectedConditions.textToBePresentInElement(urls.get(0), "https://udacity.com"));
		String encryptedPasswordVer1 = passwords.get(0).getText();
		this.clickButton("editCredentialButton_1");
		WebElement unencryptedPassword = driver.findElement(By.id("credential-password"));
		wait.until(ExpectedConditions.textToBePresentInElement(unencryptedPassword,"password"));
		Assertions.assertEquals("password", unencryptedPassword.getText());
		this.setValueToInput("!", "credential-password");
		this.clickButton("saveCredentialButton");
		goToPage("/home");
		wait.until(ExpectedConditions.textToBePresentInElement(urls.get(0), "https://udacity.com"));
		Assertions.assertNotEquals(encryptedPasswordVer1, passwords.get(0).getText());
	}

	@Test
	@Order(8)
	public void testDeleteCredential() {
		signUp();
		login();
		goToPage("/home");
		this.clickButton("nav-credentials-tab");
		this.clickButton("addCredentialButton");
		this.setValueToInput("https://udacity.com", "credential-url");
		this.setValueToInput("kota", "credential-username");
		this.setValueToInput("password", "credential-password");
		this.clickButton("saveCredentialButton");
		goToPage("/home");
		WebElement tableBodyBeforeDeletingCredential = driver.findElement(By.id("credentialTable"));
		List<WebElement> urlsBeforeDeletingCredential = tableBodyBeforeDeletingCredential.findElements(By.className("url"));
		Assertions.assertEquals(1, urlsBeforeDeletingCredential.size());
		this.clickButton("deleteCredentialButton_1");
		goToPage("/home");
		List<WebElement> urlsAfterDeletingCredential = driver.findElement(By.id("credentialTable")).findElements(By.className("url"));
		Assertions.assertEquals(0, urlsAfterDeletingCredential.size());
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
