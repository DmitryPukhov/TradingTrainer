package trading.trainer.model;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.BeanUtils;

/**
 * Application settings class with load/save functinality.
 * 
 * @author dima
 * 
 */
@XmlRootElement
@Named
public class Settings implements Serializable {
	private static final long serialVersionUID = 1L;
	private String dataFilePath;
	private int dataWindowSize = 50;
	private boolean randomStart;
	private int tickIntervalSeconds = 1;

	/**
	 * Settings xml file name
	 */
	private final static String SETTINGS_FILE = "trading.trainer.settings.xml";

	/**
	 * @return the dataFilePath
	 */
	public String getDataFilePath() {
		return dataFilePath;
	}

	/**
	 * @return the dataWindowSize - how many bars to display in window
	 */
	public int getDataWindowSize() {
		return dataWindowSize;
	}

	/**
	 * @return randomStart choose random position to start market emulation or
	 *         start from the first bar
	 */
	public boolean getRandomStart() {
		return randomStart;
	}

	/**
	 * Load stored settings from file
	 */
	@PostConstruct
	public void load() {
		try {
			// Check file existence
			File file = new File(SETTINGS_FILE);
			if (!file.exists()) {
				return;
			}
			// Load settings from file
			JAXBContext context;

			context = JAXBContext.newInstance(Settings.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();
			Settings loadedSettings = (Settings) unmarshaller.unmarshal(file);
			// Because unmarshal returns a new object, we need to copy it to
			// settings bean
			BeanUtils.copyProperties(loadedSettings, this);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Persist settings to file
	 */
	public void save() {
		try {
			// Update settings from view

			// Serialize to file using JAXB
			JAXBContext context = JAXBContext.newInstance(Settings.class);
			Marshaller marshaller = context.createMarshaller();
			File file = new File(SETTINGS_FILE);
			FileWriter writer = new FileWriter(file, false);
			marshaller.marshal(this, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dataFilePath
	 *            the dataFilePath to set
	 */
	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	/**
	 * @param dataWindowSize
	 *            the dataWindowSize to set - how many bars to display in window
	 */
	public void setDataWindowSize(int dataWindowSize) {
		this.dataWindowSize = dataWindowSize;
	}

	/**
	 * @param randomStart
	 *            set randomStart - choose random position to start market
	 *            emulation or start from the first bar
	 */
	public void setRandomStart(boolean randomStart) {
		this.randomStart = randomStart;
	}

	/**
	 * @return the tickIntervalSeconds
	 */
	public int getTickIntervalSeconds() {
		return tickIntervalSeconds;
	}

	/**
	 * @param tickIntervalSeconds
	 *            the tickIntervalSeconds to set
	 */
	public void setTickIntervalSeconds(int tickIntervalSeconds) {
		this.tickIntervalSeconds = tickIntervalSeconds;
	}
}
