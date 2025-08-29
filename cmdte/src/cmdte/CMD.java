package cmdte;

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

    public String ejecutar(String input) {
        if (input == null) input = "";
        input = input.trim();
        if (input.isEmpty()) return "";

        String[] parts = input.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = (parts.length > 1) ? parts[1] : "";

        switch (cmd) {
            case "mkdir": return mkdir(arg);
            case "mfile": return mfile(arg);
            case "rm":    return rm(arg);
            case "cd":    return cd(arg);
            case "..":    return regresar();
            case "dir":   return dir();
            case "date":  return date();
            case "time":  return time();
            case "leer":  return leer(arg);
            default:      return "Comando no valido";
        }
    }

    private String mkdir(String name) {
        if (name.isBlank()) return "Comando no valido.";
        File newDir = new File(currentDir, name);
        if (newDir.exists()) {
            return "La carpeta \"" + newDir.getName() + "\" ya existe";
        }
        return newDir.mkdir() ? "Carpeta creada: " + newDir.getName()
                              : "No se pudo crear la carpeta.";
    }

    private String mfile(String name) {
        if (name.isBlank()) return "Comando no valido";
        try {
            File f = new File(currentDir, name);
            return f.createNewFile() ? "Archivo creado: " + f.getName() : "El archivo ya existe.";
        } catch (IOException e) {
            return "Error al crear archivo: " + e.getMessage();
        }
    }

    private String rm(String name) {
        if (name.isBlank()) return "Comando no valido";
        File target = new File(currentDir, name);
        if (!target.exists()) return "No existe";
        return delete(target) ? "Eliminado: " + name : "No se pudo eliminar";
    }

    private boolean delete(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) if (!delete(c)) return false;
            }
        }
        return f.delete();
    }

    private String cd(String name) {
        if (name.isBlank()) return "Comando no valido";
        File newDir = new File(name);
        if (!newDir.isAbsolute()) newDir = new File(currentDir, name);
        if (newDir.exists() && newDir.isDirectory()) {
            currentDir = newDir;
            return "";
        }
        return "Directorio no encontrado";
    }

    private String regresar() {
        File parent = currentDir.getParentFile();
        if (parent != null) {
            currentDir = parent;
            return "";
        }
        return "Ya estas en la raiz";
    }

   
    private String dir() {
        File[] files = currentDir.listFiles();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String cabecera  = "\nDirectorio de: " + currentDir.getAbsolutePath() + "\n\n";
        cabecera       += "Ultima Modificacion     " + "Tipo   " + "Tamano        " + "Nombre\n";
        cabecera       += "---------------------------------------------------------------------\n";

        String resultado = cabecera;

        if (files != null) {
            for (File f : files) {
                String fecha  = sdf.format(new Date(f.lastModified())); 
                String tipo   = f.isDirectory() ? "<DIR>" : "FILE";
                String tam    = f.isDirectory() ? "-" : formatSize(f.length()); 
                String nombre = f.getName();

                
                String linea = pad(fecha, 22) + pad(tipo, 6) + pad(tam, 14) + nombre + "\n";
                resultado = resultado + linea;
            }
        }
        return resultado;
    }

    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double v = bytes;
        String[] u = {"KB","MB","GB","TB","PB"};
        int i = 0;
        v = v / 1024.0; // KB
        while (v >= 1024.0 && i < u.length - 1) { v = v / 1024.0; i++; }
        int t = (int) Math.round(v * 10); 
        int w = t / 10;
        int d = t % 10;
        String num = (d == 0) ? ("" + w) : (w + "." + d);
        return num + " " + u[i];
    }

  
    private String pad(String s, int width) {
        if (s == null) s = "";
        int faltan = width - s.length();
        if (faltan <= 0) return s;
        String espacios = "";
        for (int i = 0; i < faltan; i++) espacios = espacios + " ";
        return s + espacios;
    }

    private String date() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private String time() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    
    public String escribir(String filename, String text) {
        if (filename == null || filename.isBlank()) return "Comando no valido";
        if (text == null) text = "";
        try (FileWriter fw = new FileWriter(new File(currentDir, filename), true)) {
            fw.write(text + "\n");
            return "Texto escrito en " + filename;
        } catch (IOException e) {
            return "Error al escribir: " + e.getMessage();
        }
    }

    public String leer(String filename) {
        if (filename == null || filename.isBlank()) return "Comando no valido";
        File f = new File(currentDir, filename);
        if (!f.exists()) return "Archivo no encontrado.";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String contenido = "";
            String line;
            while ((line = br.readLine()) != null) {
                contenido = contenido + line + "\n";
            }
            return contenido;
        } catch (IOException e) {
            return "Error al leer: " + e.getMessage();
        }
    }
}
