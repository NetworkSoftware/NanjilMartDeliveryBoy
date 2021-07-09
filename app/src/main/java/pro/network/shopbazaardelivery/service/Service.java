package pro.network.shopbazaardelivery.service;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Service implements Serializable {
    String id;
    String customerName;
    String customerPhone;
    String problem;
    String shopname;
    String model;
    String image;
    String status;
String amount;
    public Service() {

    }

    public Service(String customerName, String customerPhone, String problem, String shopname) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.problem = problem;
        this.shopname = shopname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}