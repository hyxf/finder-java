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

import com.gutils.finder.utils.NetUtils;
import com.gutils.finder.utils.StringUtils;
import org.apache.commons.cli.*;

/**
 * main cli
 *
 * @author hyxf
 * @date 2018/7/20 pm 9:54
 */
public class MainCli {
    public static final String CMD = "finder";
    private static final int DEFAULT_PORT = 8087;
    private static final String ARG_IP = "ip";
    private static final String ARG_PORT = "port";
    private static final String ARG_DIR = "dir";
    private static final String ARG_HELP = "help";
    private static final String ARG_USER = "user";
    private static final String ARG_PASS = "pass";

    public static void main(String[] args) {
        run(args);
    }

    private static void run(String[] args) {
        Options options = new Options();
        options.addOption("i", ARG_IP, true, "Local IP");
        options.addOption("p", ARG_PORT, true, "port");
        options.addRequiredOption("d", ARG_DIR, true, "Shared directory path");
        options.addOption("u", ARG_USER, true, "username");
        options.addOption("s", ARG_PASS, true, "password");
        options.addOption("h", ARG_HELP, true, "help");
        //-----
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String dir = cmd.getOptionValue(ARG_DIR);

            String ip = cmd.getOptionValue(ARG_IP);
            if (StringUtils.isEmpty(ip)) {
                ip = NetUtils.getIP();
            }
            int port = DEFAULT_PORT;
            if (cmd.hasOption(ARG_PORT)) {
                port = Integer.parseInt(cmd.getOptionValue(ARG_PORT));
            }

            String user = cmd.getOptionValue(ARG_USER);
            String pass = cmd.getOptionValue(ARG_PASS);
            MiniServer.Builder builder = new MiniServer.Builder();
            builder.setDir(dir)
                    .setIp(ip)
                    .setPort(port)
                    .setUser(user)
                    .setPass(pass);
            MiniServer server = builder.build();
            server.start();
        } catch (Exception e) {
            hf.printHelp(CMD, "\nLAN file Sharing tool\n\n", options, "\nmake it easy!", true);
        }
    }
}
