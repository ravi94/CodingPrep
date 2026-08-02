package lld.splitwise.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class Expense {
    String description;
    String id;
    BigDecimal amount;
    User paidByUser;
    List<Split> splits;
    LocalDateTime createdAt;
}
