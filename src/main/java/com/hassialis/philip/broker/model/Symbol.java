package com.hassialis.philip.broker.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Symbol", description = "Abrreviation of uniquely identifying a stock market symbol")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol {
  @Schema(description = "Symbol value", minLength = 1, maxLength = 5)
  private String value;
}