package lld.vendingMachine;

import lld.vendingMachine.states.DispensingState;
import lld.vendingMachine.states.HasMoneyState;
import lld.vendingMachine.states.IdleState;
import lld.vendingMachine.states.State;
import lombok.Data;

import java.util.UUID;

@Data
public class VendingMachine {
    Inventory inventory;
    State currentState; // main state for usage
    State idleState, hasMoneyState, dispensingState; // cached state for usage
    Long vendingMachineBalance;
    Long currentTransactionBalance;
    UUID selectedProduct;

    VendingMachine(Inventory inventory) {
        this.inventory = inventory;
        this.vendingMachineBalance = Long.valueOf(0);
        this.currentTransactionBalance = Long.valueOf(0);
        currentState = new IdleState();
        idleState = new IdleState();
        hasMoneyState = new HasMoneyState();
        dispensingState = new DispensingState();
    }


    void addCoin(Coin coin) throws Exception {
        this.currentState.addCoin(this,coin);
    }

    void selectProduct(UUID product) throws Exception {
        this.currentState.selectProduct(this,product);
    };

    void dispenseProduct() throws Exception {
        this.currentState.dispenseProduct(this);
    };

    void cancel() throws Exception{
        this.currentState.cancel(this);
    };
}
