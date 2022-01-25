import java.util.ArrayList;
import java.util.List;

public class getByteStr {

    public static void main(String[] args) {
        String org = "+선하증권은 로드 포트 및 배출 포트의 이름";
        List<String> rst_lsit = substringByBytes(org, 0, 80);
        System.out.println(rst_lsit);

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
