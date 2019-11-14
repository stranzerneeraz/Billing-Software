package sample;

public class ProductModel {
    private int productID;
    private String particular;
    private String unitPrice;
    private String  quantity;
    private String total;

    ProductModel(Integer productID, String particular, String unitPrice, String quantity, String total) {
        this.productID = productID;
        this.particular = particular;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.total = total;
    }
    ProductModel() {}

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "productID=" + productID +
                ", particular='" + particular + '\'' +
                ", unitPrice='" + unitPrice + '\'' +
                ", quantity='" + quantity + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
}
