package def;

import java.util.Objects;

public class Fruit {
    private final String name;
    private final String countryOfOrigin;
    private final String imagePath;
    private final double price;
    private final int stock;
    private final boolean isCitrus;
    private final boolean aanbieding;
    private final String categorie;
 
    public Fruit(String name, String countryOfOrigin, String imagePath,
                 double price, int stock, boolean isCitrus, boolean aanbieding, String categorie) {
        this.name = name;
        this.countryOfOrigin = countryOfOrigin;
        this.imagePath = imagePath;
        this.price = price;
        this.stock = stock;
        this.isCitrus = isCitrus;
        this.aanbieding = aanbieding;
        this.categorie = categorie;
    }
 
    public String getName() {
        return name;
    }
 
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
 
    public String getImagePath() {
        return imagePath;
    }
 
    public double getPrice() {
        return price;
    }
 
    public int getStock() {
        return stock;
    }
 
    public boolean isCitrus() {
        return isCitrus;
    }
 
    public boolean isAanbieding() {
        return aanbieding;
    }
 
    public String getCategorie() {
        return categorie;
    }
 
    @Override
    public String toString() {
        return name;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fruit fruit)) return false;
        return name.equals(fruit.name);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
