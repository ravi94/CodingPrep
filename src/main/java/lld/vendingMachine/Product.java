package lld.vendingMachine;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Product {
    UUID id;
    String name;
    Integer price;
}
