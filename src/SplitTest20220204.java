import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;

public class SplitTest20220204 {

    /** ADDED */
    /** SPACE in EBCDIC */
    public static final byte SPACE = 0x40;
    /** SHIFT OUT in EBCDIC */
    public static final byte SHIFT_OUT = 0x0E;
    /** SHIFT IN in EBCDIC */
    public static final byte SHIFT_IN = 0x0F;

    private //@Value("${preproc.translate.charset.in.value}")
    String charsetTransIn;
    private //@Value("${preproc.translate.charset.byte.value}")
    String charsetTransByte;
    private //@Value("${output.limit}")
    Integer outputLimit; // Output byte size limit length value

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



    // plus



    /**
     * split the string for EBCDIC
     *
     * @param src        source to split
     * @param encoding   charset
     * @param maxByteLen max byte length
     * @return splitted list of string.
     * @throws UnsupportedEncodingException
     */
    public static List<String> segmentOutputEbcdic(String src, String encoding, int maxByteLen) {
        // for Byte size calculation error eliminated due to tagId and separator.
        String rowIdDelim = src.indexOf(ProcRow.delim) >= 0
                ? src.substring(0, src.indexOf(ProcRow.delim) + 1)
                : "";
        src = src.indexOf(ProcRow.delim) >= 0 ? src.substring(src.indexOf(ProcRow.delim) + 1) : src;

        // segmentOutputEbcdic Start
        List<String> res = new ArrayList<String>();

        try {
            byte[] bSrc = src.getBytes(encoding);
            byte[] bBuffer = new byte[maxByteLen];

            int idxBuffer = 0; // the pointer of buffer to write
            int idxSo = -1; // the last index of Shift-Out
            int idxSpace = -1;

            boolean koStart = false; // SHIFT-OUT started
            byte lastByte = 0; // last byte
            int targetIndex = 0;

            for (int inx = 0; inx < bSrc.length; inx++) {
                // check the flush condition
                if (idxBuffer >= (maxByteLen)) {
                    if (idxSpace > 0 && idxSpace <= (idxBuffer - 2)) {
                        targetIndex = idxSpace;
                        byte[] tmp = new byte[idxBuffer - 1 - targetIndex];
                        for (int jnx = 0; jnx < tmp.length; jnx++) {
                            tmp[jnx] = bBuffer[targetIndex + jnx + 1];
                        }

                        res.add(new String(bBuffer, 0, targetIndex + 1, encoding));
                        bBuffer = new byte[maxByteLen];

                        for (int jnx = 0; jnx < tmp.length; jnx++) {
                            bBuffer[jnx] = tmp[jnx];
                            lastByte = tmp[jnx];
                            idxBuffer = jnx + 1;
                        }
                        idxSpace = -1;
                    } else {

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
                                idxSpace = -1;
                            } else {
                                if (lastByte == SHIFT_OUT) {
                                    res.add(new String(bBuffer, 0, idxBuffer - 1, encoding));
                                    idxBuffer = 1;
                                    idxSpace = -1;
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
                                    idxSpace = -1;
                                }
                            }
                        } else {
                            res.add(new String(bBuffer, 0, idxBuffer, encoding));
                            idxBuffer = 0;
                            idxSpace = -1;
                            bBuffer = new byte[maxByteLen];
                        }
                    }
                }

                if (bSrc[inx] == SPACE) {
                    idxSpace = idxBuffer;
                    bBuffer[idxBuffer++] = bSrc[inx];
                    lastByte = bSrc[inx];
                } else if (bSrc[inx] == SHIFT_OUT) {
                    idxSo = idxBuffer;
                    if (idxBuffer == 0 && (lastByte != SHIFT_OUT && lastByte != SHIFT_IN)
                            || (bBuffer[idxBuffer - 1] != SHIFT_OUT
                            && bBuffer[idxBuffer - 1] != SHIFT_IN)) {
                        bBuffer[idxBuffer++] = bSrc[inx];
                        lastByte = bSrc[inx];
                    }
                    koStart = true;
                } else if (bSrc[inx] == SHIFT_IN) {
                    if (idxBuffer == 0 && (lastByte != SHIFT_OUT && lastByte != SHIFT_IN)
                            || (bBuffer[idxBuffer - 1] != SHIFT_OUT
                            && bBuffer[idxBuffer - 1] != SHIFT_IN)) {
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // for Byte size calculation error eliminated due to tagId and separator.
        // RESET for res.
        if (rowIdDelim != null && rowIdDelim.length() > 0 && res.size() > 0) {
            res.set(0, rowIdDelim + res.get(0));
        }

        return res;
    }

    // plus2

    // setting output sentence byte size
    private List<String> segmentOutputOthers(String org) {
        Charset cs = Charset.forName(charsetTransByte);
        CharsetEncoder coder = cs.newEncoder();
        ByteBuffer out = ByteBuffer.allocate(outputLimit); // output buffer of required size
        CharBuffer in = CharBuffer.wrap(org);
        List<String> ss = new ArrayList<>(); // a list to store the chunks
        int pos = 0;
        while (true) {
            CoderResult cr = coder.encode(in, out, true); // try to encode as much as possible
            int newPos = org.length() - in.length();
            String s = org.substring(pos, newPos);
            int endPos = s.lastIndexOf(" ") + 1; // 단어 분할 방지를 위한 공백/띄어쓰기로 구분 분할
            if (endPos <= 0) {
                // nothing
                ss.add(s);
            } else {
                // 공백 구분자를 통해 분할을 실시
                String chunk = s.substring(0, endPos);
                ss.add(chunk);
                in = CharBuffer.wrap(s.substring(endPos) + in);
                newPos -= s.substring(endPos).length();
            }
            // add what has been encoded to the list
            pos = newPos; // store new input position
            out.rewind(); // and rewind output buffer
            if (!cr.isOverflow()) {
                if (in.length() > 0) {
                    // 마지막 문장의 경우 설정된 바이트 크기에 맞추어 문장을 분할 또는 추가하여 리스트 추가.
                    String lastElement = ss.get(ss.size() - 1);
                    byte[] bLastElem = null;
                    try {
                        bLastElem = lastElement.getBytes(charsetTransByte);
                    } catch (UnsupportedEncodingException e) {
                        // log.error("in segmentOutput() #1");
                        e.printStackTrace();
                    }
                    byte[] bIn = null;
                    try {
                        bIn = in.toString().getBytes(charsetTransByte);
                    } catch (UnsupportedEncodingException e) {
                        // log.error("in segmentOutput() #2");
                        e.printStackTrace();
                    }
                    if (bLastElem.length + bIn.length <= outputLimit) {
                        ss.set(ss.size() - 1, lastElement + in);
                    } else {
                        ss.add(in.toString());
                    }
                }
                break; // everything has been encoded
            }
        }
        return ss;
    }
}
