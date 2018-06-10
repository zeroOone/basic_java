package Utils;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class HttpUtils {
    /**
     * MethodName : sendGet
     *
     * @Description : http get请求 公用方法
     * @param reqUrl
     * @param params
     * @return
     */
    public static String sendGet(String reqUrl, Map<String, String> params) {
        InputStream inputStream = null;

        HttpGet request = new HttpGet();
        String result = null;
        try {
            String url = buildUrl(reqUrl, params);
            CloseableHttpClient httpClient = HttpClients.createDefault();

            request.setHeader("Accept-Encoding", "gzip");
            request.setURI(new URI(url));

            HttpResponse response = httpClient.execute(request);

            inputStream = response.getEntity().getContent();
            result = getJsonStringFromGZIP(inputStream);
        } catch (URISyntaxException u) {
            System.out.println("BuildUrl : BAD URL !");
        } catch (ClientProtocolException c) {
            System.out.println("Execute Request : BAD REQUEST !");
        } catch (IOException i) {
            System.out.println("Execute Request Or Get Response : BAD I/O OPERATION !");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.out.println("InputStream : BAD I/O OPERATION !");
            }

        }
        request.releaseConnection();
        return result;

    }

    /**
     * MethodName : sendPost
     *
     * @Description : http post 请求 公用方法
     * @param reqUrl
     * @param params
     * @return
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static String sendPost(String reqUrl, Map<String, String> params) throws Exception {
        try {
            Set<String> set = params.keySet();
            List<NameValuePair> list = new ArrayList<>();
            for (String key : set) {
                list.add(new BasicNameValuePair(key, params.get(key)));
            }

            if (list.size() > 0) {
                try {
                    CloseableHttpClient client = HttpClients.createDefault();
                    HttpPost request = new HttpPost(reqUrl);

                    request.setHeader("Accept-Encoding", "gzip");
                    request.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));

                    HttpResponse response = client.execute(request);

                    InputStream inputStream = response.getEntity().getContent();
                    try {
                        String result = getJsonStringFromGZIP(inputStream);
                        return result;
                    } finally {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    throw new Exception("网络连接失败 ！");
                }
            } else {
                throw new Exception("参数配置出错，请检查!");
            }
        } catch (Exception e) {
            throw new Exception("发送未知异常!");
        }
    }

    /**
     * MethodName : sendPostBuffer
     *
     * @Description : http post 请求 json数据
     * @param Urls
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPostBuffer(String Urls, String params) throws ClientProtocolException, IOException {
        HttpPost request = new HttpPost(Urls);

        StringEntity se = new StringEntity(params, Consts.UTF_8);
        request.setEntity(se);
        //发送请求
        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse httpResponse = client.execute(request);
        //得到应答(Json)
        String result = EntityUtils.toString(httpResponse.getEntity());
        request.releaseConnection();
        return result;
    }

    /**
     * MethodName : sendXmlPost
     *
     * @Descrition : http 请求发送xml内容
     * @param urlStr
     * @param xmlInfo
     * @return
     */
    public static String sendXmlPost(String urlStr, String xmlInfo) {
        // xmlInfo xml具体字符串

        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();

            con.setDoOutput(true);
            con.setRequestProperty("Pragma", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(new String(xmlInfo.getBytes("utf-8")));
            out.flush();
            out.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String lines = null;
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines = lines + line;
            }

            //返回请求结果
            return lines;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  "fail";
    }

    private static String getJsonStringFromGZIP(InputStream is) {
        String jsonString = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(2);
            //取前两个字节
            byte[] header = new byte[2];
            int result = bis.read(header);
            //reset输入流到开始位置
            bis.reset();
            //判断是否是GZIP格式
            int headerDate = getShort(header);
            //Gzip流的前两个字节为 0x1f8b
            if (result != -1 && headerDate == 0x1f8b) {
                is = new GZIPInputStream(bis);
            } else {
                is = bis;
            }

            InputStreamReader reader = new InputStreamReader(is, Consts.UTF_8);
            char[] data = new char[100];
            int readSize;
            StringBuffer sb = new StringBuffer();
            while ((readSize = reader.read(data)) > 0) {
                sb.append(data, 0, readSize);
            }
            jsonString = sb.toString();
            bis.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    private static int getShort(byte[] data) {
        return (data[0] << 8) | data[1] & 0xFF;
    }

    public static String buildUrl(String reqUrl, Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        Set<String> set = params.keySet();
        for (String key : set) {
            query.append(String.format("%s=%s&", key, params.get(key)));
        }
        return reqUrl + "?" + query.toString();
    }
}
