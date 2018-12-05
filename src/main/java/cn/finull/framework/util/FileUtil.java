package cn.finull.framework.util;

import java.io.*;

public final class FileUtil {

    /**
     * 写一个临时文件
     * @param in 输入流
     * @param fileName 文件名
     * @return 临时文件
     * @throws IOException
     */
    public static File writerTempFile(InputStream in,String fileName) throws IOException {
        int index = fileName.lastIndexOf(".");
        String prefix = fileName.substring(0,index);
        String suffix = fileName.substring(index);
        // 创建临时文件
        File file = File.createTempFile(prefix,suffix);
        try (
                InputStream reader = new BufferedInputStream(in);
                OutputStream writer = new BufferedOutputStream(
                        new FileOutputStream(file)
                )
        ) {
            byte[] bs = new byte[1024];
            int len = -1;
            while ((len = reader.read(bs)) != -1) {
                writer.write(bs,0,len);
            }
            // 程序退出时删除文件
            file.deleteOnExit();
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
        return file;
    }
}
