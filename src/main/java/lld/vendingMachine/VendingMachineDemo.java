package lld.vendingMachine;

import java.util.UUID;

/**
 * Exercises the vending machine end to end: happy path, coin-by-coin payment,
 * state guard rails, cancellation, overpayment and running a product out of stock.
 *
 * Assumes a single user interacts with the machine at a time, so there is no
 * concurrency scenario here.
 *
 * Lives in the same package as {@link VendingMachine} because the constructor and
 * the four operations are package-private.
 */
public class VendingMachineDemo {

    public static void main(String[] args) throws Exception {
        happyPath();
        payCoinByCoin();
        stateGuardRails();
        cancelAndRefund();
        overpayment();
        runOutOfStock();
    }

    private static void happyPath() throws Exception {
        header("1. Buy a product with exact change");

        Inventory inventory = new Inventory();
        Product coke = new Product(UUID.randomUUID(), "Coke", 5);
        inventory.add(coke, 3);

        VendingMachine vm = new VendingMachine(inventory);
        printMachine(vm, "start");

        vm.addCoin(Coin.FIVE);
        printMachine(vm, "after inserting 5");

        vm.selectProduct(coke.getId());
        printMachine(vm, "after selecting Coke");

        vm.dispenseProduct();
        printMachine(vm, "after dispensing");
        System.out.println("Coke stock left: " + stockOf(inventory, coke.getId()));
    }

    private static void payCoinByCoin() throws Exception {
        header("2. Insufficient funds, then top up");

        Inventory inventory = new Inventory();
        Product chips = new Product(UUID.randomUUID(), "Chips", 12);
        inventory.add(chips, 2);

        VendingMachine vm = new VendingMachine(inventory);

        vm.addCoin(Coin.FIVE);
        expectFailure("Select Chips (12) holding only 5", () -> vm.selectProduct(chips.getId()));

        vm.addCoin(Coin.FIVE);
        vm.addCoin(Coin.TWO);
        printMachine(vm, "after topping up to 12");

        vm.selectProduct(chips.getId());
        vm.dispenseProduct();
        printMachine(vm, "after dispensing");
    }

    private static void stateGuardRails() throws Exception {
        header("3. Operations rejected by the current state");

        Inventory inventory = new Inventory();
        Product gum = new Product(UUID.randomUUID(), "Gum", 2);
        inventory.add(gum, 5);

        VendingMachine vm = new VendingMachine(inventory);

        // Idle: nothing but adding a coin is legal.
        expectFailure("Select before paying      [Idle]", () -> vm.selectProduct(gum.getId()));
        expectFailure("Dispense before paying    [Idle]", vm::dispenseProduct);

        // HasMoney: must select before dispensing.
        vm.addCoin(Coin.TWO);
        expectFailure("Dispense before selecting [HasMoney]", vm::dispenseProduct);

        // Dispensing: the machine is busy, everything else is rejected.
        vm.selectProduct(gum.getId());
        expectFailure("Add a coin while vending  [Dispensing]", () -> vm.addCoin(Coin.ONE));
        expectFailure("Select while vending      [Dispensing]", () -> vm.selectProduct(gum.getId()));
        expectFailure("Cancel while vending      [Dispensing]", vm::cancel);

        vm.dispenseProduct();
        printMachine(vm, "after the vend completes");
    }

    private static void cancelAndRefund() throws Exception {
        header("4. Cancel an in-flight transaction");

        Inventory inventory = new Inventory();
        Product water = new Product(UUID.randomUUID(), "Water", 10);
        inventory.add(water, 1);

        VendingMachine vm = new VendingMachine(inventory);
        vm.addCoin(Coin.FIVE);
        vm.addCoin(Coin.TWO);
        printMachine(vm, "before cancelling");

        vm.cancel();
        printMachine(vm, "after cancelling");

        // Cancelling in Idle is a no-op rather than an error.
        vm.cancel();
        printMachine(vm, "after cancelling again from Idle");
    }

    private static void overpayment() throws Exception {
        header("5. Overpay and see where the change goes");

        Inventory inventory = new Inventory();
        Product bar = new Product(UUID.randomUUID(), "Bar", 5);
        inventory.add(bar, 2);

        VendingMachine vm = new VendingMachine(inventory);
        vm.addCoin(Coin.TEN);
        vm.selectProduct(bar.getId());
        vm.dispenseProduct();

        printMachine(vm, "after paying 10 for a 5 item");
        System.out.println("NOTE: the 5 change is still held by the machine, in HasMoney.");
        System.out.println("      There is no change-return on dispense - the customer");
        System.out.println("      must either buy again or cancel to get it back.");

        vm.cancel();
        printMachine(vm, "after cancelling to reclaim the change");
    }

    private static void runOutOfStock() throws Exception {
        header("6. Sell a product out");

        Inventory inventory = new Inventory();
        Product mints = new Product(UUID.randomUUID(), "Mints", 5);
        inventory.add(mints, 2);

        VendingMachine vm = new VendingMachine(inventory);

        buyOne(vm, mints);
        System.out.println("Mints stock: " + stockOf(inventory, mints.getId()));

        buyOne(vm, mints);
        System.out.println("Mints stock: " + stockOf(inventory, mints.getId()));

        // The last unit leaving also drops Mints from the catalogue, so the machine
        // reports it as unknown rather than sold out.
        vm.addCoin(Coin.FIVE);
        expectFailure("Select Mints once sold out", () -> vm.selectProduct(mints.getId()));
        System.out.println("NOTE: reported as unlisted, not sold out - selling the last");
        System.out.println("      unit removes the product from the catalogue entirely.");

        // Restocking puts it back, but only because the caller still holds the Product.
        inventory.add(mints, 4);
        System.out.println("Restocked. Mints stock: " + stockOf(inventory, mints.getId()));
        vm.selectProduct(mints.getId());
        vm.dispenseProduct();
        printMachine(vm, "after buying from the restocked slot");
    }

    private static void buyOne(VendingMachine vm, Product product) throws Exception {
        vm.addCoin(Coin.FIVE);
        vm.selectProduct(product.getId());
        vm.dispenseProduct();
    }

    /** Reads the raw stock map; a delisted product has no entry at all. */
    private static String stockOf(Inventory inventory, UUID productId) {
        Integer qty = inventory.productStock.get(productId);
        return qty == null ? "0 (delisted)" : qty.toString();
    }

    private static void expectFailure(String label, ThrowingAction action) {
        try {
            action.run();
            System.out.printf("  %-38s BUG: was allowed%n", label);
        } catch (Exception e) {
            System.out.printf("  %-38s rejected: %s%n", label, e.getMessage());
        }
    }

    private static void printMachine(VendingMachine vm, String label) {
        System.out.printf("%-38s state=%-16s credit=%-3d collected=%d%n",
                label,
                vm.getCurrentState().getClass().getSimpleName(),
                vm.getCurrentTransactionBalance(),
                vm.getVendingMachineBalance());
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }
}
