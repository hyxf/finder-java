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

import com.gutils.finder.utils.StringUtils;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * http server
 *
 * @author hyxf
 */
public class MiniServer {
    private HttpServer mServer;
    private String ip;
    private int port;
    private String dir;
    private String user;
    private String pass;
    private boolean needAuth;

    private MiniServer(Builder builder) {
        ip = builder.ip;
        port = builder.port;
        dir = builder.dir;
        user = builder.user;
        pass = builder.pass;
        needAuth = StringUtils.isNotEmpty(this.user) && StringUtils.isNotEmpty(this.pass);
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        mServer = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("GUtils/1.1")
                .setSocketConfig(socketConfig)
                .setExceptionLogger(new StdErrorExceptionLogger())
                .registerHandler("*", new HttpFileHandler(this.dir, this.user, this.pass, this.needAuth))
                .create();
    }

    /**
     * stop server
     */
    public void stop() {
        try {
            mServer.shutdown(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            ///e.printStackTrace();
        }
    }

    /**
     * start server
     */
    public void start() throws IOException, InterruptedException {
        mServer.start();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Running at http://%s:%d/", ip, port));
        if (needAuth) {
            stringBuilder.append(String.format("\nUsername:%s , Password:%s", this.user, this.pass));
        }
        System.out.println(stringBuilder.toString());
        mServer.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("stop");
                MiniServer.this.stop();
            }
        });
    }

    /**
     * std logger
     */
    private static class StdErrorExceptionLogger implements ExceptionLogger {

        @Override
        public void log(final Exception ex) {
            if (ex instanceof SocketTimeoutException) {
                System.err.println("Connection timed out");
            } else if (ex instanceof ConnectionClosedException) {
                System.err.println(ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }

    }

    public static final class Builder {
        private String ip;
        private int port;
        private String dir;
        private String user;
        private String pass;

        public Builder() {
        }

        public Builder setIp(String val) {
            ip = val;
            return this;
        }

        public Builder setPort(int val) {
            port = val;
            return this;
        }

        public Builder setDir(String val) {
            dir = val;
            return this;
        }

        public Builder setUser(String val) {
            user = val;
            return this;
        }

        public Builder setPass(String val) {
            pass = val;
            return this;
        }

        public MiniServer build() {
            return new MiniServer(this);
        }
    }
}
