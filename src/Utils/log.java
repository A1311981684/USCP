package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class log {
    public static void Println(String msg) {
//        Calendar now = Calendar.getInstance();
//        String year = Integer.toString(now.get(Calendar.YEAR));
//        String month = Integer.toString((now.get(Calendar.MONTH) + 1));
//        String day = Integer.toString(now.get(Calendar.DAY_OF_MONTH) + 1);
//        String hour = Integer.toString(now.get(Calendar.HOUR_OF_DAY));
//        String minute = Integer.toString(now.get(Calendar.MINUTE));
//        String Second = Integer.toString( now.get(Calendar.SECOND));
        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(d);
        System.out.printf("[%s  %s]: %s\r\n", time, Thread.currentThread().getStackTrace()[2], msg);
    }
}
