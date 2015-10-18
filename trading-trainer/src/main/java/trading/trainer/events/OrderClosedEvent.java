package trading.trainer.events;

import java.util.Date;

import trading.trainer.model.Bar;
import trading.trainer.model.Order;

/**
 * Previously opened order has been closed event
 * 
 * @author dima
 * 
 */
public class OrderClosedEvent {
	/**
	 * Order which has been closed
	 */
	private Order order;

	/**
	 * Bar on which the order was closed
	 */
	private Bar bar;

	/**
	 * Close time
	 */
	private Date time;

	/**
	 * Order profit calculated
	 */
	private double profit;

	/**
	 * Construct event for order
	 * 
	 * @param order
	 *            newly closed order
	 * @param time
	 *            order close time
	 */
	public OrderClosedEvent(Date time, Order order, Bar bar, double profit) {
		this.time = time;
		this.order = order;
		this.bar = bar;
		this.profit = profit;
	}

	/**
	 * @return the bar
	 */
	public Bar getBar() {
		return bar;
	}

	/**
	 * @return the order {@link OrderClosedEvent#order}
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @return the profit
	 */
	public double getProfit() {
		return profit;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

}
