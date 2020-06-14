package com.rawa.cloud.helper;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HttpHelper {

    public static File upload(String url, String name, File file) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build();
            httppost.setConfig(requestConfig);

            FileBody bin = new FileBody(file);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart(name, bin)
                    .setContentType(ContentType.MULTIPART_FORM_DATA)
                    .build();

            httppost.setEntity(reqEntity);

            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                File ret = File.createTempFile(file.getName(), ".pdf");
                ret.deleteOnExit();
                FileOutputStream out = new FileOutputStream(ret);
                resEntity.writeTo(out);
//                InputStream in = resEntity.getContent();
//                byte[] buffer = new byte[8 * 1024];
//                int bytesRead;
//                while ((bytesRead = in.read(buffer)) != -1) {
//                    out.write(buffer, 0, bytesRead);
//                }
                out.flush();
                out.close();
//                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return ret;
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                httpclient.close();
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        File source = new File("/Users/zhangyin/IdeaProjects/rawa-cloud-app/office/3.docx");
        File file = upload("http://localhost:3000/convert/office", "files", source);
        System.out.println(file.getPath());
    }
}
