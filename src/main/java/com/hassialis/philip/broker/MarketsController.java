package com.hassialis.philip.broker;

import java.util.List;

import com.hassialis.philip.broker.model.Symbol;
import com.hassialis.philip.broker.store.InMemoryStore;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/markets")
public class MarketsController {

  private final InMemoryStore store;

  public MarketsController(final InMemoryStore store) {
    this.store = store;
  }

  @Get
  public List<Symbol> all() {
    return store.getAllSymbols();
  }

}