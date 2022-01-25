import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;

// https://stackoverflow.com/questions/48868721/splitting-a-string-with-byte-length-limits-in-java
public class getByteStrList {

    public static void main(String[] args) throws Exception {
        String org = "기능과 앱의 사용 여부는 지역마다 다를 수 있습니다.\n" +
                "* 일부 앱과 게임은 별도로 판매됩니다. Microsoft 365 구독은 별도로 판매됩니다. 일부 레이아웃은 디스플레이 해상도 및 배율 설정에 따라서만 사용 가능합니다.\n" +
                "1 인터넷에 액세스할 수 있어야 합니다. 서비스 이용료가 부과될 수 있습니다.\n" +
                "2 특정 기능을 사용하려면 특정 하드웨어가 필요합니다. Windows 11 사양을 참조하세요. SMS를 통해 채팅하는 것은 일부 국가에서만 가능하며 다른 지역에서는 점차적으로 제공될 예정입니다. 자세한 내용은 이 페이지를 참조하세요. 인터넷에 액세스할 수 있어야 합니다. ISP 이용료가 부과될 수 있습니다.\n" +
                "3 Xbox Game Pass 구독은 별도로 판매됩니다(https://www.xbox.com). 추가 하드웨어 및 구독이 필요할 수도 있습니다. 게임 타이틀, 번호, 기능 및 사용 가능 여부는 시기/디바이스/지역/플랫폼별로 다릅니다(https://www.xbox.com/regions). 정기 멤버십은 취소하는 경우를 제외하고 당시의 최신 정가(변경될 수 있으며 관련 세금이 포함됨)로 계속 요금이 청구됩니다(account.microsoft.com/services).\n" +
                "6 일부 앱과 게임은 별도로 판매됩니다.\n" +
                "7 Windows 11 업그레이드는 2021년 후반에서 2022년에 걸쳐 진행됩니다. 시점은 디바이스마다 다릅니다. 특정 기능을 사용하려면 특정 하드웨어가 필요합니다. Windows 11 사양을 참조하세요.\n" +
                "9 Windows 10 S 모드가 설치된 컴퓨터의 경우 이 버전의 PC 상태 검사 앱을 다운로드하세요. 대부분의 호환되는 디바이스는 이러한 요구 사항을 충족하기 때문에 이 앱은 그래픽 카드나 디스플레이를 확인하지 않습니다(Windows 11 사양 참조).\n" +
                "10 표시된 일부 앱은 나중에 출시될 예정입니다. 특정 앱은 Windows 11에서 Microsoft Store 앱을 통해서만 사용할 수 있습니다.";
        // org = "가 가 가 가 가 가 가 가 가 가 가 가 가 가 가 가 가 ";
        String charset = "cp933";
        //결과 cp933 미인식
        //결과 euc-kr 인식

        List<String> str_list = SplitStringByByteLength(org, charset, 80);
        for (String str : str_list) {
            System.out.println(String.format("%02d", str.getBytes(charset).length) + " Bytes: " + str);
        }

        // 바이트 크기 구하기
        String test = "바이트 구하는 메소드 입니다";
        int length = test.getBytes(charset).length;
        System.out.println("\n$test sentence: " + test);
        System.out.println("byte length: " + length + " Bytes");
    }

    //바이트로 분할, 공백을 통해 단어 분할이 되지 않게 함.
    public static List<String> SplitStringByByteLength(String src, String encoding, int maxsize) throws Exception {
        Charset cs = Charset.forName(encoding);
        CharsetEncoder coder = cs.newEncoder();
        ByteBuffer out = ByteBuffer.allocate(maxsize);  // output buffer of required size
        CharBuffer in = CharBuffer.wrap(src);
        List<String> ss = new ArrayList<>();            // a list to store the chunks
        int pos = 0;
        while (true) {
            CoderResult cr = coder.encode(in, out, true); // try to encode as much as possible
            int newPos = src.length() - in.length();
            String s = src.substring(pos, newPos);
            int endPos = s.lastIndexOf(" ") + 1; // 단어 분할 방지를 위한 공백/띄어쓰기로 구분 분할
            if (endPos <= 0) {
                // 아무것도 안함
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
                    byte[] bLastElem = lastElement.getBytes(encoding);
                    byte[] bIn = in.toString().getBytes(encoding);
                    if (bLastElem.length + bIn.length <= maxsize) {
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
