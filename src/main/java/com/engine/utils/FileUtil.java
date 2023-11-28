package com.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 根据文件路径读取文件内容
     *
     * @param fileName
     * @throws IOException
     */
    public static String readFileContent(String fileName) throws IOException {
        FileReader fileReader = null;
        Reader reader = null;
        try {
            File jsonFile = ResourceUtils.getFile("classpath:" + fileName);
            fileReader = new FileReader(jsonFile);
            reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取文件报错", e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
