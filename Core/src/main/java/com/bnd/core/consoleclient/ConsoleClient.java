package com.bnd.core.consoleclient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.util.FileUtil;
import org.apache.commons.lang.StringUtils;

/**
 * <code>ConsoleClient</code> is the abstract superclass of all command line clients.
 * It's a single entry point to the application.
 * 
 * @author © Peter Banda
 * @since 2011
 */
public abstract class ConsoleClient {

	private static final String DEFAULT_AUTHOR = "Peter Banda";
	protected static final String OPTION_FLAG_SHOW_MANUAL = "m";

	private final FileUtil fileUtil = FileUtil.getInstance();

	private ConsoleClientOption<?>[] defaultOptions;
	private ConsoleClientOption<?>[] options;
	private Map<String, ConsoleClientOption<?>> optionMap = new HashMap<String, ConsoleClientOption<?>>();
	private Map<ConsoleClientOption<?>, Object> optionValueMap = new HashMap<ConsoleClientOption<?>, Object>();
	private String args[];

	public ConsoleClient(String args[]) {
		this.args = args;
		defaultOptions = createDefaultOptions(); 
		options = createOptions();
		addOptions(defaultOptions);
		addOptions(options);
	}

	private void addOptions(ConsoleClientOption<?>[] options) {
		for (ConsoleClientOption<?> option : options) {
			if (optionMap.containsKey(option.getFlag())) {
				throw new RuntimeException("Ambiguous definition of option (" + option.getFlag() + ")");	
			} else {
				optionMap.put(option.getFlag(), option);
			}
		}
	}

	private static ConsoleClientOption<?>[] createDefaultOptions() {
		return new ConsoleClientOption<?>[] {
			new ConsoleClientOption<Boolean>(OPTION_FLAG_SHOW_MANUAL, "help", Boolean.class, Boolean.FALSE ,"prints this manual (help)")
		};
	}

	protected abstract ConsoleClientOption<?>[] createOptions();

	protected ConsoleClientOption<?> getOption(String flag) {
		return optionMap.get(flag);
	}

	protected Object getOptionValue(String flag) {
		ConsoleClientOption<?> option = getOption(flag);
		if (option != null) {
			return getOptionValue(option);
		}
		throw new ConsoleClientException("Option flag " + flag + " not recognized!");
	}

	protected <T> T getOptionValue(ConsoleClientOption<T> option) {
		T value = (T) optionValueMap.get(option);
		if (value == null) {
			value = option.getDefaultValue();
		}
		return value;
	}

	private void setOptionValue(ConsoleClientOption<?> option, String value) {
		optionValueMap.put(option, option.getConvertedValue(value));
	}

	protected String getInfo(ConsoleClientOption<?> option) {
		StringBuffer sb = new StringBuffer();
		sb.append("-");
		sb.append(option.getFlag());
		sb.append(" (");
		sb.append(option.getName());
		sb.append(") ");
		return sb.toString();
	}

	protected String getInfo(ConsoleClientOption<?>[] options) {
		Collection<String> infos = new ArrayList<String>();
		for (ConsoleClientOption<?> option : options) {
			infos.add(getInfo(option));
		}
		return StringUtils.join(infos, ", ");
	}

	private void showManual() {
		showHeader();
		showOptionDescriptions();
		println("");
	};

	protected void showHeader() {
		println("");
		println(getAppName() + " - © " + getCreationYear() + " " + getAuthor());
		println("https://peterbanda.net");
		println("");
		println("use: java -jar " + getJarName() + " [options]");
		println("");
		println("---------------------------------------------------------");
		println("");
	}

	protected void showOptionDescriptions() {
		println("Options:");
		println("");
		for (final ConsoleClientOption<?> option : options) {
			println(option.toString());
		}
		for (final ConsoleClientOption<?> defaultOption : defaultOptions) {
			println(defaultOption.toString());
		}
	}

	private boolean isOption(int argIndex) {
		String s = args[argIndex];
		return s != null && s.startsWith("-");
	}

	private String getOption(int argIndex) {
		String s = args[argIndex];
		String arg = s.substring(1, s.length());
		return arg.toLowerCase();
	}

