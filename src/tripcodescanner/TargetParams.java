package tripcodescanner;


import java.math.BigInteger;

public class TargetParams {
    private EllipseParams params;
    private String code;
    private BigInteger decimalCode;

    public TargetParams(EllipseParams params, String code) {
        this.params = params;
        this.code = code;
        this.decimalCode = this.convertToDecimal(this.code);
    }

    private BigInteger convertToDecimal(String ternaryCode) {
    	BigInteger decimalCode = BigInteger.ZERO;
        if (ternaryCode != null) {
            for (int i = ternaryCode.length() - 1, j = 0; i >= 0; i--, j++) {
                int bitValue = (Integer.parseInt(ternaryCode.charAt(i) + ""));
//                decimalCode += (bitValue * Math.pow(3, j));
                decimalCode = decimalCode.add(BigInteger.valueOf((long) (bitValue * Math.pow(3, j))));
            }
        }
        return decimalCode;
    }

    public EllipseParams getEllipseParams() {
        return this.params;
    }

    public String getCode() {
        return this.code;
    }

    public BigInteger getDecimalCode() {
        return this.decimalCode;
    }

}