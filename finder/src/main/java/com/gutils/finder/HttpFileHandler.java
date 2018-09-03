/**
 *  Copyright (C) 2018 hyxf <1162584980@qq.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.gutils.finder;

import com.gutils.finder.codec.Base64;
import com.gutils.finder.utils.FileUtils;
import com.gutils.finder.utils.HtmlUtils;
import com.gutils.finder.utils.StringUtils;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * http file handler
 *
 * @author hyxf
 * @date 2018/7/21 am 11:14
 */
public class HttpFileHandler implements HttpRequestHandler {
    private static final int SPLIT_NUM = 2;
    private static final String SESS_UID = "uid";
    private final String fileRoot;
    private String username;
    private String password;
    private boolean needAuth;

    public HttpFileHandler(final String fileRoot, String user, String pass, boolean auth) {
        super();
        this.fileRoot = fileRoot;
        this.username = user;
        this.password = pass;
        this.needAuth = auth;
    }

    @Override
    public void handle(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {
        //----basic auth----
        boolean hasLogin = false;
        Header[] cookies = request.getHeaders("Cookie");
        if (needAuth && cookies != null && cookies.length > 0) {
            for (Header header : cookies) {
                String value = header.getValue();
                String[] temp = value.split(":");
                if (temp.length == 2) {
                    if (SESS_UID.equalsIgnoreCase(temp[0]) && temp[1].startsWith(this.username)) {
                        hasLogin = true;
                        break;
                    }
                }
            }
        }
        if (needAuth && !hasLogin) {
            boolean auth = false;
            Header header = request.getFirstHeader("authorization");
            if (header != null && StringUtils.isNotEmpty(header.getValue())) {
                String authorization = header.getValue();
                String[] temp = authorization.split(" ");
                if (temp.length == SPLIT_NUM) {
                    String base64String = temp[1];
                    String accountString = new String(Base64.decodeBase64(base64String), Charset.forName("UTF-8"));
                    String account[] = accountString.split(":");
                    if (account.length == SPLIT_NUM) {
                        auth = this.username.equalsIgnoreCase(account[0]) && this.password.equalsIgnoreCase(account[1]);
                        response.addHeader("Set-Cookie", String.format("%s:%s", SESS_UID, this.username));
                    }
                }
            }
            if (!auth) {
                response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
                response.addHeader("WWW-Authenticate", "Basic realm=\"STOP!\"");
                response.addHeader("Content-Type", "text/html");
                String html = HtmlUtils.getBaseHtml("No permissions");
                StringEntity entity = new StringEntity(html,
                        ContentType.create("text/html", "UTF-8"));
                response.setEntity(entity);
                return;
            }
        }
        //----
        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }
        String target = request.getRequestLine().getUri();

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            byte[] entityContent = EntityUtils.toByteArray(entity);
            System.out.println("Incoming entity content (bytes): " + entityContent.length);
        }
        String path = URLDecoder.decode(target, "UTF-8");
        final File file = new File(this.fileRoot, path);
        if (!file.exists()) {

            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            String html = HtmlUtils.getBaseHtml(String.format("%s not found", file.getPath()));
            StringEntity entity = new StringEntity(html,
                    ContentType.create("text/html", "UTF-8"));
            response.setEntity(entity);
            System.out.println("File " + file.getPath() + " not found");

        } else if (!file.canRead()) {

            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            String html = HtmlUtils.getBaseHtml("Access denied");
            StringEntity entity = new StringEntity(
                    html,
                    ContentType.create("text/html", "UTF-8"));
            response.setEntity(entity);
            System.out.println("Cannot read file " + file.getPath());

        } else if (file.isDirectory() && file.canRead()) {

            response.setStatusCode(HttpStatus.SC_OK);
            String html = buildHtml(path, file);
            StringEntity entity = new StringEntity(html,
                    ContentType.create("text/html", "UTF-8"));
            response.setEntity(entity);

        } else {
            HttpCoreContext coreContext = HttpCoreContext.adapt(context);
            HttpConnection conn = coreContext.getConnection(HttpConnection.class);
            response.setStatusCode(HttpStatus.SC_OK);
            FileEntity body = new FileEntity(file, HtmlUtils.parseContentType(file));
            response.setEntity(body);
            System.out.println(conn + ": serving file " + file.getPath());
        }
    }

    /**
     * build html
     *
     * @param filePath
     * @param dir
     * @return
     */
    private static String buildHtml(String filePath, File dir) {
        String html = HtmlUtils.getHtml("index.html");
        String cliName = MainCli.CMD;
        String path = filePath;
        //---

        //---
        StringBuilder sb = new StringBuilder();

        String rootPath = "/";
        if (!rootPath.equalsIgnoreCase(filePath)) {
            sb.append(String.format("<tr><td><a href=\"%s\" title=\"%s\">%s</a> </td><td>%s</td><td>%s</td></tr>",
                    "../", "<-back", "<-back", "-", "-")).append("\n");
        }

        File[] subFile = dir.listFiles();
        if (subFile != null) {
            for (File file : subFile) {
                String size = "-";
                String fileName;
                if (file.isFile()) {
                    size = FileUtils.convertFileSize(file.length());
                    fileName = file.getName();
                } else {
                    fileName = file.getName();
                    if (!fileName.endsWith(rootPath)) {
                        fileName = fileName + rootPath;
                    }
                }
                String fileTime = StringUtils.convertTime(file.lastModified());
                sb.append(String.format("<tr><td><a href=\"%s\" title=\"%s\">%s</a> </td><td>%s</td><td>%s</td></tr>",
                        fileName, fileName, fileName, fileTime, size)).append("\n");
            }
        }
        return html.replace("{cliName}", cliName).replace("{path}", path).replace("{content}", sb.toString());
    }

}
