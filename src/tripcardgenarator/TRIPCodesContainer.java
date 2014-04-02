package tripcardgenarator;

public class TRIPCodesContainer {
	
	private final String ternaryCode;
	private final String ringCode1;
	private final String ringCode2;
	private final int bitCount;

	
	public TRIPCodesContainer(StringBuilder ternaryCode, StringBuilder ringCode1, StringBuilder ringCode2, int bitCount) {
		this.ternaryCode = ternaryCode.toString();
		this.ringCode1 = ringCode1.toString();
		this.ringCode2 = ringCode2.toString();
		this.bitCount = bitCount;
	}

	
	public String getTernaryCode() {
		return ternaryCode;
	}

	
	public String getRingCode1() {
		return ringCode1;
	}

	
	public String getRingCode2() {
		return ringCode2;
	}


	public int getBitCount() {
		return bitCount;
	}

} // method
