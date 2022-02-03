import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class splitSentenceByByteSizeNotLossWords {

    public static void main(String[] args) throws Exception {

        String srcStr = "가가 하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하하안녕하.세요. Hello. 단어구분을 위한 띄어쓰기를 참고하여 설정된 바이트 크기로 문장내 문장절 분리 및 출력합니다." +
                "설정 값이 클수록 확인시 보기 좋습니다. 출력 바이트 크기 지정시 실제값보다 2정도 낮게 하면 더 정확합니다. 알겠죠? 참 어려워. 하하하하하하하하하하하하하하하하";
        String charset = "CP933";
        int wannaOutputByteSize = 80;

        int setByteSize = wannaOutputByteSize; // for safe output.
        //setByteSize = Math.max(setByteSize, 5); // for safe output KOREAN.
        //List<String> split_List = splitSentenceByByteSizeWithoutLossOfWords(srcStr, charset, setByteSize);
        List<String> split_List = bbbb(srcStr, charset, setByteSize);

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


    public static List<String> bbbb(String text, String encoding, int maxLen) throws Exception {
        List<String> res = new ArrayList<String>();

        String regExp = "([\\u3131-\\u314e|\\u314f-\\u3163|\\uac00-\\ud7a3]+)";
        Pattern stringPattern = Pattern.compile(regExp);
        Matcher m = stringPattern.matcher(text);

        int start = -1;
        int tgIdx = 0;
        String prefix = null;
        String tgText = null;

        int prefixLen = 0;
        int tgTextLen = 0;

        StringBuffer sbBuffer = new StringBuffer();
        int bufferLen = 0;

        while (m.find(tgIdx)) {
            start = m.start();
            if(tgIdx < start) {
                prefix = text.substring(tgIdx, start);
            }
            tgText = m.group();

            tgIdx = m.end();

            if (prefix != null) {
                System.out.print(prefix);
                prefixLen = prefix.getBytes(encoding).length;
                if (bufferLen + prefixLen > maxLen) {
                    res.add(sbBuffer.toString());
                    sbBuffer = new StringBuffer();
                    bufferLen = 0;
                }
                sbBuffer.append(prefix);
                bufferLen += prefixLen;
            } else {
                prefixLen = 0;
            }
            tgTextLen = tgText.getBytes(encoding).length + 2;

            if (bufferLen + tgTextLen > maxLen) {
                res.add(sbBuffer.toString());
                sbBuffer = new StringBuffer();
                bufferLen = 0;
            }

            if (tgTextLen > maxLen) {
                String longText = tgText;

                int limitCnt =  (maxLen - 4) / 2;
                String slicedText = null;

                for (int inx = 0; inx < longText.length() ; inx += limitCnt) {
                    if (inx + limitCnt < longText.length()) {
                        slicedText = longText.substring(inx, inx + limitCnt);
                    } else {
                        slicedText = longText.substring(inx);
                    }
                    res.add(slicedText);
                }
                bufferLen = 0;
            } else {
                sbBuffer.append(tgText);
                bufferLen += tgTextLen;
            }

        }

        if (bufferLen > 0) {
            res.add(sbBuffer.toString());
        }

        return res;
    }


    public static List<String> aaaa(String text, String encoding, int maxLen) throws Exception {
        List<String> res = new ArrayList<String>();
//        text = text.replaceAll(, "<SO>$1<SI>");

        String regExp = "([\\u3131-\\u314e|\\u314f-\\u3163|\\uac00-\\ud7a3]+)";
        Pattern stringPattern = Pattern.compile(regExp);
        Matcher m = stringPattern.matcher(text);

        int start = -1;
        int end = -1;
        String ttt = null;
        if(m.find(0)) {
            start = m.start();
            System.out.println(start);
            ttt = m.group();
            System.out.println(ttt);
            end = m.end();
            System.out.println(end);
        }

        String[] splitted = text.split("\\s");
        String SHIFT_OUT = "<";
        String SHIFT_IN = ">";
        StringBuffer sbBuffer = new StringBuffer();
        int curLen = 0;
        int itemLen = 0;
        for (String item : splitted) {
            itemLen = item.getBytes(encoding).length;
            if (curLen + itemLen + 2 == maxLen) {
                curLen += itemLen + 2;
                sbBuffer.append(SHIFT_OUT);
                sbBuffer.append(item);
                sbBuffer.append(SHIFT_IN);
            } else if (curLen + itemLen + 3 < maxLen) {
                curLen += itemLen + 3;
                sbBuffer.append(SHIFT_OUT);
                sbBuffer.append(item + " ");
                sbBuffer.append(SHIFT_IN);
            } else {
                res.add(sbBuffer.toString());
                sbBuffer = new StringBuffer();
                curLen = itemLen + 3;
                sbBuffer.append(SHIFT_OUT);
                sbBuffer.append(item + " ");
                sbBuffer.append(SHIFT_IN);
            }
        }

        if (curLen > 0) {
            res.add(sbBuffer.toString());
        }

        return res;
    }

    // splitSentenceByByteSizeWithoutLossOfWords
    private static List<String> splitSentenceByByteSizeWithoutLossOfWords(String org, String charset, int setByteSize
    ) {

        // set String byte
        Charset cs = Charset.forName(charset);
        CharsetEncoder coder = cs.newEncoder();
        ByteBuffer out = ByteBuffer.allocate(setByteSize); // output buffer of required size
        CharBuffer in = CharBuffer.wrap(org);

        List<String> ss = new ArrayList<>(); // a list to store the chunks

        int pos = 0; // index position start value to be divided. *분할될 인덱스 위치 시작값.
        while (true) {
            CoderResult cr = coder.encode(in, out, true); // try to encode as much as possible
            int newPos = org.length() - in.length();
            String s = org.substring(pos, newPos);

            // 단어 분할 방지를 위한 공백/띄어쓰기로 구분 분할
            // Divide into spaces/spaces to prevent word division.
            int endPos = s.lastIndexOf(" ") + 1;
            if (endPos <= 0) {
                // nothing
                ss.add(s);
            } else {
                // Check for space separation
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
                    // In the case of the last sentence,
                    // the list is added by dividing or adding the sentence according to the set byte size.
                    // *마지막 문장의 경우 설정된 바이트 크기에 맞추어 문장을 분할 또는 추가하여 리스트 추가.
                    String lastElement = ss.get(ss.size() - 1);

                    byte[] bLastElem = null;
                    try {
                        bLastElem = lastElement.getBytes(charset);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    byte[] bIn = null;
                    try {
                        bIn = in.toString().getBytes(charset);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (bLastElem != null && bIn != null && bLastElem.length + bIn.length <= setByteSize) {
                            ss.set(ss.size() - 1, lastElement + in);
                        } else {
                            ss.add(in.toString());
                        }
                    } catch (Exception e) {
                        System.out.println("bLastElem value: " + Arrays.toString(bLastElem));
                        System.out.println("bIn value: " + Arrays.toString(bIn));
                        e.printStackTrace();
                    }
                }
                break; // everything has been encoded
            }
        }
        return ss;
    }
}
