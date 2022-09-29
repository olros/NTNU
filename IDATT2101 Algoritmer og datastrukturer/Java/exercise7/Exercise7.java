package exercise7;

import java.io.*;

public class Exercise7 {

	private static String compressInput = "diverse.lyx";
	private static String compressHalfway = "compressed.lz77";
	private static String compressed = "compressed";
	private static String decompressHalfway = "decompressed.huffman";
	private static String decompressed = "decompressed.lyx";

	public static void main(String[] args) throws IOException {
		compress();
//		decompress();
	}

	// Compress
	private static void compress() {
		try (
				DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(compressInput))));
				DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(compressHalfway)));
		) {
			int inputLength = input.available();
			System.out.println("Input size: " + inputLength);
			byte[] inputData = new byte[inputLength];
			input.readFully(inputData, 0, inputLength);
			byte[] out = LZ77.compress(inputData);
			System.out.println("Size after LZ: " + out.length);
			output.write(out);
			int compressedSize = Huffman.compress(compressHalfway, compressed);
			System.out.println("Size after LZ and Huffman: " + compressedSize);
		} catch (IOException e) {
			System.out.println("Couldn't read file");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Decompress
	private static void decompress() throws IOException {
		int decompressedSize = Huffman.decompress(compressed, decompressHalfway);
		System.out.println("Size after Huffman decompress: " + decompressedSize);

		DataInputStream lzInput = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(decompressHalfway))));
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(decompressed)));

		int inputLength = lzInput.available();
		byte[] inputData = new byte[inputLength];
		lzInput.readFully(inputData, 0, inputLength);
		byte[] decoded = LZ77.decompress(inputData);
		System.out.println("Final size: " + decoded.length);
		output.write(decoded);
	}
}
