package com.bnd.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.util.Date;

/**
 * <code>ConversionUtil</code> is a utility class for reading and writing a file.
 * 
 * @author © Peter Banda
 * @since 2006
 */
public class FileUtil {
	
	/**
	 * The size of the buffer used for handling files.
	 */
	protected static final int BUFFER_SIZE = 1024;
	
	/**
	 * The default charset name to be used for file handling.
	 */
	private static final String DEFAULT_CHARSET_NAME = "8859_1";

	/**
	 * The name of the charset used by encryption utility.
	 */
	private String charsetName;

	/**
	 * Gets the value of the attribute charsetName back.
	 *
	 * @return The value of the attribute charsetName.
	 */
	public String getCharsetName() {
		if (charsetName == null) {
			charsetName = DEFAULT_CHARSET_NAME;
		}
		return charsetName;
	}

	/**
	 * Sets the value of the attribute charsetName.
	 *
	 * @param aCharsetName The charsetName to set.
	 */
	private void setCharsetName(String aCharsetName) {
		charsetName = aCharsetName;
	}

	/**
	 * The singleton of this class.
	 */
	private static FileUtil Instance;
	
	/**
	 * The default constructor.
	 */
	private FileUtil() {
	}
	
	/**
	 * The standard constructor.
	 * 
	 * @param aCharsetName The charset to set.
	 */
	private FileUtil(String aCharsetName) {
		setCharsetName(aCharsetName);
	}

	/**
	 * Gets the instance of this class.
	 * 
	 * @return The singleton-instance. 
	 */
	public static FileUtil getInstance() {
		if (Instance == null) {
			Instance = new FileUtil();
		}
		return Instance;
	}

	/**
	 * Gets the instance of this class.
	 * 
	 * @param aCharsetName The charset to set. 
	 * @return The singleton-instance. 
	 */
	public static FileUtil getInstance(String aCharsetName) {
		if (Instance == null) {
			Instance = new FileUtil(aCharsetName);
		}
		return Instance;
	}
	
	/**
	 * Writes the byte array to a given file.
	 *  
	 * @param aByteArray The content to write to.
	 * @param targetFilename The name of the file to store in.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeByteArrayToFile(byte[] aByteArray, String targetFilename)
	throws FileNotFoundException, IOException {
		ByteArrayInputStream theTargetInputStream = new ByteArrayInputStream(
				aByteArray);
		FileOutputStream theFileOutputStream = new FileOutputStream(
				targetFilename);
		copyStreamContent(theTargetInputStream, theFileOutputStream);
		theFileOutputStream.close();
		theTargetInputStream.close();
	}

	/**
	 * Writes given string to the file.
	 *  
	 * @param string The content to write to.
	 * @param targetFilename The name of the file to store string in.
	 * @param appendFlag The flag indicating append operation.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeStringToFile(String string, String targetFilename, boolean appendFlag)
	throws FileNotFoundException, IOException {
		StringBufferInputStream theTargetInputStream = new StringBufferInputStream(
				string);
		FileOutputStream theFileOutputStream = new FileOutputStream(
				targetFilename, appendFlag);
		copyStreamContent(theTargetInputStream, theFileOutputStream);
		theFileOutputStream.close();
		theTargetInputStream.close();
	}

	/**
	 * Appends given string to the file.
	 *  
	 * @param string The content to write to.
	 * @param targetFilename The name of the file to store string in.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void appendStringToFile(String string, String targetFilename)
	throws FileNotFoundException, IOException {
		writeStringToFile(string, targetFilename, true);
	}

	/**
	 * Reads the byte array from the given file.
	 *  
	 * @param fileName The name of a file to read bytes from.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return The content of file as byte array.
	 */
	public byte[] readByteArrayFromFile(String fileName)
	throws FileNotFoundException, IOException {
		FileInputStream theSourceInputStream = new FileInputStream(fileName);
		ByteArrayOutputStream theSourceOutputStream = new ByteArrayOutputStream(
				BUFFER_SIZE);
		copyStreamContent(theSourceInputStream, theSourceOutputStream);
		byte[] theResult = theSourceOutputStream.toByteArray();
		theSourceInputStream.close();
		theSourceOutputStream.close();
		return theResult;
	}

	/**
	 * Reads the string from the given file.
	 *  
	 * @param fileName The name of a file to read bytes from.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return The content of file as byte array.
	 */
	public String readStringFromFile(String filename)
	throws FileNotFoundException, IOException {
		return new String(readByteArrayFromFile(filename), getCharsetName());
	}

	/**
	 * Copies given source file specified by its path and name to the 
	 * given target file.
	 *  
	 * @param sourceFilename The source filename where to copy from.
	 * @param targetFilename The target filename where to copy.
	 * @param fileName The name of the file to copy.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void copyFileTo(String sourceFilename, String targetFilename)
	throws FileNotFoundException, IOException {
		String theFileContent = readStringFromFile(sourceFilename);
		overwriteStringToFile(theFileContent, targetFilename);
	}
	
	/**
	 * Appends a source file to the given target file.
	 *  
	 * @param sourceFilename The source filename where to append from.
	 * @param targetFilename The target filename where to append.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void appendFileTo(String sourceFilename, String targetFilename)
	throws FileNotFoundException, IOException {
		String theFileContent = readStringFromFile(sourceFilename);
		appendStringToFile(theFileContent, targetFilename);
	}

	/**
	 * Writes given string to the file or in case the file exists,
	 * overwrites file with given string.
	 *  
	 * @param string The content to write or overwrite.
	 * @param targetFilename The name of the file to store string in.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void overwriteStringToFile(String string, String targetFilename)
	throws FileNotFoundException, IOException {
		writeStringToFile(string, targetFilename, false);
	}

	/**
	 * Removes file.
	 * 
	 * @param fileName The name of the file to remove.
	 * @return True, if the deleting was succesful, otherwise false.
	 */
	public boolean removeFile(String fileName) {
		return new File(fileName).delete();
	}

