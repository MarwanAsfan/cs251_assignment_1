import java.util.*;


public class Main {
    public static void main(String[] args) {
        List<Product> products = List.of(new Product(
                        "Phone", 1500, List.of(new Attribute("Storage", Arrays.asList("256", "128", "64", "32")),
                        new Attribute("Ram", Arrays.asList("16", "12", "8", "4"))),
                        List.of(new Variation("Phone 1", Map.of("Storage", "32", "Ram", "4"), 1500),
                                new Variation("Phone 2", Map.of("Storage", "64", "Ram", "8"), 2000),
                                new Variation("Phone 3", Map.of("Storage", "128", "Ram", "16"), 2400),
                                new Variation("Phone 4", Map.of("Storage", "64", "Ram", "4"), 2400)
                        )
                ),
                new Product(
                        "Shirt", 1500, List.of(new Attribute("Color", Arrays.asList("Red", "Green", "Blue", "Yellow"))),
                        List.of(new Variation("Shirt 2", Map.of("Color", "Red"), 1500))
                ));
        Shop shop = new Shop(products);
        shop.runShop();
    }
}


class Attribute {
    String name;
    List<String> values;

    Attribute(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

}

class Variation {
    String name;
    int price;
    Map<String, String> attributeValues;

    Variation(String name, Map<String, String> attributeValues, int price) {
        this.name = name;
        this.price = price;

        this.attributeValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.attributeValues.putAll(attributeValues);
    }
}

class Product {
    String name;
    int price;
    List<Attribute> attributes;
    List<Variation> variations;

    Product(String name, int price, List<Attribute> attributes, List<Variation> variations) {
        this.name = name;
        this.price = price;
        this.attributes = attributes;
        this.variations = variations;
    }
}

class Cart {
    List<Variation> cart;

    Cart(List<Variation> products) {
        this.cart = products;
    }

    public void addProduct(Variation product) {
        cart.add(product);
    }
    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
        } else {
            System.out.println("\n--- Cart ---");
            int total = 0;
            for (Variation v : cart) {
                System.out.println("- " + v.name + " : " + v.price);
                total += v.price;
            }
            System.out.println("Total: " + total);
            System.out.println("------------\n");
        }
    }

    // [ADD THIS]
    public void clear() {
        cart.clear();
        System.out.println("Cart cleared.");
    }
}

class Shop {
    List<Product> products;
    int index;
    Map<String, String> selectedAttributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Cart cart = new Cart(new ArrayList<>());

    Shop(List<Product> products) {
        this.products = products;
    }

    void runShop() {
        if (products.isEmpty()) {
            System.out.print("The Shop is empty");
        } else {
            System.out.print("Products: ");
            System.out.print("   ");
            System.out.print("Price: ");
            System.out.print("   ");
            System.out.println("Index: ");
            for (int i = 0; i < products.size(); i++) {
                System.out.print(products.get(i).name);
                System.out.print("        ");
                System.out.print(products.get(i).price);
                System.out.print("        ");
                System.out.println(i);
            }
            System.out.print("\nPlease Select a Product (0 - ");
            System.out.print(products.size() - 1);
            System.out.println(")");
            System.out.println("Or access cart");
            selectProduct();
        }
    }

    private void selectProduct() {
        Scanner input = new Scanner(System.in);
        while (true) {
            if (input.hasNext("cart")) {
                input.next();
                cart.viewCart();

                System.out.println("Type 'clear' to empty, or 'back' to return.");
                String cmd = input.next();
                if (cmd.equalsIgnoreCase("clear")) cart.clear();

                System.out.println("Select a product index:");
                continue;
            }
            if (input.hasNextInt()) {
                index = input.nextInt();
                if (index >= 0 && index < products.size()) {
                    break;
                } else {
                    System.out.println("Invalid! Please select an existing product (0 - " + (products.size() - 1) + ")");
                }
            } else {
                System.out.println("Invalid! Please enter a number");
                input.next();
            }
        }
        System.out.println("Selected Product: " + products.get(index).name);
        showSelectedProduct();
    }

