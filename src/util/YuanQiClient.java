package util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 腾讯元器 OpenAPI 调用客户端
 * 文档参考：https://docs.qq.com/aio/p/sck8w384ntu41e5
 */
public class YuanQiClient {

    // ==================== 请修改为你的实际值 ====================
    private static final String APPKEY = "ACB9mGCc3sGJljLwQFoxi7rJjKpzUC5w";
    private static final String APPID  = "2060893739381619776";
    private static final String API_URL = "https://open.hunyuan.tencent.com/openapi/v1/agent/chat/completions";
    // =========================================================

    /**
     * 调用腾讯元器智能体，发送用户消息并返回回复内容
     * @param userMessage 用户输入的问题
     * @return 智能体的回复文本，失败返回 null
     */
    public static String chat(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "问题不能为空。";
        }

        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + APPKEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);

            // ========== 构建正确的请求体（按照 OpenAPI 规范） ==========
            JSONObject body = new JSONObject();
            body.put("assistant_id", APPID);
            body.put("stream", false);   // 非流式响应

            // 构建 messages 数组
            JSONArray messagesArray = new JSONArray();
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");

            // ⚠️ 关键：content 必须是数组，每个元素包含 type 和 text
            JSONArray contentArray = new JSONArray();
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", userMessage);
            contentArray.put(textContent);

            userMsg.put("content", contentArray);
            messagesArray.put(userMsg);
            body.put("messages", messagesArray);

            String jsonInput = body.toString();
            System.out.println("【调试】请求体: " + jsonInput);  // 可打印查看

            // 发送请求
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                // 读取成功响应
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject resp = new JSONObject(sb.toString());
                    // 解析回复内容（标准路径：choices[0].message.content）
                    JSONArray choices = resp.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject choice = choices.getJSONObject(0);
                        JSONObject message = choice.getJSONObject("message");
                        // 注意：返回的 content 可能也是数组或字符串，这里兼容处理
                        Object contentObj = message.get("content");
                        if (contentObj instanceof JSONArray) {
                            JSONArray contentArr = (JSONArray) contentObj;
                            if (contentArr.length() > 0) {
                                JSONObject first = contentArr.getJSONObject(0);
                                return first.optString("text", "");
                            }
                        } else if (contentObj instanceof String) {
                            return (String) contentObj;
                        }
                        return message.optString("content", "");
                    }
                    return "未获取到回答";
                }
            } else {
                // 读取错误信息
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    System.err.println("元器 API 错误 (" + code + "): " + sb);
                }
                return "调用失败，HTTP 状态码：" + code;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "发生异常：" + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}