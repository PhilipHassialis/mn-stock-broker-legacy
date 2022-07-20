package com.hassialis.philip.broker.account;

import java.util.UUID;

import com.hassialis.philip.broker.model.WatchList;
import com.hassialis.philip.broker.store.InMemoryAccountStore;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;

@Controller("/account/watchlist")
public class WatchListController {

  private final InMemoryAccountStore store;
  public static final UUID ACCOUNT_ID = UUID.randomUUID();

  public WatchListController(final InMemoryAccountStore store) {
    this.store = store;
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  public WatchList get() {
    final UUID accountId = UUID.randomUUID();
    return store.getWatchList(ACCOUNT_ID);
  }

  @Put(produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  public WatchList update(@Body WatchList watchList) {
    return store.updateWatchList(ACCOUNT_ID, watchList);
  }

  @Delete(produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON, value = "/{accountId}")
  public void delete(@PathVariable UUID accountId) {
    store.deleteWatchList(accountId);
  }

}
