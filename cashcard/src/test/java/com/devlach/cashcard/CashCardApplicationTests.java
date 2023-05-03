package com.devlach.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;




    @Test
    void shouldReturnsACashCardWhenDataIsFound() {

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/cashcards/62", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Long id = documentContext.read("$.id", Long.class);
        assertThat(id).isNotNull();
    }

    @Test
    void shouldNotReturnsACashCardWithAnUnknownId() {
        ResponseEntity<String> reponse = restTemplate.getForEntity(
                "/cashcards/56", String.class
        );

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);
        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(62, 63, 64);
        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.0);
    }

    @Test
    @DirtiesContext
    void shouldCreateANewCashCard() {
        Double amountSpec = 123.45;
        CashCard cashCard = new CashCard(null, amountSpec);
        ResponseEntity<CashCard> response = restTemplate.postForEntity(
                "/cashcards", cashCard, CashCard.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = response.getHeaders().getLocation();
        ResponseEntity<CashCard> getResponse = restTemplate.getForEntity(location, CashCard.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CashCard cashCardResponse = getResponse.getBody();
        Long id = cashCardResponse.id();
        Double amount = cashCardResponse.amount();
        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(amountSpec);

    }
}
