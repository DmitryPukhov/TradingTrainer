package trading.trainer.model;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

/**
 * Trader's account amount and current order data
 * 
 * @author dima
 * 
 */
@Named
public class TradingContext {
	/**
	 * TradingContext money amount
	 */
	private double amount;

	/**
	 * Last order result
	 */
	private double lastOrderProfit;

	/**
	 * Active orders
	 */
	private List<Order> openedOrders = new ArrayList<Order>();

	/**
	 * Last received bar
	 */
	private Bar lastBar;

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @return active orders
	 */
	public List<Order> getOpenedOrders() {
		return openedOrders;
	}

	/**
	 * Buy or sell orders in queue
	 * 
	 * @return
	 */
	public OrderType getOpenedOrderType() {
		// Order type NONE if empty
		if (openedOrders.isEmpty()) {
			return OrderType.NONE;
		}
		// All orders in queue are the same type, buy or sell
		// Get order type from head order
		Order order = openedOrders.get(0);
		return order.getOrderType();
	}

	/**
	 * @return the lastBar
	 */
	public Bar getLastBar() {
		return lastBar;
	}

	/**
	 * @return the lastOrderProfit
	 */
	public double getLastOrderProfit() {
		return lastOrderProfit;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @param lastBar
	 *            the lastBar to set
	 */
	public void setLastBar(Bar lastBar) {
		this.lastBar = lastBar;
	}

	/**
	 * @param lastOrderProfit
	 *            the lastOrderProfit to set
	 */
	public void setLastOrderProfit(double lastOrderProfit) {
		this.lastOrderProfit = lastOrderProfit;
	}

}
