package com.hassialis.philip.broker.store;

import java.util.HashMap;
import java.util.UUID;

import javax.inject.Singleton;

import com.hassialis.philip.broker.model.WatchList;

@Singleton
public class InMemoryAccountStore {

  private final HashMap<UUID, WatchList> watchListsPerAccountId = new HashMap<>();

  public WatchList getWatchList(UUID accountId) {
    return watchListsPerAccountId.getOrDefault(accountId, new WatchList());
  }

  public WatchList updateWatchList(final UUID accountId, WatchList watchList) {
    watchListsPerAccountId.put(accountId, watchList);
    return getWatchList(accountId);
  }

  public void deleteWatchList(UUID accountId) {
    watchListsPerAccountId.remove(accountId);
  }

}
