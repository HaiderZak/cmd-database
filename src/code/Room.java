package code;

public class Room {
	private Integer room_num;
	private String room_type;
	private Double room_price;
	private String room_view;
	private String hotel_address;
	private Boolean can_be_extended;
	
	public Room() {
		
	}
	public Room(Integer room_num, String room_type, Double room_price, String room_view, String hotel_address, Boolean can_be_extended) {
		this.room_num = room_num;
		this.room_type = room_type;
		this.room_price = room_price;
		this.room_view = room_view;
		this.hotel_address = hotel_address;
		this.can_be_extended = can_be_extended;
	}
	
	public Integer getRoomNum() {
		return room_num;
	}
	public String getRoomType() {
		return room_type;
	}
	public Double getRoomPrice() {
		return room_price;
	}
	public String getRoomView() {
		return room_view;
	}
	public String getHotelAddress() {
		return hotel_address;
	}
	public Boolean can_be_extended() {
		return can_be_extended;
	}
	public String toString() {
		return "Addr: " + getHotelAddress() + "--Type: " + getRoomType() + "--Num: " + getRoomNum() + "--View: " + getRoomView() + "--Extendable: " + can_be_extended() 
		+ "--Price: $" + getRoomPrice();
	}
}
