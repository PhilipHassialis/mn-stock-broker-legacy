package com.hassialis.philip.broker;

import java.util.Optional;

import com.hassialis.philip.broker.model.Quote;
import com.hassialis.philip.broker.store.InMemoryStore;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

@Controller("/quotes")
public class QuotesController {

  private final InMemoryStore store;

  public QuotesController(InMemoryStore store) {
    this.store = store;
  }

  @Get("/{symbol}")
  public HttpResponse getQuote(@PathVariable String symbol) {
    final Optional<Quote> maybeQuote = store.fetchQuote(symbol);
    return HttpResponse.ok(maybeQuote.get());

  }

}
