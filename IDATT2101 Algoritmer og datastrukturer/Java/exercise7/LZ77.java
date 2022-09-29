package exercise7;

public class LZ77 {

	// Buffer-size of 64kB
	private static final int BUFFER_SIZE = 1024 * 64;
	private static final int THRESHOLD = 3;
	private static final int MAX_LENGTH = 127;

	private LZ77() {
	}

	/**
	 * Compress an array of bytes
	 *
	 * @param input the bytes to compress
	 * @return an array of compressed bytes
	 */
	public static byte[] compress(byte[] input) {
		int inputLength = input.length;
		byte[] output = new byte[inputLength * 2];
		int[] indexArray = new int[BUFFER_SIZE];
		int[] chainArray = new int[BUFFER_SIZE];
		int inputIndex = 0;
		int outputIndex = 0;
		int literals = 0;
		while (inputIndex < inputLength) {
			int matchOffset = 0;
			int matchLength = 1;
			// Make sure that the remaining inputs are more than the threshold
			if (inputIndex + THRESHOLD < inputLength) {
				int key = hashBytes(input, inputIndex);
				int searchIndex = indexArray[key & 0xFFFF] - 1;
				while ((inputIndex - searchIndex) < BUFFER_SIZE && searchIndex >= 0) {
					if (inputIndex + matchLength < inputLength && input[inputIndex + matchLength] == input[searchIndex + matchLength]) {
						int length = 0;
						// Iterate through the input from the searchIndex to find the length of the equality
						while (inputIndex + length < inputLength && length < MAX_LENGTH && input[searchIndex + length] == input[inputIndex + length]) {
							length++;
						}
						// If this equality is longer than the ones found before
						if (length > matchLength) {
							matchOffset = inputIndex - searchIndex;
							matchLength = length;
							if (length >= MAX_LENGTH) {
								break;
							}
						}
					}
					// Update the search index from the chain array
					searchIndex = chainArray[searchIndex & 0xFFFF] - 1;
				}
				// Make sure that it's worth to compress
				if (matchLength <= THRESHOLD) {
					matchOffset = 0;
					matchLength = 1;
				}
				int index = inputIndex;
				int end = index + matchLength;
				if (end + THRESHOLD > inputLength) {
					end = inputLength - THRESHOLD;
				}
				while (index < end) {
					// Update the index for each byte of the input to be compressed.
					int key2 = hashBytes(input, index);
					chainArray[index & 0xFFFF] = indexArray[key2 & 0xFFFF];
					indexArray[key2 & 0xFFFF] = index + 1;
					index++;
				}
			}
			if (matchOffset == 0) {
				literals += matchLength;
				inputIndex += matchLength;
			}
			// Flush literals if match found, end of input, or longest encodable run.
			if (literals > 0 && (matchOffset > 0 || inputIndex == inputLength || literals == 127)) {
				output[outputIndex++] = (byte) literals;
				int literalIndex = inputIndex - literals;
				while (literalIndex < inputIndex) {
					output[outputIndex++] = input[literalIndex++];
				}
				literals = 0;
			}
			// Add reference to the match
			if (matchOffset > 0) {
				output[outputIndex++] = (byte) (0x80 | matchLength);
				output[outputIndex++] = (byte) (matchOffset >> 8);
				output[outputIndex++] = (byte) matchOffset;
				inputIndex += matchLength;
			}
		}
		// Shorten the byte-array to the final length
		byte[] finalBytes = new byte[outputIndex];
		if (outputIndex >= 0) System.arraycopy(output, 0, finalBytes, 0, outputIndex);
		return finalBytes;
	}

	/**
	 * Create a kind of hash from the index in the array and the following 3
	 *
	 * @param input the array of bytes
	 * @param index the index in the array
	 * @return an int key
	 */
	private static int hashBytes(byte[] input, int index) {
		int key = (input[index] & 0xFF) * 33 + (input[index + 1] & 0xFF);
		key = key * 33 + (input[index + 2] & 0xFF);
		key = key * 33 + (input[index + 3] & 0xFF);
		return key;
	}

	/**
	 * Decompress an array of bytes
	 *
	 * @param input the bytes to decompress
	 * @return an uncompressed array of bytes
	 */
	public static byte[] decompress(byte[] input) {
		int inputLength = input.length;
		byte[] output = new byte[inputLength * 20];
		int inputIndex = 0;
		int outputIndex = 0;
		// While there is more to uncompress
		while (inputIndex < inputLength) {
			int matchOffset = 0;
			int matchLength = input[inputIndex++] & 0xFF;
			if (matchLength > 127) {
				matchLength = matchLength & 0x7F;
				matchOffset = input[inputIndex++] & 0xFF;
				matchOffset = (matchOffset << 8) | (input[inputIndex++] & 0xFF);
			}
			int outputEnd = outputIndex + matchLength;
			if (matchOffset == 0) {
				// If it's uncompressed
				while (outputIndex < outputEnd) {
					if (inputIndex >= inputLength) break;
					output[outputIndex++] = input[inputIndex++];
				}
			} else {
				// Else find referenced and print that
				while (outputIndex < outputEnd) {
					output[outputIndex] = output[outputIndex - matchOffset];
					outputIndex++;
				}
			}
		}
		// Shorten the byte-array to the final length
		byte[] finalBytes = new byte[outputIndex];
		System.arraycopy(output, 0, finalBytes, 0, outputIndex);
		return finalBytes;
	}
}