	/**
	 * Checks if the file exists.
	 * 
	 * @param fileName The name of the file to check existence.
	 * @return True, if the file exists, otherwise false.
	 */
	public boolean existFile(String fileName) {
		return new File(fileName).exists();
	}

	/**
	 * Checks if the directory for destination and file name exists.
	 * If doesn't it will be created. 
	 *  
	 * @param destinationFileName The destination file name. 
	 */
	public boolean checkDirectory(String destinationFileName) {
		File theDir = new File(destinationFileName).getParentFile();
		if (!theDir.exists()) {
			return theDir.mkdirs();
		}
	    return true;
	}

	/**
	 * Copies the content of one stream to another one.
	 * 
	 * @param inputStream The source stream.
	 * @param outputStream The target stream.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void copyStreamContent(InputStream inputStream, OutputStream outputStream)
	throws IOException {
		byte[] theCopyBuffer = new byte[BUFFER_SIZE];
		int theNumberOfBytesRead;

		theNumberOfBytesRead = inputStream.read(theCopyBuffer);
		while (theNumberOfBytesRead != -1) {
			outputStream.write(theCopyBuffer, 0, theNumberOfBytesRead);
			theNumberOfBytesRead = inputStream.read(theCopyBuffer);
		}
	}

	/**
	 * Reads the string from the given file.
	 *  
	 * @param fileName The name of a file to read bytes from.
	 * @return The content of file as byte array.
	 * 
	 * @throws BndFileException
	 */
	public String readStringFromFileSafe(String filename) {
		String result = null;
		try {
			result = readStringFromFile(filename);
		} catch (FileNotFoundException e) {
			handleFileNotFoundException(e, filename);
		} catch (IOException e) {
			handleIOException(e, filename);
		}
		return result;
	}

	/**
	 * Copies given source file specified by its path and name to the 
	 * given target file.
	 *  
	 * @param sourceFilename The source filename where to copy from.
	 * @param targetFilename The target filename where to copy.
	 * @param fileName The name of the file to copy.
	 * 
	 * @throws BndFileException
	 */
	public void copyFileToSafe(String sourceFilename, String targetFilename) {
		final String fileContent = readStringFromFileSafe(sourceFilename);
		try {
			overwriteStringToFile(fileContent, targetFilename);
		} catch (FileNotFoundException e) {
			handleFileNotFoundException(e, targetFilename);
		} catch (IOException e) {
			handleIOException(e, targetFilename);
		}
	}
	
	/**
	 * Appends a source file to the given target file.
	 *  
	 * @param sourceFilename The source filename where to append from.
	 * @param targetFilename The target filename where to append.
	 * 
	 * @throws BndFileException
	 */
	public void appendFileToSafe(String sourceFilename, String targetFilename) {
		final String fileContent = readStringFromFileSafe(sourceFilename);
		try {
			appendStringToFile(fileContent, targetFilename);
		} catch (FileNotFoundException e) {
			handleFileNotFoundException(e, targetFilename);
		} catch (IOException e) {
			handleIOException(e, targetFilename);
		}
	}

	/**
	 * Writes given string to the file.
	 *  
	 * @param string The content to write to.
	 * @param targetFilename The name of the file to store string in.
	 * @param appendFlag The flag indicating append operation.
	 * 
	 * @throws BndFileException
	 */
	private void writeStringToFileSafe(String string, String targetFilename, boolean appendFlag) {
		try {
			writeStringToFile(string, targetFilename, appendFlag);
		} catch (FileNotFoundException e) {
			handleFileNotFoundException(e, targetFilename);
		} catch (IOException e) {
			handleIOException(e, targetFilename);
		}
	}

	/**
	 * Appends given string to the file.
	 *  
	 * @param string The content to write to.
	 * @param targetFilename The name of the file to store string in.
	 * 
	 * @throws BndFileException
	 */
	public void appendStringToFileSafe(String string, String targetFilename) {
		writeStringToFileSafe(string, targetFilename, true);
	}

	/**
	 * Writes given string to the file or in case the file exists,
	 * overwrites file with given string.
	 *  
	 * @param string The content to write or overwrite.
	 * @param targetFilename The name of the file to store string in.

	 * @throws BndFileException
	 */
	public void overwriteStringToFileSafe(String string, String targetFilename) {
		writeStringToFileSafe(string, targetFilename, false);
	}

	private void handleIOException(IOException exception, String filename) {
		throw new BndFileException("IO exception occurred while reading a file '" + filename + "'.", exception);
	}

	private void handleFileNotFoundException(FileNotFoundException exception, String filename) {
		throw new BndFileException("File '" + filename + "' can't be found.", exception);
	}

	/**
	 * Gets the value of the date as String back.
	 * 
	 * @param aDate The date to handle.
	 * @return The value of the date as String.
	 */
	@Deprecated
	public String getDateAsString(Date aDate) {
		StringBuffer theSB = new StringBuffer();
		theSB.append((1900 + aDate.getYear()));
		theSB.append("-");
		theSB.append(aDate.getMonth() + 1);
		theSB.append("-");
		theSB.append(aDate.getDate());
		theSB.append("-");
		theSB.append(aDate.getHours());
		theSB.append("-");
		theSB.append(aDate.getMinutes());
		theSB.append("-");
		theSB.append(aDate.getSeconds());
		return theSB.toString();
	}
}
