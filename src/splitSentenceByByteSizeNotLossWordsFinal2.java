import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class splitSentenceByByteSizeNotLossWordsFinal2 {

    /**
     * SPACE in EBCDIC
     */
    public static final byte SPACE = 0x40;
    /**
     * SHIFT OUT in EBCDIC
     */
    public static final byte SHIFT_OUT = 0x0E;
    /**
     * SHIFT IN in EBCDIC
     */
    public static final byte SHIFT_IN = 0x0F;

    public static void main(String[] args) {

        // set text sentence
        String srcStr = "Medium is a place to write, read, and connect It's easy and free to post your thinking on any topic and connect with millions of readers. ";
        srcStr += "미디엄은 쓰고, 읽고, 연결하는 장소입니다. 어떤 주제에든 쉽고 자유롭게 여러분의 생각을 올리고 수백만 명의 독자와 연결할 수 있습니다. ";
        srcStr += "--------------------------------------------------------------------------------";
        // set text Encoding for
        String charset = "CP933";
        int setByteSize = 105; // for safe output.

        List<String> split_List = segmentOutput(srcStr, charset, setByteSize);

        // check Row info in list
        System.out.println("split List: " + split_List);

        int cnt = 0;
        for (String tempStr : split_List) {
            cnt += 1;
            int tempStrLength = 0;
            try {
                tempStrLength = tempStr.getBytes(charset).length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String fmtStr = String.format("%02d", tempStrLength);
            System.out.println("NO." + cnt + ":" + fmtStr + " Bytes : '" + tempStr + "'");
        }


    }

    public static List<String> segmentOutput(String src, String encoding, int maxByteLen) {
        List<String> res = new ArrayList<>();

        try {
            byte[] bSrc = src.getBytes(encoding);
            byte[] bBuffer = new byte[maxByteLen];

            int idxBuffer = 0; // the pointer of buffer to write
            int idxSo = -1; // the last index of Shift-Out
            int idxSpace = -1;

            boolean koStart = false; // SHIFT-OUT started
            byte lastByte = 0; // last byte
            int targetIndex;

            for (byte b : bSrc) {
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

                if (b == SPACE) {
                    idxSpace = idxBuffer;
                    bBuffer[idxBuffer++] = b;
                    lastByte = b;
                } else if (b == SHIFT_OUT) {
                    idxSo = idxBuffer;
                    if (idxBuffer == 0 && (lastByte != SHIFT_OUT && lastByte != SHIFT_IN)
                            || (bBuffer[idxBuffer - 1] != SHIFT_OUT
                            && bBuffer[idxBuffer - 1] != SHIFT_IN)) {
                        bBuffer[idxBuffer++] = b;
                        lastByte = b;
                    }
                    koStart = true;
                } else if (b == SHIFT_IN) {
                    if (idxBuffer == 0 && (lastByte != SHIFT_OUT && lastByte != SHIFT_IN)
                            || (bBuffer[idxBuffer - 1] != SHIFT_OUT
                            && bBuffer[idxBuffer - 1] != SHIFT_IN)) {
                        bBuffer[idxBuffer++] = b;
                        lastByte = b;
                    }
                    koStart = false;
                } else {
                    bBuffer[idxBuffer++] = b;
                    lastByte = b;
                }
            }

            // empty buffer
            if (idxBuffer > 0) {
                res.add(new String(bBuffer, 0, idxBuffer, encoding));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }
}
