package pro.network.nanjilmartdelivery.product;

public interface StatusListener {

    void onDeliveredClick(String id);
    void onWhatsAppClick(String phone);
    void onCallClick(String phone);
    void onCancelClick(String id);
    void onTrackOrder(String id);
    void onCourier(String id);
    void InProgress(Order order );
    void InPicked(Order order);


}
