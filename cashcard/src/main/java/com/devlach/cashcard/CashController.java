package com.devlach.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cashcards")
public class CashController {

    private final CashCardRepository cashCardRepository;

    public CashController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        return  cashCardRepository.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping()
    public ResponseEntity<Iterable<CashCard>> findAll() {
        return ResponseEntity.ok(cashCardRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<CashCard> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ubc) {
        CashCard newCashCard = cashCardRepository.save(newCashCardRequest);
        URI location = ubc.path("/cashcards/{id}").buildAndExpand(newCashCard.id()).toUri();
     return ResponseEntity.created(location).body(newCashCard);
    }
}
