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
        if (input == null) input = "";
        input = input.trim();
        if (input.isEmpty()) return "";

        String[] parts = input.split("\\s+", 2);
        String cmd = parts[0].toLowerCase(); 
        String arg = (parts.length > 1) ? parts[1] : "";

        switch (cmd) {
            case "mkdir":     return mkdir(arg);
            case "mfile":     return mfile(arg);
            case "rm":        return rm(arg);
            case "cd":        return cd(arg);
            case "<...>":     return back();      
            case "dir":       return dir();
            case "date":      return date();
            case "time":      return time();
            case "leer":      return read(arg);     
           
            default:          return "Comando no valido.";
        }
    }

    private String mkdir(String name) {
        if (name.isBlank()) return "Comando no valido.";
        File newDir = new File(currentDir, name);
        return newDir.mkdir() ? "Carpeta creada: " + newDir.getName()
                              : "No se pudo crear.";
    }

    private String mfile(String name) {
        if (name.isBlank()) return "Comando no valido.";
        try {
            File f = new File(currentDir, name);
            return f.createNewFile() ? "Archivo creado: " + f.getName()
                                     : "El archivo ya existe.";
        } catch (IOException e) {
            return "Error al crear archivo: " + e.getMessage();
        }
    }

    private String rm(String name) {
        if (name.isBlank()) return "Comando no valido.";
        File target = new File(currentDir, name);
        if (!target.exists()) return "No existe.";
        return deleteRecursive(target) ? "Eliminado: " + name
                                       : "No se pudo eliminar.";
    }

    private boolean deleteRecursive(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) if (!deleteRecursive(c)) return false;
            }
        }
        return f.delete();
    }

    private String cd(String name) {
        if (name.isBlank()) return "Comando no valido.";
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
        return "Ya estás en la raiz.";
    }

    private String dir() {
        StringBuilder sb = new StringBuilder();
        File[] files = currentDir.listFiles();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append("\nDirectorio de: ").append(currentDir.getAbsolutePath()).append("\n\n");
        sb.append(String.format("%-20s  %-6s  %-10s  %s\n",
                "Ultima Modificacion", "Tipo", "Tamano", "Nombre"));
        sb.append("---------------------------------------------------------------\n");

        if (files != null) {
            for (File f : files) {
                String fecha = sdf.format(new Date(f.lastModified()));
                String tipo = f.isDirectory() ? "<DIR>" : "FILE";
                String tam  = f.isDirectory() ? "-" : String.valueOf(f.length());
                sb.append(String.format("%-20s  %-6s  %-10s  %s\n", fecha, tipo, tam, f.getName()));
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

    
    public String write(String filename, String text) {
        if (filename == null || filename.isBlank()) return "Comando no valido.";
        if (text == null) text = "";
        try (FileWriter fw = new FileWriter(new File(currentDir, filename), true)) {
            fw.write(text + System.lineSeparator());
            return "Texto escrito en " + filename;
        } catch (IOException e) {
            return "Error al escribir: " + e.getMessage();
        }
    }

    public String read(String filename) {
        if (filename == null || filename.isBlank()) return "Comando no valido.";
        File f = new File(currentDir, filename);
        if (!f.exists()) return "Archivo no encontrado.";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            return "Error al leer: " + e.getMessage();
        }
    }
}


