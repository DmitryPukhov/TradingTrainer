package trading.trainer.model;

import java.util.Date;

/**
 * Buy or sell order
 * 
 * @author dima
 * 
 */
public class Order {
	/**
	 * ` Buy or sell order type
	 */
	private OrderType orderType = OrderType.NONE;

	/**
	 * Order opened price
	 */
	private double openPrice;

	/**
	 * Order opened timie
	 */
	private Date openTime;

	/**
	 * Constructor for all params
	 * 
	 * @param orderType
	 *            buy or sell {@link OrderType}
	 * @param openTime
	 *            order open time
	 * @param openPrice
	 *            order open price
	 */
	public Order(OrderType orderType, Date openTime, double openPrice) {
		this.orderType = orderType;
		this.openTime = openTime;
		this.openPrice = openPrice;
	}

	/**
	 * @return the openPrice
	 */
	public double getOpenPrice() {
		return openPrice;
	}

	/**
	 * @return the openTime
	 */
	public Date getOpenTime() {
		return openTime;
	}

	/**
	 * @return the orderType
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * @param openPrice
	 *            the openPrice to set
	 */
	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	/**
	 * @param openTime
	 *            the openTime to set
	 */
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	/**
	 * @param orderType
	 *            the orderType to set
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

}
