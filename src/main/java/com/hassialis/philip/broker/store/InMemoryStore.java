package com.hassialis.philip.broker.store;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import com.hassialis.philip.broker.model.Quote;
import com.hassialis.philip.broker.model.Symbol;

import io.netty.util.internal.ThreadLocalRandom;

@Singleton
public class InMemoryStore {

  private final List<Symbol> symbols;
  private final ThreadLocalRandom current = ThreadLocalRandom.current();
  private final Map<String, Quote> cachedQuotes = new HashMap<>();

  public InMemoryStore() {
    symbols = Stream.of("AAPL", "AMZN", "META", "GOOG", "MSFT", "NFLX")
        .map(Symbol::new)
        .collect(Collectors.toList());
    symbols.forEach(symbol -> cachedQuotes.put(symbol.getValue(), randomQuote(symbol)));

  }

  private Quote randomQuote(Symbol symbol) {
    return Quote.builder()
        .symbol(symbol)
        .bid(randomValue())
        .ask(randomValue())
        .lastPrice(randomValue())
        .volume(randomValue()).build();
  }

  public List<Symbol> getAllSymbols() {
    return symbols;
  }

  public Optional<Quote> fetchQuote(final String symbol) {
    return Optional.ofNullable(cachedQuotes.get(symbol));
  }

  private BigDecimal randomValue() {

    return BigDecimal.valueOf(current.nextDouble(1, 100));
  }

  public void update(Quote quote) {
    cachedQuotes.put(quote.getSymbol().getValue(), quote);
  }

}