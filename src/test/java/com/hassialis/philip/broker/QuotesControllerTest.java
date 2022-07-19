package com.hassialis.philip.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hassialis.philip.broker.model.Quote;
import com.hassialis.philip.broker.model.Symbol;
import com.hassialis.philip.broker.store.InMemoryStore;

import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import static org.assertj.core.api.Assertions.assertThat;

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