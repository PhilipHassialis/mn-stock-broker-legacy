package com.hassialis.philip.broker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hassialis.philip.broker.account.WatchListController;
import com.hassialis.philip.broker.error.CustomError;
import com.hassialis.philip.broker.model.Quote;
import com.hassialis.philip.broker.model.Symbol;
import com.hassialis.philip.broker.model.WatchList;
import com.hassialis.philip.broker.store.InMemoryAccountStore;
import com.hassialis.philip.broker.store.InMemoryStore;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
public class WatchListControllerTest {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);
  private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

  @Inject
  EmbeddedApplication<?> application;

  @Inject
  @Client("/account/watchlist")
  RxHttpClient client;

  @Inject
  InMemoryAccountStore store;

  @Test
  void returnsEmptyWatchListForAccount() {
    final WatchList result = client.toBlocking().retrieve("/", WatchList.class);
    assertTrue(result.getSymbols().isEmpty());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  @Test
  void returnsWatchListForAccount() {
    WatchList watchList = generateWatchList();
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);

    final WatchList result = client.toBlocking().retrieve("/", WatchList.class);
    assertEquals(3, result.getSymbols().size());
    assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());

  }

  @Test
  void canUpdateWatchListForAccount() {
    WatchList watchList = generateWatchList();
    final HttpResponse<Object> added = client.toBlocking().exchange(HttpRequest.PUT("/", watchList));
    assertEquals(HttpStatus.OK, added.getStatus());
    assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));

  }

  @Test
  void canDeleteWatchListForAccount() {
    WatchList watchList = generateWatchList();
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);
    assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    final HttpResponse<Object> deleted = client.toBlocking().exchange(HttpRequest.DELETE("/" + TEST_ACCOUNT_ID));
    assertEquals(HttpStatus.OK, deleted.getStatus());
    assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
  }

  WatchList generateWatchList() {
    List<Symbol> symbols = Stream.of("AAPL", "MSFT", "AMZN")
        .map(Symbol::new)
        .collect(Collectors.toList());
    return new WatchList(symbols);
  }

}