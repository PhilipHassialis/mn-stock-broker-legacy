package com.hassialis.philip.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hassialis.philip.broker.account.WatchListControllerReactive;
import com.hassialis.philip.broker.model.Symbol;
import com.hassialis.philip.broker.model.WatchList;
import com.hassialis.philip.broker.store.InMemoryAccountStore;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;

@MicronautTest
public class WatchListControllerTestReactive {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerReactive.class);
  private static final UUID TEST_ACCOUNT_ID = WatchListControllerReactive.ACCOUNT_ID;

  @Inject
  EmbeddedApplication<?> application;

  @Inject
  @Client("/account/watchlist-reactive")
  RxHttpClient client;

  @Inject
  InMemoryAccountStore store;

  @Test
  void returnsEmptyWatchListForAccount() {
    final Single<WatchList> result = client.retrieve(HttpRequest.GET("/"), WatchList.class).singleOrError();
    LOG.debug("Result {}", result);
    assertTrue(result.blockingGet().getSymbols().isEmpty());
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
  void returnsWatchListForAccountAsSingle() {
    WatchList watchList = generateWatchList();
    store.updateWatchList(TEST_ACCOUNT_ID, watchList);

    final WatchList result = client.toBlocking().retrieve("/single", WatchList.class);
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