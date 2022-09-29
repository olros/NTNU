package exercise7;

public class Bitstring {
	int lengde;
	long biter;

	Bitstring() {
	}

	Bitstring(int len, long bits) {
		lengde = len;
		biter = bits;
	}

	Bitstring(int len, byte b) {
		this.lengde = len;
		this.biter = convertByte(b, len);
	}

	static Bitstring concat(Bitstring s1, Bitstring s2) {
		Bitstring ny = new Bitstring();
		ny.lengde = s1.lengde + s2.lengde;
		if (ny.lengde > 64) {
			System.out.println("For lang bitstreng, gÃ¥r ikke!");
			return null;
		}
		ny.biter = s2.biter | (s1.biter << s2.lengde);
		return ny;
	}

	public long convertByte(byte b, int length) {
		long temp = 0;
		for (long i = 1 << length - 1; i != 0; i >>= 1) {
			if ((b & i) == 0) {
				temp = (temp << 1);
			} else temp = ((temp << 1) | 1);
		}
		return temp;
	}

	public void remove() {
		this.biter = (biter >> 1);
		this.lengde--;
	}
}