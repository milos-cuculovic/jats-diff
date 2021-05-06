package main.diff_L1_L2.ui.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageHandler {

	private ResourceBundle resourceBundle = null;

	public String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}


	public MessageHandler(String bundleName) {


		this.resourceBundle = ResourceBundle.getBundle(bundleName);
	}

}
