package view.panel;

import util.YuanQiClient;
import view.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AiAssistantPanel extends JPanel {
    private JTextArea chatArea;       // 显示对话记录
    private JTextField inputField;    // 输入框
    private JButton sendButton;

    public AiAssistantPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 聊天记录区
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("AI 智能助手 - 银巢智护"));
        add(scrollPane, BorderLayout.CENTER);

        // 底部输入区
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        inputField = new JTextField();
        inputField.addActionListener(this::sendMessage);
        sendButton = new JButton("发送");
        sendButton.addActionListener(this::sendMessage);

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // 欢迎语
        appendMessage("银巢智护", "您好！我是东软颐养中心的 AI 助手，可以帮您查询床位信息、护理项目、合同到期提醒等。请问有什么可以帮您？");
    }

    private void sendMessage(ActionEvent e) {
        String question = inputField.getText().trim();
        if (question.isEmpty()) {
            return;
        }
        // 显示用户问题
        appendMessage("我", question);
        inputField.setText("");
        sendButton.setEnabled(false);

        // 在后台线程中调用 API，避免阻塞 UI
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return YuanQiClient.chat(question);
            }

            @Override
            protected void done() {
                try {
                    String answer = get();
                    if (answer == null || answer.isEmpty()) {
                        answer = "抱歉，暂时无法获取回答，请稍后再试。";
                    }
                    appendMessage("银巢智护", answer);
                } catch (Exception ex) {
                    appendMessage("银巢智护", "发生错误：" + ex.getMessage());
                } finally {
                    sendButton.setEnabled(true);
                    inputField.requestFocus();
                }
            }
        }.execute();
    }

    private void appendMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(sender + "：\n" + message + "\n\n");
            // 自动滚动到底部
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}