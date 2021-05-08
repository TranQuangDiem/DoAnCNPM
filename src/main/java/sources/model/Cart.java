package sources.model;

import sources.entity.Product;

import java.util.Optional;

public class Cart {
    Optional<Product> product;
    int soluong;

    public Cart() {
    }

    public Cart(Optional<Product> product, int soluong) {
        this.product = product;
        this.soluong = soluong;
    }

    public Optional<Product> getProduct() {
        return product;
    }

    public void setProduct(Optional<Product> product) {
        this.product = product;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }
}
