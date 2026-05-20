package com.neusoft.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// 声明一个公共的工具类 JsonFileUtil，用于简化 JSON 文件读写操作
public class JsonFileUtil {
    // 静态常量：创建一个 ObjectMapper 实例，用于 JSON 与 Java 对象之间的转换
    private static final ObjectMapper mapper = new ObjectMapper();
    // 静态常量：定义数据存储的根目录为当前工程下的 "data/" 文件夹
    private static final String DATA_DIR = "data/";
    // 静态初始化块，在类加载时执行一次
    static {
        // 根据 DATA_DIR 路径创建一个 File 对象，表示 data 目录
        File dir = new File(DATA_DIR);
        // 判断该目录是否已经存在
        if (!dir.exists()) {
            // 如果不存在，则创建该目录（包括所有必需的父目录）
            dir.mkdirs();
        }
    }

    // 泛型方法：从指定文件名的 JSON 文件中读取数据，并返回一个 List<T> 集合
    public static <T> List<T> readList(String fileName, Class<T> clazz) {
        // 拼接出完整路径，创建 File 对象表示目标 JSON 文件
        File file = new File(DATA_DIR + fileName);
        // 如果文件不存在
        if (!file.exists()) {
            // 直接返回一个空的 ArrayList，避免读取异常
            return new ArrayList<>();
        }
        try { // 尝试执行可能抛出 IOException 的代码
            // 构造一个集合类型描述：表示 ArrayList<T> 类型，其中元素类型为 clazz
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            // 使用 ObjectMapper 从文件中读取 JSON 内容，并转换为 listType 对应的 Java 集合对象
            return mapper.readValue(file, listType);
        } catch (IOException e) { // 捕获 IO 异常（如文件格式错误、读取失败等）
            e.printStackTrace(); // 打印异常堆栈信息，便于调试
            return new ArrayList<>(); // 发生异常时返回空列表，保证方法不会返回 null
        }
    }

    // 泛型方法：将给定的 List<T> 集合数据以 JSON 格式写入到指定文件名的文件中
    public static <T> void writeList(String fileName, List<T> list) {
        // 拼接完整路径，创建 File 对象表示目标 JSON 文件
        File file = new File(DATA_DIR + fileName);
        try { // 尝试执行可能抛出 IOException 的写入操作
            // 使用默认的漂亮打印格式（带缩进和换行），将 list 对象序列化为 JSON 并写入文件
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
        } catch (IOException e) { // 捕获写入时的异常（如磁盘满、权限不足等）
            e.printStackTrace(); // 打印异常堆栈信息，便于定位问题
        }
    }
}