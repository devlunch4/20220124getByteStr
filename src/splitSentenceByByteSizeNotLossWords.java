import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class splitSentenceByByteSizeNotLossWords {

    public static void main(String[] args) {

        String srcStr = "안녕하세요. 반갑습니다. 단어구분을 위한 띄어쓰기를 참고하여 설정된 바이트 크기로 문장내 문장절 분리 및 출력합니다." +
                "설정 값이 클수록 확인시 보기 좋습니다. 출력 바이트 크기 지정시 실제값보다 2정도 낮게 하면 더 정확합니다. 알겠죠? 참 어려워.";
        String charset = "UTF-8";
        int wannaOutputByteSize = 30;

        int setByteSize = wannaOutputByteSize - 2; // for safe output.
        setByteSize = Math.max(setByteSize, 5); // for safe output KOREAN.
        List<String> split_List = splitSentenceByByteSizeWithoutLossOfWords(srcStr, charset, setByteSize);

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
