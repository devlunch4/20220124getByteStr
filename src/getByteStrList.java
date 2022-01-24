import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;

public class getByteStrList {

    public static void main(String[] args) throws Exception{
        String org = "안녕하세요 반갑습니다. 텍스트를 바이트 크기로 자르고 있습니다.";
        List<String> str_list = SplitStringByByteLength(org, "cp933", 28);
        for (String str:str_list){
            System.out.println(str.getBytes("CP933").length + ": " + str);
        }
    }

    public static List<String> SplitStringByByteLength(String src, String encoding, int maxsize) {
        Charset cs = Charset.forName(encoding);
        CharsetEncoder coder = cs.newEncoder();
        ByteBuffer out = ByteBuffer.allocate(maxsize);  // output buffer of required size
        CharBuffer in = CharBuffer.wrap(src);
        List<String> ss = new ArrayList<>();            // a list to store the chunks
        int pos = 0;
        while(true) {
            CoderResult cr = coder.encode(in, out, true); // try to encode as much as possible
            int newpos = src.length() - in.length();
            String s = src.substring(pos, newpos);
            ss.add(s);                                  // add what has been encoded to the list
            pos = newpos;                               // store new input position
            out.rewind();                               // and rewind output buffer
            if (! cr.isOverflow()) {
                break;                                  // everything has been encoded
            }
        }
        return ss;
    }
}
