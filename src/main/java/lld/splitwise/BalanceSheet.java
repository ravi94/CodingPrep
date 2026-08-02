package lld.splitwise;

import lld.splitwise.models.Expense;
import lld.splitwise.models.Split;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BalanceSheet {
    private Map<String, Map<String, BigDecimal>> owes ;

    BalanceSheet(){
        this.owes= new HashMap<>();
    }

    public void apply(Expense expense){
        String creditor = expense.getPaidByUser().getId();
        for(Split split: expense.getSplits()){
            String debtor = split.getUser().getId();
            if(creditor.equals(debtor)) continue; // no need to adjust for yourself
            adjust(debtor,creditor,split.getAmount());
        }

    }

    private void adjust(String debtor, String creditor , BigDecimal amount){
        BigDecimal reverseOwe = owes.getOrDefault(creditor,Map.of()).getOrDefault(debtor,BigDecimal.ZERO);
        if(reverseOwe.compareTo(BigDecimal.ZERO) >0){
            BigDecimal offset = amount.min(reverseOwe);
            owes.get(creditor).merge(debtor,offset.negate(),BigDecimal::add);
            amount = amount.subtract(offset);
        }
        if(amount.compareTo(BigDecimal.ZERO) > 0){
            owes.computeIfAbsent(debtor,k ->new HashMap<>()).merge(creditor,amount,BigDecimal::add);
        }
    }

    public BigDecimal amountOwed(String debtor, String creditor){
        return owes.getOrDefault(debtor,Map.of()).getOrDefault(creditor,BigDecimal.ZERO);
    }

    // net balance for each user
    public Map<String,BigDecimal> netBalances(){
        Map<String ,BigDecimal> netBalances = new HashMap<>();
        owes.forEach((debtor,creditors)->{
            creditors.forEach((creditor, amountOwed)->{
                netBalances.merge(debtor,amountOwed.negate(),BigDecimal::add);  // note merge take care of non existent keys
                netBalances.merge(creditor,amountOwed,BigDecimal::add);
            });
        });
        return netBalances;
    }
}
