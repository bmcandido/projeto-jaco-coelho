package br.com.sankhya.jaco.integracao.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import br.com.sankhya.modelcore.MGEModelException;

public class HelperLog {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final int LINE_WIDTH = 180;

	private static String getTimestamp() {
		return dateFormat.format(new Date());
	}

	private static List<String> wrapText(String text, int width) {
		List<String> lines = new ArrayList<>();
		int length = text.length();
		for (int start = 0; start < length; start += width) {
			lines.add(text.substring(start, Math.min(length, start + width)));
		}
		return lines;
	}

	private static String formatMessage(String level, String message, String className, String methodName,
			int lineNumber) {
		String timestamp = getTimestamp();
		List<String> messageLines = wrapText(message, LINE_WIDTH);

		StringBuilder formattedMessage = new StringBuilder();
		formattedMessage.append(
				"----------------------------------------------------------------------------------------------------------------\n");
		formattedMessage.append("LOG MESSAGE\n");
		formattedMessage.append(
				"----------------------------------------------------------------------------------------------------------------\n");
		formattedMessage.append(String.format("Level: %s\n", level));
		formattedMessage.append(String.format("Time: %s\n", timestamp));
		formattedMessage.append(String.format("Class: %s\n", className));
		formattedMessage.append(String.format("Method: %s\n", methodName));
		formattedMessage.append(String.format("Line: %d\n", lineNumber));
		formattedMessage.append(
				"----------------------------------------------------------------------------------------------------------------\n");
		formattedMessage.append("Message:\n");

		for (String line : messageLines) {
			formattedMessage.append(String.format("%s\n", line));
		}

		formattedMessage.append(
				"----------------------------------------------------------------------------------------------------------------");

		return formattedMessage.toString();
	}

	private static String formatMessageError(String level, String message, String className, String methodName,
			int lineNumber) {
		String timestamp = getTimestamp();
		List<String> messageLines = wrapText(message, LINE_WIDTH);

		StringBuilder formattedMessage = new StringBuilder();

		formattedMessage.append(String.format("Level: %s\n", level));
		formattedMessage.append(String.format("Time: %s\n", timestamp));
		formattedMessage.append(String.format("Class: %s\n", className));
		formattedMessage.append(String.format("Method: %s\n", methodName));
		formattedMessage.append(String.format("Line: %d\n", lineNumber));
		formattedMessage.append(message);

		return formattedMessage.toString();
	}

	private static String formatInfoMessage(String message) {
		String timestamp = getTimestamp();
		List<String> messageLines = wrapText(message, LINE_WIDTH);

		StringBuilder formattedMessage = new StringBuilder();
		formattedMessage.append("LOG INFO\n");
		formattedMessage.append(String.format("Time: %s\n", timestamp));
		formattedMessage.append("[MESSAGE]:\n");

		for (String line : messageLines) {
			formattedMessage.append(String.format("%s\n", line));
		}

		return formattedMessage.toString();
	}

	private static String formatInfoMessageJSON(String tipMensage, String message) {
		String timestamp = getTimestamp();
		List<String> messageLines = wrapText(message, LINE_WIDTH);

		StringBuilder formattedMessage = new StringBuilder();
		formattedMessage.append("[" + tipMensage + "]\n");
		formattedMessage.append(String.format("Time: %s\n", timestamp));
		formattedMessage.append("[MESSAGE]:\n");

		return formattedMessage.toString();
	}

	private static String formatInfoMessage(String message, boolean noViewLogCabecalho) {
		String timestamp = getTimestamp();
		List<String> messageLines = wrapText(message, LINE_WIDTH);

		StringBuilder formattedMessage = new StringBuilder();
		formattedMessage.append("LOG INFO\n");
		if (!noViewLogCabecalho) {
			formattedMessage.append(String.format("Time: %s\n", timestamp));
			formattedMessage.append("[MESSAGE]:\n");
		}

		for (String line : messageLines) {
			formattedMessage.append(String.format("%s\n", line));
		}

		return formattedMessage.toString();
	}

	public void info(boolean showLogs, String message) {
		if (showLogs) {

			System.out.println(formatInfoMessage(message));

		}
	}

	public static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping()
			.addSerializationExclusionStrategy(new GsonSerializationExclusionConfig())
			.addDeserializationExclusionStrategy(new GsonDeserializationExclusionConfig()).create();
	private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

	public void infoJson(boolean showLogs, String tipmensage, Object json) {
		if (showLogs) {

			final String jsonBeatyfull = beautiful(prettyGson.toJson(json)).replace("\\\"", "");
			System.out.println(formatInfoMessageJSON(tipmensage, jsonBeatyfull));
		}
	}

	public void info(boolean showLogs, String message, boolean noViewLogCabecalho) {
		if (showLogs) {
			System.out.println(formatInfoMessage(message, noViewLogCabecalho));
		}
	}

