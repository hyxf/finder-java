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
package com.gutils.finder.utils;

import org.apache.http.entity.ContentType;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * html utils
 *
 * @author hyxf
 * @date 2018/7/21 am 11:10
 */
public class HtmlUtils {
    private static final String BASE_HTML = "base.html";

    /**
     * get html
     *
     * @param content
     * @return
     */
    public static String getBaseHtml(String content) {
        InputStream stream = null;
        try {
            String html = getHtml(BASE_HTML);
            return String.format(html, content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(stream);
        }
        return null;
    }

    /**
     * get html
     *
     * @param fileName
     * @return
     */
    public static String getHtml(String fileName) {
        InputStream stream = null;
        try {
            String html = FileUtils.resourceToString(fileName, Charset.forName("utf-8"), HtmlUtils.class.getClassLoader());
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(stream);
        }
        return null;
    }

    /**
     * parser content type
     *
     * @param file
     * @return
     */
    public static ContentType parseContentType(File file) {
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        ContentType contentType;
        switch (ext) {
            case "html":
                contentType = ContentType.create("text/html", "utf-8");
                break;
            case "js":
                contentType = ContentType.create("application/x-javascript", "utf-8");
                break;
            case "css":
                contentType = ContentType.create("text/xml", "utf-8");
                break;
            case "xml":
                contentType = ContentType.create("text/xml", "utf-8");
                break;
            case "json":
                contentType = ContentType.create("text/json", "utf-8");
                break;
            case "gif":
                contentType = ContentType.create("image/gif");
                break;
            case "jpeg":
                contentType = ContentType.create("image/jpeg");
                break;
            case "png":
                contentType = ContentType.create("image/png");
                break;
            case "webp":
                contentType = ContentType.create("image/webp");
                break;
            case "zip":
                contentType = ContentType.create("application/x-zip-compressed");
                break;
            case "pdf":
                contentType = ContentType.create("application/pdf");
                break;
            case "jar":
                contentType = ContentType.create("application/java-archive");
                break;
            case "apk":
                contentType = ContentType.create("application/vnd.android.package-archive");
                break;
            default:
                contentType = ContentType.create("application/octet-stream");
                break;
        }
        return contentType;
    }
}
