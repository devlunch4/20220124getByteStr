import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SplitTest20220204 {
	/** SHIFT OUT in EBCDIC */
	public static final byte SHIFT_OUT = 0x0E;
	/** SHIFT IN in EBCDIC */
	public static final byte SHIFT_IN = 0x0F;
	/** the range of korean language for the regular expression */
	public static final String KoPattern = "[\u3131-\u318E\uAC00-\uD7A3]";
	
	/**
	 * split the string
	 * @param src source to split
	 * @param encoding charset
	 * @param maxByteLen max byte length
	 * @return splitted list of string.
	 * @throws UnsupportedEncodingException
	 */
	public static List<String> splitEbcdic(String src, String encoding, int maxByteLen) throws UnsupportedEncodingException {
		List<String> 	res 		= new ArrayList<String>();

		byte[] 			bSrc 		= src.getBytes(encoding);
		byte[] 			bBuffer 	= new byte[maxByteLen];
		
		int 			idxBuffer 	= 0; // the pointer of buffer to write
		int 			idxSo 		= -1; // the last index of Shift-Out
		
		boolean 		koStart 	= false; // SHIFT-OUT started
		byte 			lastByte 	= 0; // last byte
		
		for (int inx = 0;inx < bSrc.length; inx ++) {
			// check the flush condition
			if (idxBuffer >= (maxByteLen)) {
				if (koStart) {
					if ((idxBuffer - idxSo) % 2 == 0) {
						byte temp = bBuffer[idxBuffer - 1];
						bBuffer[idxBuffer - 1] = SHIFT_IN;
						res.add(new String(bBuffer, 0, idxBuffer, encoding));
						bBuffer = new byte[maxByteLen];
						bBuffer[0] = SHIFT_OUT;
						bBuffer[1] = temp;
						lastByte = temp;
						idxBuffer = 2;
					} else {
						if (lastByte == SHIFT_OUT) {
							res.add(new String(bBuffer, 0, idxBuffer - 1, encoding));
							idxBuffer = 1;
							bBuffer = new byte[maxByteLen];
							bBuffer[0] = SHIFT_OUT;
						} else {
							byte temp1 = bBuffer[idxBuffer - 2];
							byte temp2 = bBuffer[idxBuffer - 1];
							bBuffer[idxBuffer - 2] = SHIFT_IN;
							res.add(new String(bBuffer, 0, idxBuffer - 2, encoding));
							bBuffer = new byte[maxByteLen];
							bBuffer[0] = SHIFT_OUT;
							bBuffer[1] = temp1;
							bBuffer[2] = temp2;
							lastByte = temp2;
							idxBuffer = 3;
						}
					}
				} else {
					res.add(new String(bBuffer, 0, idxBuffer, encoding));
					idxBuffer = 0;
					bBuffer = new byte[maxByteLen];
				}
			}
			
			
			if (bSrc[inx] == SHIFT_OUT) {
				idxSo = idxBuffer;
				if (idxBuffer == 0 && (lastByte != SHIFT_OUT && lastByte != SHIFT_IN) || (bBuffer[idxBuffer - 1] != SHIFT_OUT && bBuffer[idxBuffer - 1] != SHIFT_IN)) {
					bBuffer[idxBuffer++] = bSrc[inx];
					lastByte = bSrc[inx];
				}
				koStart = true;
			} else if (bSrc[inx] == SHIFT_IN) {
				if (idxBuffer == 0 && (lastByte != SHIFT_OUT && lastByte != SHIFT_IN) || (bBuffer[idxBuffer - 1] != SHIFT_OUT && bBuffer[idxBuffer - 1] != SHIFT_IN)) {
					bBuffer[idxBuffer++] = bSrc[inx];
					lastByte = bSrc[inx];
				}
				koStart = false;
			} else {
				bBuffer[idxBuffer++] = bSrc[inx];
				lastByte = bSrc[inx];
			}
		}
		
		// empty buffer
		if (idxBuffer > 0) {
			res.add(new String(bBuffer, 0, idxBuffer, encoding));
		}
		
		return res;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String src = "1일이삼사오륙칠팔12345678901234567890가나다라 마바사아 자차카 타파하에이사아자차카 비이. ++++++ 123 가가가abcdef ghijklmnop카카카";
		String encoding = "MS949";
		List<String> res = SplitTest20220204.splitEbcdic(src, encoding, 20);
		
		for (String item:res) {
			System.out.println("(" + item.getBytes(encoding).length + "):" + item);
		}
	}

}
