package lld.vendingMachine.states;

import lld.vendingMachine.Coin;
import lld.vendingMachine.Product;
import lld.vendingMachine.VendingMachine;

import java.util.UUID;

public interface State {
    void  addCoin(VendingMachine vm, Coin coin) throws Exception;
    void  selectProduct(VendingMachine vm, UUID productCode) throws Exception;
    void  dispenseProduct(VendingMachine vm) throws Exception;
    void  cancel(VendingMachine vm) throws Exception;


}
