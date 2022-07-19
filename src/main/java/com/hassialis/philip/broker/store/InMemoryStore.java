package com.hassialis.philip.broker.store;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import com.hassialis.philip.broker.model.Symbol;

@Singleton
public class InMemoryStore {

  private final List<Symbol> symbols;

  public InMemoryStore() {
    symbols = Stream.of("AAPL", "AMZN", "META", "GOOG", "MSFT", "NFLX")
        .map(Symbol::new)
        .collect(Collectors.toList());
  }

  public List<Symbol> getAllSymbols() {
    return symbols;
  }

}