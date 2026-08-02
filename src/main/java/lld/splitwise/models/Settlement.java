package lld.splitwise.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Settlement {
    String from;
    String to;
    BigDecimal amount;
}
