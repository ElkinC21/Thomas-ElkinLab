package cmdte;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main extends JFrame {

    private final JTextArea area = new JTextArea();
    private final CMD console = new CMD();
    private int promptPos;

    
    private boolean waitingWrite = false;
    private String pendingWriteFile = null;

    public Main() {
        setTitle("Administrador: Command Prompt");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        area.setFont(new Font("Consolas", Font.PLAIN, 16));
        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setLineWrap(false);

        add(new JScrollPane(area), BorderLayout.CENTER);

       
        println("Microsoft Windows [Version 10.0.22621.521]");
        println("(c) Microsoft Corporation. All rights reserved.");
        println("");
        printPrompt();

       
        ((AbstractDocument) area.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override public void remove(DocumentFilter.FilterBypass fb, int off, int len) throws BadLocationException {
                if (off < promptPos) return; super.remove(fb, off, len); }
            @Override public void replace(DocumentFilter.FilterBypass fb, int off, int len, String t, AttributeSet a)
                    throws BadLocationException {
                if (off < promptPos) return; super.replace(fb, off, len, t, a); }
            @Override public void insertString(DocumentFilter.FilterBypass fb, int off, String s, AttributeSet a)
                    throws BadLocationException {
                if (off < promptPos) { area.setCaretPosition(area.getDocument().getLength()); return; }
                super.insertString(fb, off, s, a); }
        });

      
        area.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_HOME) {
                    area.setCaretPosition(promptPos);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    runCommand();
                }
            }
        });

        SwingUtilities.invokeLater(() -> area.requestFocusInWindow());
    }

    private void println(String s) { area.append(s + "\n"); }

    private void printPrompt() {
        area.append(console.getPrompt());
        promptPos = area.getDocument().getLength();
        area.setCaretPosition(promptPos);
    }

    private void runCommand() {
        try {
            Document doc = area.getDocument();
            String line = doc.getText(promptPos, doc.getLength() - promptPos).trim();
            area.append("\n");
            if (line.isEmpty()) { printPrompt(); return; }

            
            if (waitingWrite) {
                String text = line;
                String out = console.escribir(pendingWriteFile, text);
                if (!out.isEmpty()) println(out);
                waitingWrite = false;
                pendingWriteFile = null;
                printPrompt();
                return;
            }

            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String arg = (parts.length > 1) ? parts[1] : "";

            if ("escribir".equals(cmd)) { 
                if (arg.isBlank()) {
                    println("Comando no valido.");
                } else {
                    int sp = arg.indexOf(' ');
                    if (sp >= 0) {
                        
                        String file = arg.substring(0, sp).trim();
                        String text = arg.substring(sp + 1);
                        String out = console.escribir(file, text);
                        if (!out.isEmpty()) println(out);
                    } else {
                        
                        pendingWriteFile = arg;
                        waitingWrite = true;
                        println("Escriba el contenido y presione Enter:");
                    }
                }
            } else {
                String out = console.ejecutar(line);
                if (!out.isEmpty()) println(out);
            }

            printPrompt();
        } catch (BadLocationException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

}