	private boolean isValue(int argIndex) {
		return !isOption(argIndex);
	}

	private String getStringSafe(int argIndex, String variableName) {
		if (argIndex >= args.length) {
			throw new ConsoleClientException("No value found after " + args[argIndex - 1] + " option.");
		}
		if (isValue(argIndex)) {
			return args[argIndex];
		}
		throw new ConsoleClientException(variableName + " not specified!");
	}

	private void readOptions() {
		if (args == null || args.length == 0) {
			showManual();
			return;
		}
		for (int i = 0; i < args.length; i++) {
			if (isOption(i)) {     // it looks like option
				String optionFlag = getOption(i);
				ConsoleClientOption<?> option = optionMap.get(optionFlag);
				if (option != null) {
					if (option.getType() != Boolean.class) {
						i++;
						setOptionValue(option, getStringSafe(i, option.getName()));
					} else {
						setOptionValue(option, "true");
					}
				} else {
					throw new ConsoleClientException("Option " + args[i] + " not recognized.");
				}
			} else {
				throw new ConsoleClientException("Input '" + args[i] + "' is not valid. Option must start with \'-\'.");
			}
		}
	}

	protected boolean isShowManualFlag() {
		return (Boolean) getOptionValue(OPTION_FLAG_SHOW_MANUAL);
	}

	protected void validate() {
		for (ConsoleClientOption<?> option : optionMap.values()) {
			if (option.isMandatory()) {
				checkOptionNotNull(option);
			}			
		}
	}

	protected void checkOptionNotNull(ConsoleClientOption<?> option) {
		if (isOptionNull(option)) {
			throw new ConsoleClientException("The -" + option.getFlag() + " option expected!");
		}		
	}

	protected boolean isOptionNull(ConsoleClientOption<?> option) {
		return getOptionValue(option) == null;
	}

	protected void checkOptionsNotNull(ConsoleClientOption<?>[] options) {
		for (ConsoleClientOption<?> option : options) {
			checkOptionNotNull(option);
		}
	}

	protected void checkAtLeastOneOfOptionsNotNull(ConsoleClientOption<?>[] options, String errorMessage) {
		for (ConsoleClientOption<?> option : options) {
			if (!isOptionNull(option)) {
				return;
			}
		}
		throw new ConsoleClientException(errorMessage + getInfo(options));
	}

	protected void checkExactlyOneOptionNotNull(ConsoleClientOption<?>[] options, String errorMessage) {
		boolean notNullFound = false;
		for (ConsoleClientOption<?> option : options) {
			if (notNullFound) {
				if (!isOptionNull(option)) {
					throw new ConsoleClientException(errorMessage + getInfo(options));
				}
			} else if (!isOptionNull(option)) {
				notNullFound = true;
			}
		}
	}

	private boolean handleManual() {
		if (isShowManualFlag()) {
			println("Showing manual (help). Other options are ignored...");
			showManual();
			return true;
		}
		return false;
	}
	
	protected abstract void execute();

	public void run() {
		try {			
			readOptions();
			if (!handleManual()) {
			    validate();
				execute();
			}
		} catch (ConsoleClientException e) {
			println("Console client error occured:");
			println(" " + e.getMessage());
		} catch (BndRuntimeException e) {
			println("Application error occured:");
			println(" " + e.getMessage());			
		} catch (Exception e) {
			println("Fatal error occured:");
			println(" " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected String getAuthor() {
		return DEFAULT_AUTHOR;
	}

	protected int getCreationYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	protected abstract String getAppName();

	protected abstract String getJarName();

	protected abstract String getVersion();

	protected void writeToFile(String fileName, String content) {
		fileUtil.overwriteStringToFileSafe(content, fileName);
	}

	protected String readFromFile(String fileName) {
		return fileUtil.readStringFromFileSafe(fileName);
	}

	protected boolean existsFile(String fileName) {
		return fileUtil.existFile(fileName);
	}

	protected void println(String string) {
		System.out.println(string);
	}

	protected void print(String string) {
		System.out.print(string);
	}

	protected void printlnExecutionTimeInfo(Date startTime) {
		println("The task sucussfully finished in " + (new Date().getTime() - startTime.getTime()) + "ms.");
	}
}