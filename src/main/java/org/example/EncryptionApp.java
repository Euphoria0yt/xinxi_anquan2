package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EncryptionApp extends JFrame {

    private JTextField keyField;
    private JTextField plaintextField;
    private JTextField ciphertextField;

    public EncryptionApp() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("加密解密程序");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 280);
        getContentPane().setBackground(new Color(160, 144, 230)); // 浅蓝色背景

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        JLabel keyLabel = new JLabel("    密钥:");
        keyField = new JTextField();

        JLabel plaintextLabel = new JLabel("    明文:");
        plaintextField = new JTextField();

        JLabel ciphertextLabel = new JLabel("    密文:");
        ciphertextField = new JTextField();

        inputPanel.add(keyLabel);
        inputPanel.add(keyField);
        inputPanel.add(plaintextLabel);
        inputPanel.add(plaintextField);
        inputPanel.add(ciphertextLabel);
        inputPanel.add(ciphertextField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton encryptButton = new JButton("加密");
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keytext = keyField.getText();
                String plaintext = plaintextField.getText();
                boolean isstr = true;
                if(plaintext.length()==2) {
                    isstr =true;
                    String x = Integer.toBinaryString(plaintext.charAt(0));
                    plaintext = String.format("%16s", x).replace(' ', '0');
                } else isstr =false;   //判断是二进制还是ascill码,进行加密
                String ciphertext = S_AES.encrypt(plaintext,keytext,isstr);
                ciphertextField.setText(ciphertext);
            }
        });

        JButton decryptButton = new JButton("解密");
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keytext = keyField.getText();
                String ciphertext = ciphertextField.getText();
                boolean isstr = true;
                if(ciphertext.length()==2) {
                    isstr =true;
                    String x = Integer.toBinaryString(ciphertext.charAt(0));
                    ciphertext = String.format("%16s", x).replace(' ', '0');
                } else isstr =false;   //判断是二进制还是ascill码,进行解密.
                String plaintext = S_AES.decode(ciphertext,keytext,isstr);
                plaintextField.setText(plaintext);
            }
        });

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        getContentPane().add(inputPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EncryptionApp();
            }
        });
    }
}



