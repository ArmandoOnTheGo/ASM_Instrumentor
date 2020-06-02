public class C {
	public int mid(int x, int y, int z) {
		int m = z;
		if (y < z) {
			if (x < y)
				m = y;
			else if (x < z)
				m = x;
		} else {
			if (x > y)
				m = y;
			else if (x > z)
				m = x;
		}
		return m;
	}
}