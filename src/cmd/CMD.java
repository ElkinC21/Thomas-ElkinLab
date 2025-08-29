package cmd;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CMD {
    private File currentDir;

    public CMD() {
        currentDir = new File(System.getProperty("user.dir"));
    }

    public String getPrompt() {
        return currentDir.getAbsolutePath() + ">";
    }

    public String execute(String input) {
        if (input.trim().isEmpty()) return "";

        String[] parts = input.split(" ", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "mkdir": return mkdir(arg);
            case "mfile": return mfile(arg);
            case "rm": return rm(arg);
            case "cd": return cd(arg);
            case "<..>": return back();
            case "dir": return dir();
            case "date": return date();
            case "time": return time();
            case "wr": return write(arg);
            case "rd": return read(arg);
            default: return "Comando no reconocido.";
        }
    }

    private String mkdir(String name) {
        if (name.isBlank()) return "Uso: mkdir <nombre>";
        File newDir = new File(currentDir, name);
        return newDir.mkdir() ? "Carpeta creada: " + newDir.getName() : "No se pudo crear.";
    }

    private String mfile(String name) {
        if (name.isBlank()) return "Uso: mfile <nombre.ext>";
        try {
            File f = new File(currentDir, name);
            return f.createNewFile() ? "Archivo creado: " + f.getName() : "Ya existe.";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String rm(String name) {
        if (name.isBlank()) return "Uso: rm <nombre>";
        File target = new File(currentDir, name);
        if (!target.exists()) return "No existe.";
        return deleteRecursive(target) ? "Eliminado: " + name : "No se pudo eliminar.";
    }

    private boolean deleteRecursive(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) deleteRecursive(c);
        }
        return f.delete();
    }

    private String cd(String name) {
        if (name.isBlank()) return "Uso: cd <carpeta>";
        File newDir = new File(name);
        if (!newDir.isAbsolute()) newDir = new File(currentDir, name);
        if (newDir.exists() && newDir.isDirectory()) {
            currentDir = newDir;
            return "";
        }
        return "Directorio no encontrado.";
    }

    private String back() {
        File parent = currentDir.getParentFile();
        if (parent != null) {
            currentDir = parent;
            return "";
        }
        return "Ya estás en raíz.";
    }

    private String dir() {
        StringBuilder sb = new StringBuilder();
        File[] files = currentDir.listFiles();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append("\nDirectorio de: ").append(currentDir.getAbsolutePath()).append("\n\n");
        sb.append("Ultima Modificación      Tipo     Tamaño     Nombre\n");
        sb.append("------------------------------------------------------\n");

        if (files != null) {
            for (File f : files) {
                String fecha = sdf.format(new Date(f.lastModified()));
                String tipo = f.isDirectory() ? "<DIR>" : "FILE";
                String tam = f.isDirectory() ? "-" : String.valueOf(f.length());
                sb.append(String.format("%-22s %-8s %-10s %s\n", fecha, tipo, tam, f.getName()));
            }
        }
        return sb.toString();
    }

    private String date() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private String time() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private String write(String filename) {
        if (filename.isBlank()) return "Uso: wr <archivo>";
        try (FileWriter fw = new FileWriter(new File(currentDir, filename), true)) {
            fw.write("Texto de ejemplo\n");
            return "Texto escrito en " + filename;
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String read(String filename) {
        if (filename.isBlank()) return "Uso: rd <archivo>";
        File f = new File(currentDir, filename);
        if (!f.exists()) return "Archivo no encontrado.";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
