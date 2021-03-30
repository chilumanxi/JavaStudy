import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class DoGET {
    public static void main(String args[]) throws Exception{
//        创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
//      创建Http GET请求
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8808/test");

        CloseableHttpResponse response = null;

        try{
            response = httpClient.execute(httpGet);

            if(response.getStatusLine().getStatusCode() == 200){
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");

                System.out.println(content);
            }
        }finally {
            if(response != null){
                response.close();
            }
        }
        httpClient.close();
    }
}
