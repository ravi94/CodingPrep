package lld.splitwise;

import lld.splitwise.models.Settlement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class SettlementSimplifier {
    public List<Settlement> minimise(Map<String, BigDecimal> netBalances){
        List<Settlement> allSettlement = new ArrayList<>();

        PriorityQueue<Map.Entry<String,BigDecimal>> creditors = new PriorityQueue<>((entry1,entry2 )->{
           return entry2.getValue().compareTo(entry1.getValue());
        });

        PriorityQueue<Map.Entry<String,BigDecimal>> debtors = new PriorityQueue<>((entry1,entry2)-> {
            return entry1.getValue().compareTo(entry2.getValue());
        });

        netBalances.forEach((user,balance)->{
            if(balance.compareTo(BigDecimal.ZERO) > 0)
                creditors.add(Map.entry(user,balance));
            else if(balance.compareTo(BigDecimal.ZERO) < 0){
                debtors.add(Map.entry(user,balance));
            }
        });

        while(!creditors.isEmpty() && !debtors.isEmpty()){
            var creditor = creditors.poll();
            var debtor = debtors.poll();

            BigDecimal amount = creditor.getValue().min(debtor.getValue().abs());

            allSettlement.add(new Settlement(debtor.getKey(),creditor.getKey(),amount));

            BigDecimal creditorAmountLeft = creditor.getValue().subtract(amount);
            BigDecimal debtorAmountLeft = debtor.getValue().add(amount);

            if(creditorAmountLeft.compareTo(BigDecimal.ZERO)>0)
                creditors.add(Map.entry(creditor.getKey(),creditorAmountLeft));
            else if(debtorAmountLeft.compareTo(BigDecimal.ZERO)>0)
                debtors.add(Map.entry(debtor.getKey(),debtorAmountLeft));
        }
        return allSettlement;
    }
}
