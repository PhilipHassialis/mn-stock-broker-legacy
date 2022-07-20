package com.hassialis.philip.broker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hassialis.philip.broker.error.CustomError;
import com.hassialis.philip.broker.model.Quote;
import com.hassialis.philip.broker.model.Symbol;
import com.hassialis.philip.broker.store.InMemoryStore;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
public class QuotesControllerTest {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);

  @Inject
  EmbeddedApplication<?> application;

  @Inject
  @Client("/")
  RxHttpClient client;

  @Inject
  InMemoryStore store;

  @Test
  void returnsQuotesPerSymbol() {

    final Quote appleQuote = initRandomQuote("AAPL");
    store.update(appleQuote);

    final Quote amznQuote = initRandomQuote("AMZN");
    store.update(amznQuote);

    final Quote aaplResult = client.toBlocking().retrieve("/quotes/AAPL", Quote.class);
    LOG.debug("Result {}", aaplResult);
    assertThat(appleQuote).usingRecursiveComparison().isEqualTo(aaplResult);

    final Quote amznResult = client.toBlocking().retrieve("/quotes/AMZN", Quote.class);
    LOG.debug("Result {}", amznResult);
    assertThat(amznQuote).usingRecursiveComparison().isEqualTo(amznResult);
  }

  @Test
  void returnsNotFoundOnUnsupportedSymbol() {
    try {
      client.toBlocking().retrieve("/quotes/UNSUPPORTED", Quote.class, CustomError.class);
    } catch (HttpClientResponseException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
      LOG.debug("Body: {}", e.getResponse().getBody());
      final Optional<CustomError> customError = e.getResponse().getBody(CustomError.class);
      assertTrue(customError.isPresent());
      assertEquals(404, customError.get().getStatus());
      assertEquals("NOT_FOUND", customError.get().getError());
      assertEquals("Quote for symbol not available", customError.get().getMessage());
      assertEquals("/quotes/UNSUPPORTED", customError.get().getPath());
    }
  }

  private Quote initRandomQuote(String symbolName) {
    return Quote.builder()
        .symbol(new Symbol(symbolName))
        .bid(randomValue())
        .ask(randomValue())
        .lastPrice(randomValue())
        .volume(randomValue()).build();
  }

  private BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }

}