	public void debug(boolean showLogs, String message) {
		if (showLogs) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
			String className = caller.getClassName();
			String methodName = caller.getMethodName();
			int lineNumber = caller.getLineNumber();
			System.out.println(formatMessage("DEBUG", message, className, methodName, lineNumber));
		}
	}

	public void error(boolean showLogs, String message) {
		if (showLogs) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
			String className = caller.getClassName();
			String methodName = caller.getMethodName();
			int lineNumber = caller.getLineNumber();
			System.out.println(formatMessage("ERROR", message, className, methodName, lineNumber));
		}
	}

	public void error(boolean showLogs, String message, Throwable throwable) {
		if (showLogs) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
			String className = caller.getClassName();
			String methodName = caller.getMethodName();
			int lineNumber = caller.getLineNumber();
			System.out.println(
					formatMessage("ERROR", message + "\n" + throwable.toString(), className, methodName, lineNumber));
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			System.out.println(sw.toString());
		}

	}

	public void error(boolean showLogs, String message, Throwable throwable, boolean showThrowExceptionSNK)
			throws MGEModelException {
		if (showLogs) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
			String className = caller.getClassName();
			String methodName = caller.getMethodName();
			int lineNumber = caller.getLineNumber();
			String formattedMessage = formatMessage("ERROR", message + "\n" + throwable.toString(), className,
					methodName, lineNumber);
			System.out.println(formattedMessage);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			System.out.println(sw.toString());
		}

		if (showThrowExceptionSNK) {
			throw new MGEModelException(message, throwable);
		}
	}

	public String logErrorAndReturnMessage(boolean showLogs, String message, Throwable throwable)
			throws MGEModelException {
		StringBuilder errorMessage = new StringBuilder();

		if (showLogs) {
			StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
			String className = caller.getClassName();
			String methodName = caller.getMethodName();
			int lineNumber = caller.getLineNumber();

			// Formatar a mensagem de erro
			String formattedMessage = formatMessageError("ERROR", message + "\n" + throwable.toString(), className,
					methodName, lineNumber);
			errorMessage.append(formattedMessage).append("\n");

			// Capturar a stack trace
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			errorMessage.append(sw.toString());
			System.out.println(message + "\n" + formattedMessage + "\n" + sw.toString());
		}

		return errorMessage.toString();
	}

	private String beautiful(String input) {
		int tabCount = 0;

		StringBuilder inputBuilder = new StringBuilder();
		char[] inputChar = input.toCharArray();

		for (int i = 0; i < inputChar.length; i++) {
			String charI = String.valueOf(inputChar[i]);
			if (charI.equals("}") || charI.equals("]")) {
				tabCount--;
				if (!String.valueOf(inputChar[i - 1]).equals("[") && !String.valueOf(inputChar[i - 1]).equals("{"))
					inputBuilder.append(newLine(tabCount));
			}
			inputBuilder.append(charI);

			if (charI.equals("{") || charI.equals("[")) {
				tabCount++;
				if (String.valueOf(inputChar[i + 1]).equals("]") || String.valueOf(inputChar[i + 1]).equals("}"))
					continue;

				inputBuilder.append(newLine(tabCount));
			}

			if (charI.equals(",")) {
				inputBuilder.append(newLine(tabCount));
			}
		}

		return inputBuilder.toString();
	}

	private static String newLine(int tabCount) {
		StringBuilder builder = new StringBuilder();

		builder.append("\n");
		for (int j = 0; j < tabCount; j++)
			builder.append("  ");

		return builder.toString();
	}
}

class GsonDeserializationExclusionConfig implements ExclusionStrategy {
	public GsonDeserializationExclusionConfig() {
	}

	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

	public boolean shouldSkipField(FieldAttributes fieldAttr) {
		Expose expose = (Expose) fieldAttr.getAnnotation(Expose.class);
		boolean deserialize = ObjectUtil.nonNull(expose) && !expose.deserialize();
		return deserialize;
	}
}

class ObjectUtil {
	public ObjectUtil() {
	}

	public static boolean isNull(Object object) {
		return object == null;
	}

	public static boolean isNotNull(Object object) {
		return !isNull(object);
	}

	public static boolean nonNull(Object object) {
		return !isNull(object);
	}

	public static void requireNonNull(Object object, String message) {
		if (isNull(object)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static boolean isEquals(Object target, Object model) {
		if (target == null && model == null) {
			return true;
		} else {
			return target != null && model != null ? target.equals(model) : false;
		}
	}

	public static boolean isNotEquals(Object target, Object model) {
		return !isEquals(target, model);
	}

	public static boolean isEmpty(Object[] array) {
		return isNull(array) || array.length == 0;
	}

	public static boolean isEmpty(Collection<?> collection) {
		return isNull(collection) || collection.size() == 0;
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	public static final <T> List<T> toList(Collection<T> collection) {
		return collection != null && !collection.isEmpty() ? new ArrayList(collection) : new ArrayList();
	}
}

class GsonSerializationExclusionConfig implements ExclusionStrategy {
	public GsonSerializationExclusionConfig() {
	}

	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}

	public boolean shouldSkipField(FieldAttributes fieldAttr) {
		Expose expose = (Expose) fieldAttr.getAnnotation(Expose.class);
		boolean serialize = ObjectUtil.nonNull(expose) && !expose.serialize();
		return serialize;
	}
}
