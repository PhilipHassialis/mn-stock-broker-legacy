package com.hassialis.philip.broker.account;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hassialis.philip.broker.model.WatchList;
import com.hassialis.philip.broker.store.InMemoryAccountStore;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

@Controller("/account/watchlist-reactive")
public class WatchListControllerReactive {

  private final InMemoryAccountStore store;
  private final Scheduler scheduler;
  public static final UUID ACCOUNT_ID = UUID.randomUUID();
  private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerReactive.class);

  public WatchListControllerReactive(
      @Named(TaskExecutors.IO) ExecutorService executorService,
      final InMemoryAccountStore store) {
    this.store = store;
    this.scheduler = Schedulers.from(executorService);
  }

  @Get(value = "/single", produces = MediaType.APPLICATION_JSON)
  public Flowable<WatchList> getAsSingle() {
    return Single.fromCallable(() -> {
      LOG.debug("getAsSingle thread - {}", Thread.currentThread().getName());
      return store.getWatchList(ACCOUNT_ID);
    }).toFlowable().subscribeOn(scheduler);
  }

  @Get(produces = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public WatchList get() {
    LOG.debug("getWatchList thread {}", Thread.currentThread().getName());
    return store.getWatchList(ACCOUNT_ID);
  }

  @Put(produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
  @ExecuteOn(TaskExecutors.IO)
  public WatchList update(@Body WatchList watchList) {
    return store.updateWatchList(ACCOUNT_ID, watchList);
  }

  @Delete(produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON, value = "/{accountId}")
  @ExecuteOn(TaskExecutors.IO)
  public void delete(@PathVariable UUID accountId) {
    store.deleteWatchList(accountId);
  }

}
