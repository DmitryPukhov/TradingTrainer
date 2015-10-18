package trading.trainer.model;

import java.util.Date;


/**
 * Bar with OHLC data
 * @author dima
 *
 */
public class Bar {
	private Date time;
	private double open;
	private double high;
	private double low;
	private double close;
	private long volume;
	
	/**
	 * Construct and fill all values
	 * @param time bar time
	 * @param open bar open price
	 * @param high bar high price
	 * @param low bar low price
	 * @param close bar close price
	 * @param volume bar volume
	 */
	public Bar(Date time, double open, double high, double low, double close, long volume){
		this.time = time;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}
	
	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	/**
	 * @return the open price
	 */
	public double getOpen() {
		return open;
	}
	/**
	 * @param open the open price to set
	 */
	public void setOpen(double open) {
		this.open = open;
	}
	/**
	 * @return the high price
	 */
	public double getHigh() {
		return high;
	}
	/**
	 * @param high the high price to set
	 */
	public void setHigh(double high) {
		this.high = high;
	}
	/**
	 * @return the low price
	 */
	public double getLow() {
		return low;
	}
	/**
	 * @param low the low price to set
	 */
	public void setLow(double low) {
		this.low = low;
	}
	/**
	 * @return the close price
	 */
	public double getClose() {
		return close;
	}
	/**
	 * @param close the close price to set
	 */
	public void setClose(double close) {
		this.close = close;
	}
	/**
	 * @return the volume
	 */
	public long getVolume() {
		return volume;
	}
	/**
	 * @param volume the volume to set
	 */
	public void setVolume(long volume) {
		this.volume = volume;
	}

}
