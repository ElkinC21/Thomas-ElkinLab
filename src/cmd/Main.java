package cmd;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {
    private JTextArea output;
    private JTextField input;
    private CMD console;

    public Main() {
        setTitle("Administrador: Command Prompt");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        console = new CMD();

        output = new JTextArea();
        output.setFont(new Font("Consolas", Font.PLAIN, 16));
        output.setBackground(Color.BLACK);
        output.setForeground(Color.WHITE);
        output.setEditable(false);

        input = new JTextField();
        input.setFont(new Font("Consolas", Font.PLAIN, 16));
        input.setBackground(Color.BLACK);
        input.setForeground(Color.WHITE);
        input.setCaretColor(Color.WHITE);

        JScrollPane scroll = new JScrollPane(output);
        add(scroll, BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

       
        output.append("Microsoft Windows [Version 10.0.22621.521]\n");
        output.append("(c) Microsoft Corporation. All rights reserved.\n\n");
        showPrompt();

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = input.getText();
                output.append(console.getPrompt() + command + "\n");
                String result = console.execute(command);
                if (!result.isEmpty()) {
                    output.append(result + "\n");
                }
                input.setText("");
                showPrompt();
            }
        });
    }

    private void showPrompt() {
        output.append(console.getPrompt());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
