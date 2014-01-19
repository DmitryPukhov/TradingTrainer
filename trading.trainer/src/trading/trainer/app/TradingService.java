package trading.trainer.app;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;

import trading.trainer.events.OrderClosedEvent;
import trading.trainer.events.OrderOpenedEvent;
import trading.trainer.model.Bar;
import trading.trainer.model.Order;
import trading.trainer.model.OrderType;
import trading.trainer.model.TradingContext;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Trading functionality service
 * 
 * @author dima
 * 
 */
@Named
public class TradingService {
	/**
	 * 0 - no pending orders; +1 - one buy order; +2 - two buy orders ...; -1
	 * -one sell order; ...
	 * 
	 */
	private double pendingOrderSignal = 0;

	/**
	 * Order opened or closed event bus
	 */
	private EventBus orderEventBus = new EventBus();

	/**
	 * @return the orderEventBus
	 */
	public EventBus getOrderEventBus() {
		return orderEventBus;
	}

	/**
	 * In this constructor attach to market data event from market emulator bean
	 */
	@Inject
	public TradingService(
			@Value("#{marketEmulator}") MarketEmulator marketEmulator) {
		this.marketEmulator = marketEmulator;
		// Attach to new bar event
		marketEmulator.getBarEventBus().register(this);
	}

	/**
	 * Trading context - last price, order
	 */
	@Inject
	private TradingContext tradingContext;

	/**
	 * Market data provider
	 */
	private MarketEmulator marketEmulator;

	/**
	 * Place buy order
	 */
	public void buy() {
		this.pendingOrderSignal++;
	}

	/**
	 * Place sell order
	 */
	public void sell() {
		this.pendingOrderSignal--;
	}

	/**
	 * Execute atomic buy or sell order. No reverse orders here.
	 * 
	 * @param order
	 */
	private synchronized void executeOrder(Bar bar, Order order) {
		// If opened orders are the opposite, close opened order
		if (tradingContext.getOpenedOrderType() != OrderType.NONE
				&& tradingContext.getOpenedOrderType() != order.getOrderType()) {
			closeOpenedOrder(bar);
			return;
		}

		// Open a new order
		tradingContext.getOpenedOrders().add(order);

		// Fire order opened event
		OrderOpenedEvent event = new OrderOpenedEvent(bar.getTime(), order);
		orderEventBus.post(event);

	}

	/**
	 * Close the oldest opened order
	 */
	private synchronized void closeOpenedOrder(Bar bar) {
		Order order = tradingContext.getOpenedOrders().remove(0);
		if (order == null) {
			return;
		}
		// Add profit to context
		double profit = getOrderProfit(bar, order);
		tradingContext.setAmount(tradingContext.getAmount() + profit);

		// Fire order closed event
		OrderClosedEvent event = new OrderClosedEvent(bar.getTime(), order,
				bar, profit);
		orderEventBus.post(event);
	}

	/**
	 * Calculate profit for one order at specific bar
	 * 
	 * @param order
	 */
	private double getOrderProfit(Bar bar, Order order) {
		// Pessimistic estimation - the worst close price in this bar
		double profit = 0;
		switch (order.getOrderType()) {
		case BUY:
			profit = bar.getLow() - order.getOpenPrice();
			break;
		case SELL:
			profit = order.getOpenPrice() - bar.getHigh();
			break;
		}
		return profit;
	}

	/**
	 * Get profit of currently active orders
	 * 
	 * @return
	 */
	public double getOpenedOrdersProfit() {
		double allProfit = 0;
		Bar bar = tradingContext.getLastBar();
		// Add profit from every order
		for (Order order : tradingContext.getOpenedOrders()) {
			double orderProfit = getOrderProfit(bar, order);
			allProfit += orderProfit;
		}
		return allProfit;
	}

	/**
	 * New bar received from market emulator
	 * 
	 * @param bar
	 */
	@Subscribe
	public synchronized void onNewBar(Bar bar) {
		// Process all buy signals if they exist
		while (pendingOrderSignal > 0) {
			Order order = new Order(OrderType.BUY, bar.getTime(), bar.getHigh());
			executeOrder(bar, order);
			pendingOrderSignal--;
		}
		// otherwise process all sell signals if they exist
		while (pendingOrderSignal < 0) {
			Order order = new Order(OrderType.SELL, bar.getTime(), bar.getLow());
			executeOrder(bar, order);
			pendingOrderSignal++;
		}
	}
}
