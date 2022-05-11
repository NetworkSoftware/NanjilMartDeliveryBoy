package pro.network.nanjilmartdelivery.product;

public interface StatusListener {

    void onDeliveredClick(String id);
    void onWhatsAppClick(String phone);
    void onCallClick(String phone);
    void onTrackOrder(String id);
    void InPicked(Order order);
    void Bill(Order position);
    void onLocation(Order position);

}
