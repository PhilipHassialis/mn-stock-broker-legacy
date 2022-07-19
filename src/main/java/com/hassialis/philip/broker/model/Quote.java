package com.hassialis.philip.broker.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quote {

  private Symbol symbol;
  private BigDecimal bid;
  private BigDecimal ask;
  private BigDecimal lastPrice;
  private BigDecimal volume;

}
