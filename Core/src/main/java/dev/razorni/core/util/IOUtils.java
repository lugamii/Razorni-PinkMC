package dev.razorni.core.util;

import com.google.common.base.Charsets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 8:06 PM
 * Core / rip.orbit.gravity.util
 */
public class IOUtils {

	public static List<String> readLines(InputStream input) throws IOException {
		return readLines(input, Charset.defaultCharset());
	}

	public static List<String> readLines(InputStream input, Charset encoding) throws IOException {
		InputStreamReader reader = new InputStreamReader(input, toCharset(encoding));
		return readLines((Reader)reader);
	}

	public static List<String> readLines(InputStream input, String encoding) throws IOException {
		return readLines(input, toCharset(encoding));
	}

	public static Charset toCharset(Charset charset) {
		return charset == null ? Charset.defaultCharset() : charset;
	}

	public static Charset toCharset(String charset) {
		return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
	}

	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = toBufferedReader(input);
		List<String> list = new ArrayList();

		for(String line = reader.readLine(); line != null; line = reader.readLine()) {
			list.add(line);
		}

		return list;
	}
	public static BufferedReader toBufferedReader(Reader reader) {
		return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
	}

}
