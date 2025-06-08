package com.task.wallet;
import com.task.wallet.dto.WalletDto;
import com.task.wallet.entities.Wallet;
import com.task.wallet.repositories.WalletRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {
	@LocalServerPort
	private Integer port;
	@Autowired
	private WalletRepository walletRepository;
	private TestRestTemplate restTemplate = new TestRestTemplate();
	private String expectedWalletId;
	public static PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>("postgres:14");

	@BeforeAll
	public static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	public static void afterAll() {
		postgres.stop();
	}

	@DynamicPropertySource
	public static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@BeforeEach
	public void fillingDataBase() {
		Wallet wallet = new Wallet();
		wallet.setBalance(BigDecimal.valueOf(10000.00));
		wallet.setOwnerFirstName("Test");
		wallet.setOwnerLastName("Testerov");
		walletRepository.save(wallet);
		expectedWalletId = String.valueOf(wallet.getId());
	}

	@AfterEach
	public void clearDataBase() {
		walletRepository.deleteAll();
	}

	@Test
	@DisplayName("Get new created wallet by ID")
	public void whenGetWalletById_thenReturnWalletDto() {
		ResponseEntity<WalletDto> walletDto = restTemplate.getRestTemplate()
				.getForEntity("http://localhost:" + port + "/api/v1/wallet/" + expectedWalletId, WalletDto.class);
		Assertions.assertTrue(walletDto.getStatusCode().is2xxSuccessful());
		Assertions.assertEquals(expectedWalletId, walletDto.getBody().getId());
		Assertions.assertEquals("Test", walletDto.getBody().getOwnerFirstName());
		Assertions.assertEquals("Testerov", walletDto.getBody().getOwnerLastName());
		Assertions.assertEquals(BigDecimal.valueOf(10000.00).setScale(2), walletDto.getBody().getBalance());
	}
}