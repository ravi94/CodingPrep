package lld.splitwise.splitStartegy;

import lld.splitwise.models.Split;
import lld.splitwise.models.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SplitStrategy {
    // metadata is like exact amount or percentage share or splitshare , sometime it can be dummy
    List<Split> computeSplits(BigDecimal total , List<User> users, List<BigDecimal> metaData);
}
