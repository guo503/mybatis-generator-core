package org.mybatis.generator.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Description: 文件工具类
 * Author: guos
 * Date: 2019/2/18 9:27
 **/

public class LocalFileUtils {

    public static void readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void writeFile(File file, String content) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        osw.write(content);
        osw.flush();
    }

    public static void appendFile(File file, String content) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(
                new FileOutputStream(file, true), // true to append
                "UTF-8"
        );
        out.write(content);
        out.close();
    }


    /**
     * @param filePath
     * @param content
     * @param targetLineNum:要写入位置的行号
     * @throws Exception
     */
    public static void randomAccessAppend(String filePath, String content, int targetLineNum) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        int currentLineNum = 0;
        if (currentLineNum > targetLineNum) {
            return;
        }
        while (raf.readLine() != null) {
            if (currentLineNum == targetLineNum) { // 定位到目标行时结束
                break;
            }
            currentLineNum++;
        }
        raf.write(("\r\n" + (content)).getBytes());
        raf.close();
    }

    public static void insert(String fileName, long pos, String content) throws IOException {
        //创建临时空文件
        File tempFile = File.createTempFile("temp", null);
        //在虚拟机终止时，请求删除此抽象路径名表示的文件或目录
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);

        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.seek(pos);
        byte[] buffer = new byte[4];
        int num;
        while (-1 != (num = raf.read(buffer))) {
            fos.write(buffer, 0, num);
        }
        raf.seek(pos);
        raf.write(content.getBytes());
        FileInputStream fis = new FileInputStream(tempFile);
        while (-1 != (num = fis.read(buffer))) {
            raf.write(buffer, 0, num);
        }
    }


    /**
     * 修改匹配到的行内容
     *
     * @param filePath:文件路径
     * @param oldStr:       被替换的字符串
     * @param newStr:       替换的字符串
     */
    public static void modifyLine(String filePath, String oldStr, String newStr) {
        try {
            Pattern pattern = Pattern.compile(oldStr, Pattern.CASE_INSENSITIVE); // 要匹配的字段内容，正则表达式
            Matcher matcher = pattern.matcher("");
            List<String> lines = Files.readAllLines(Paths.get(filePath)); // 读取文本文件
            if (lines.size() == 0) {
                return;
            }
            int size = lines.size();
            if ("}".equals(oldStr)) {
                //如果是},去最后一个
                matcher.reset(lines.get(size - 1));
                if (matcher.find()) { // 匹配正则表达式
                    lines.remove(size - 1);
                    lines.add(size - 1, newStr);
                }
            } else if (oldStr.contains("package")) {
                matcher.reset(lines.get(0));
                if (matcher.find()) { // 匹配正则表达式
                    lines.remove(0);
                    lines.add(0, newStr);
                }
            } else {
                for (int i = 0; i < size; i++) {
                    matcher.reset(lines.get(i));
                    if (matcher.find()) { // 匹配正则表达式
                        lines.remove(i);
                        lines.add(i, newStr);
                    }
                }
            }
            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查找字符串
     *
     * @param filePath:文件路径
     * @param queryStr:     查找内容字符串
     */
    public static boolean findStr(String filePath, String queryStr) {
        try {
            File file = new File(filePath);
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                //指定字符串判断处
                if (line.contains(queryStr)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getPath(String packagePath) {
        return File.separator + packagePath.replace(".", File.separator);
    }

    public static void main(String[] args) throws Exception {
        String filePath = "d:/test.Java";
        LocalFileUtils.modifyLine(filePath, "package", "package com.tsyj.test;\n\n\nimport java.util.*;");
        LocalFileUtils.modifyLine(filePath, "}", "\tappend xxx\n}");
    }
}