package trading.trainer.events;

import java.util.Date;

import trading.trainer.model.Order;

/**
 * Opened new order event
 * 
 * @author dima
 * 
 */
public class OrderOpenedEvent {
	/**
	 * Order which has been opened
	 */
	private Order order;

	/**
	 * Order open time
	 */
	private Date time;

	/**
	 * Construct event for order
	 * 
	 * @param order
	 */
	public OrderOpenedEvent(Date time, Order order) {
		this.order = order;
		this.time = time;
	}

	/**
	 * @return the order {@link OrderOpenedEvent#order}
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @return open time {@link OrderOpenedEvent#time}
	 */
	public Date getTime() {
		return time;
	}

}