    private void showSelectedProduct() {
        Variation selectedProduct = (null);
        Scanner input = new Scanner(System.in);
        while (true) {
            if (selectedProduct != null && selectedAttributes.size() == products.get(this.index).attributes.size()) {
                System.out.println("Name: " + selectedProduct.name);
                System.out.println("Price: " + selectedProduct.price);
            } else {
                System.out.println("Name: " + "select attributes to proceed");
                System.out.println("Price: " + "select attributes to proceed");
            }
            printProduct();
            System.out.println("\nCommands:");
            System.out.println(" [Name] [Value] : Select attribute (e.g. 'Color Red')");
            System.out.println(" '++'         : Add current match to cart");
            System.out.println(" 'back'         : Go back");
            System.out.print("> ");
            if (input.hasNext()) {
                String action = input.next();
                if (action.equalsIgnoreCase("back")) {
                    System.out.println("Returning to main list...");
                    runShop();
                    return;
                }
                if (action.equals("++")) {
                    if (selectedProduct != null && selectedAttributes.size() == products.get(index).attributes.size()) {
                        cart.addProduct(selectedProduct);
                        System.out.println("Success: Added " + selectedProduct.name);
                    } else {
                        System.out.println("Error: Please select all attributes first.");
                    }
                    continue;
                }
                if (input.hasNext()) {
                    String attributeValue = input.next();
                    if (isAvailable(action, attributeValue)) {
                        if (selectedAttributes.containsKey(action)) {
                            if (selectedAttributes.get(action).equals(attributeValue)) {
                                selectedAttributes.remove(action);
                                System.out.println("Attribute " + action + " with value " + attributeValue + " is Removed");
                                continue;
                            }
                        }
                        selectedAttributes.put(action.toLowerCase(), attributeValue);
                        System.out.println("Attribute " + action + " with value " + attributeValue + " is selected");
                        selectedProduct = products.get(this.index).variations.stream().filter(variation -> {
                                    for (Map.Entry<String, String> entry : selectedAttributes.entrySet()) {
                                        if (!entry.getValue().equalsIgnoreCase(variation.attributeValues.get(entry.getKey()))) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                        ).findFirst().orElse(null);
                    } else {
                        System.out.println("Attribute " + action + " with value " + attributeValue + " is not valid");
                    }
                }
            }
        }
    }

    private void printProduct() {
        for (int i = 0; i < products.get(index).attributes.size(); i++) {
            System.out.print(products.get(index).attributes.get(i).name);
            for (int j = 0; j < products.get(index).attributes.get(i).values.size(); j++) {
                String attributeValue = products.get(index).attributes.get(i).values.get(j);
                String attributeName = products.get(index).attributes.get(i).name;
                boolean isSelected = false;
                if (isAvailable(attributeName, attributeValue)) {
                    for (String key : selectedAttributes.keySet()) {
                        if (selectedAttributes.get(key).equals(attributeValue)) {
                            System.out.print((j == 0 ? ": " : ", ") + "[*" + attributeValue + "*]");
                            isSelected = true;
                        }
                    }
                    if (isSelected) {
                        continue;
                    }
                    System.out.print((j == 0 ? ": " : ", ") + attributeValue);
                } else {
                    System.out.print((j == 0 ? ": " : ", ") + "----" + attributeValue + "----");
                }
            }
            System.out.print("\n");
        }
    }

    private boolean isAvailable(String attributeName, String attributeValue) {
        for (Variation variation : products.get(index).variations) {
            for (Map.Entry<String, String> availableAttributes : variation.attributeValues.entrySet()) {
                if (availableAttributes.getValue().equals(attributeValue)) {
                    boolean found = true;
                    for (Map.Entry<String, String> entry : selectedAttributes.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(attributeName)) {
                            continue;
                        }
                        String selectedKey = entry.getKey();
                        String selectedValue = entry.getValue();
                        if (!variation.attributeValues.get(selectedKey).equals(selectedValue)) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}