package pro.network.shopbazaardelivery.service;

public interface StatusListener {

    void onDeliveredClick(String id);
    void onCallClick(String phone);
    void onTrackOrder(String id);
    void onItemClick(Service service);

}
