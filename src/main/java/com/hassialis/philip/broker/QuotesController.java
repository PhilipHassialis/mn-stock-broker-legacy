package com.hassialis.philip.broker;

import java.util.Optional;

import com.hassialis.philip.broker.error.CustomError;
import com.hassialis.philip.broker.model.Quote;
import com.hassialis.philip.broker.store.InMemoryStore;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/quotes")
public class QuotesController {

  private final InMemoryStore store;

  public QuotesController(InMemoryStore store) {
    this.store = store;
  }

  @Operation(summary = "Returns a quote for a given symbol")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
  @ApiResponse(responseCode = "400", description = "Invalid symbol specified")
  @Tag(name = "quotes")
  @Get("/{symbol}")
  public HttpResponse getQuote(@PathVariable String symbol) {
    final Optional<Quote> maybeQuote = store.fetchQuote(symbol);
    if (maybeQuote.isEmpty()) {
      final CustomError notFound = CustomError.builder()
          .status(HttpStatus.NOT_FOUND.getCode())
          .error(HttpStatus.NOT_FOUND.name())
          .message("Quote for symbol not available")
          .path("/quotes/" + symbol)
          .build();
      return HttpResponse.notFound(notFound);
    }
    return HttpResponse.ok(maybeQuote.get());

  }

}
