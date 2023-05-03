package com.devlach.cashcard;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@JsonTest
class CashCardJsonTest {


    private CashCard[] cashCards;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(62L, 123.45),
                new CashCard(63L, 1.00),
                new CashCard(64L, 150.00)
        );
    }

    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }


    @Test
    void cashCardListDeserializationTest() throws IOException {
        String expected="""
            [
              {"id": 62, "amount": 123.45 },
              {"id": 63, "amount": 1.00 },
              {"id": 64, "amount": 150.00 }
            ]
         """;
        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }
}
