package pro.network.nanjilmartdelivery.product;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by ravi on 16/11/17.
 */

public class Order implements Serializable {
    String id;
    String items;
    String quantity;
    String price;
    String status;
    String name;
    String phone;
    String address;
    String reson;
    ArrayList<Product> productBeans;
    String createdOn;
    String description;
    String dcharge;
    String pincode;
    String total;

    public Order() {
    }

    public Order(String items, String quantity, String price, String status, String name, String phone, String address, String reson, ArrayList<Product> productBeans, String createdOn, String description) {
        this.items = items;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.reson = reson;
        this.productBeans = productBeans;
        this.createdOn = createdOn;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReson() {
        return reson;
    }

    public void setReson(String reson) {
        this.reson = reson;
    }

    public ArrayList<Product> getProductBeans() {
        return productBeans;
    }

    public void setProductBeans(ArrayList<Product> productBeans) {
        this.productBeans = productBeans;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDcharge() {
        return dcharge;
    }

    public void setDcharge(String dcharge) {
        this.dcharge = dcharge;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}