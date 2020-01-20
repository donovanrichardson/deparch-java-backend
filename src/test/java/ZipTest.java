
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.fail;

public class ZipTest {

    @Test
    public void dropTest(){
        Map<String, InputStream> result = new HashMap<>();
//        File f = new File("muni2.zip");
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(f);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            fail();
//        }
        Connection c = Jsoup.connect("https://transitfeeds.com/p/sfmta/60/20191211/download");
//        InputStream onemb = c.ignoreContentType(true).execute().bodyStream();
        Connection notEnoughBytes = c.ignoreContentType(true);
        Connection.Request requestSize = notEnoughBytes.request();
        requestSize.maxBodySize(1073741824);
        ZipInputStream verZip = null;
        try {
            Connection.Response almostZipStream = notEnoughBytes.request(requestSize).execute();
            verZip = new ZipInputStream(
                    almostZipStream.bodyStream());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
//        ZipInputStream verZip = new ZipInputStream(fis);
        while (true) {
            ZipEntry entry;
            byte[] b = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int l;

            try {
                entry = verZip.getNextEntry();
            } catch (IOException e) {
                break;
            }

            if (entry == null) {
                break;
            }

            try {
                while ((l = verZip.read(b)) > 0) {
                    out.write(b, 0, l);
                }
                out.flush();
            }catch(EOFException e){
                e.printStackTrace();
            }
            catch (IOException i) {
                System.out.println("there was an ioexception");
                i.printStackTrace();
                fail();
            }
            result.put(entry.getName(), new ByteArrayInputStream(out.toByteArray()));
        }
        try{
            verZip.close();
        } catch (IOException i){
            i.printStackTrace();
            fail();
        }

        for(String k : result.keySet()){
            System.out.println(k);
        }

    }

}
