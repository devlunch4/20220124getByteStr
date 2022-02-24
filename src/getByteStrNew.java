import java.util.ArrayList;
import java.util.List;

public class getByteStrNew {

    public static void main(String[] args) {
        String org = "미디엄은 쓰고, 읽고, 연결하는 장소입니다. 어떤 주제에든 쉽고 자유롭게 여러분의 생각을 올리고 수백만 명의 독자와 연결할 수 있습니다. ";
        List<String> rst_list = substringByBytes(org, 0, 80);
        System.out.println(rst_list);
    }

    public static List<String> substringByBytes(String str, int beginBytes, int endBytes) {
        List<String> list = new ArrayList<>();

        if (str == null || str.length() == 0) {
            return null;
        }
        if (beginBytes < 0) {
            beginBytes = 0;
        }
        if (endBytes < 1) {
            return null;
        }

        int len = str.length();
        int beginIndex = -1;
        int endIndex = 0;
        int curBytes = 0;
        String ch = null;

        for (int i = 0; i < len; i++) {
            ch = str.substring(i, i + 1);
            curBytes += ch.getBytes().length;
            if (beginIndex == -1 && curBytes >= beginBytes) {
                beginIndex = i;
            }
            if (curBytes > endBytes) {
                break;
            } else {
                endIndex = i + 1;
            }
            list.add(str.substring(beginIndex, endIndex));
        }
        return list;
    }
}
