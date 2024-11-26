// C:\Users\ALLMIGHTY\Desktop\AccessoDatos\AccessoDatosProductosHibernate\src\main\java\org\zakaria\Main.java
package org.zakaria;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.zakaria.entity.Appliance;
import org.zakaria.utils.HibernateUtil;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        int choice = -1;
        while (choice != 0) {
            choice = showMenu();
            switch (choice) {
                case 1:
                    insertarNuevoAppliance();
                    break;
                case 2:
                    actualizarPrecioAppliance();
                    break;
                case 3:
                    listarTodosAppliances();
                    break;
                case 4:
                    listarAppliancesPorMarca("Bosch");
                    break;
                case 5:
                    listarAppliancesPorRangoPrecio(100, 500);
                    break;
                case 6:
                    importarDesdeArrays();
                    break;
                case 7:
                    exportarDatos();
                    break;
                case 8:
                    leerDesdeCSV("appliances.csv");
                    break;
                case 9:
                    leerDesdeJSON("appliances.json");
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Saliendo de la aplicación...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida. Por favor, intenta de nuevo.");
            }
        }

        // Cerrar la fábrica de sesiones de Hibernate antes de salir
        HibernateUtil.shutdown();
    }

    /**
     * Muestra el menú de opciones al usuario y devuelve la elección.
     */
    private static int showMenu() {
        String[] options = {
                "1. Insertar un nuevo electrodoméstico",
                "2. Actualizar el precio de un electrodoméstico",
                "3. Listar todos los electrodomésticos",
                "4. Listar electrodomésticos de la marca Bosch",
                "5. Listar electrodomésticos con precio entre 100 y 500 €",
                "6. Importar electrodomésticos desde arrays (Ejercicio 3)",
                "7. Exportar electrodomésticos a CSV y JSON",
                "8. Leer electrodomésticos desde CSV",
                "9. Leer electrodomésticos desde JSON",
                "0. Salir"
        };

        String input = (String) JOptionPane.showInputDialog(
                null,
                "Selecciona una opción:",
                "Menú de Electrodomésticos",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (input == null) {
            // El usuario cerró el diálogo o pulsó cancelar
            return 0;
        }

        try {
            return Integer.parseInt(input.substring(0, 1));
        } catch (Exception e) {
            return -1; // Opción inválida
        }
    }

    /**
     * Inserta un nuevo electrodoméstico en la base de datos.
     */
    private static void insertarNuevoAppliance() {
        try {
            String modelo = JOptionPane.showInputDialog("Introduce el modelo del electrodoméstico:");
            if (modelo == null || modelo.trim().isEmpty()) {
                mostrarMensaje("Modelo no puede estar vacío.");
                return;
            }

            String marca = JOptionPane.showInputDialog("Introduce la marca del electrodoméstico:");
            if (marca == null || marca.trim().isEmpty()) {
                mostrarMensaje("Marca no puede estar vacía.");
                return;
            }

            String tipo = JOptionPane.showInputDialog("Introduce el tipo del electrodoméstico:");
            if (tipo == null || tipo.trim().isEmpty()) {
                mostrarMensaje("Tipo no puede estar vacío.");
                return;
            }

            String precioStr = JOptionPane.showInputDialog("Introduce el precio del electrodoméstico:");
            if (precioStr == null || precioStr.trim().isEmpty()) {
                mostrarMensaje("Precio no puede estar vacío.");
                return;
            }

            double precio = Double.parseDouble(precioStr);

            Appliance appliance = new Appliance(modelo, marca, tipo, precio);
            boolean done = saveAppliance(appliance);

            if (done) {
                mostrarMensaje("Electrodoméstico guardado correctamente.");
            } else {
                mostrarMensaje("Error al guardar el electrodoméstico.");
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Precio inválido. Por favor, introduce un número válido.");
        }
    }

    /**
     * Actualiza el precio de un electrodoméstico basado en su ID.
     */
    private static void actualizarPrecioAppliance() {
        try {
            String idStr = JOptionPane.showInputDialog("Introduce el ID del electrodoméstico a actualizar:");
            if (idStr == null || idStr.trim().isEmpty()) {
                mostrarMensaje("ID no puede estar vacío.");
                return;
            }

            Long id = Long.parseLong(idStr);

            String precioStr = JOptionPane.showInputDialog("Introduce el nuevo precio:");
            if (precioStr == null || precioStr.trim().isEmpty()) {
                mostrarMensaje("Precio no puede estar vacío.");
                return;
            }

            double nuevoPrecio = Double.parseDouble(precioStr);

            boolean updated = updateAppliancePriceWithId(id, nuevoPrecio);

            if (updated) {
                mostrarMensaje("Precio actualizado correctamente.");
            } else {
                mostrarMensaje("Error al actualizar el precio.");
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Entrada inválida. Por favor, introduce números válidos.");
        }
    }

    /**
     * Lista todos los electrodomésticos en la base de datos.
     */
    private static void listarTodosAppliances() {
        ArrayList<Appliance> appliances = getAllAppliances();
        if (appliances != null && !appliances.isEmpty()) {
            StringBuilder sb = new StringBuilder("Listado de todos los electrodomésticos:\n");
            for (Appliance a : appliances) {
                sb.append(a.toString()).append("\n");
            }
            mostrarMensaje(sb.toString());
        } else {
            mostrarMensaje("No se encontraron electrodomésticos.");
        }
    }

    /**
     * Lista los electrodomésticos de una marca específica.
     *
     * @param marca La marca a filtrar.
     */
    private static void listarAppliancesPorMarca(String marca) {
        ArrayList<Appliance> appliances = getAppliancesByMarca(marca);
        if (appliances != null && !appliances.isEmpty()) {
            StringBuilder sb = new StringBuilder("Electrodomésticos de la marca " + marca + ":\n");
            for (Appliance a : appliances) {
                sb.append(a.toString()).append("\n");
            }
            mostrarMensaje(sb.toString());
        } else {
            mostrarMensaje("No se encontraron electrodomésticos de la marca " + marca + ".");
        }
    }

    /**
     * Lista los electrodomésticos dentro de un rango de precios específico.
     *
     * @param minPrice Precio mínimo.
     * @param maxPrice Precio máximo.
     */
    private static void listarAppliancesPorRangoPrecio(double minPrice, double maxPrice) {
        ArrayList<Appliance> appliances = getAllAppliancesWithPriceBounds(minPrice, maxPrice);
        if (appliances != null && !appliances.isEmpty()) {
            StringBuilder sb = new StringBuilder("Electrodomésticos con precio entre " + minPrice + " y " + maxPrice + " €:\n");
            for (Appliance a : appliances) {
                sb.append(a.toString()).append("\n");
            }
            mostrarMensaje(sb.toString());
        } else {
            mostrarMensaje("No se encontraron electrodomésticos en el rango de precio especificado.");
        }
    }

    /**
     * Importa electrodomésticos desde arrays predefinidos (Ejercicio 3).
     */
    private static void importarDesdeArrays() {
        String[] modelos = {"LG 85 p X295", "Lavadora Bosch 489", "Secador XYZ"};
        String[] marcas = {"LG", "Bosch", "LaPava"};
        String[] tipos = {"televisor", "lavadora", "secador"};
        int[] precios = {795, 345, 87};

        ArrayList<Appliance> appliancesToAdd = new ArrayList<>();
        for (int i = 0; i < modelos.length; i++) {
            appliancesToAdd.add(new Appliance(modelos[i], marcas[i], tipos[i], precios[i]));
        }

        int successCount = 0;
        for (Appliance a : appliancesToAdd) {
            if (saveAppliance(a)) {
                successCount++;
            }
        }

        mostrarMensaje("Se han importado " + successCount + " electrodomésticos.");
    }

    /**
     * Exporta los electrodomésticos a archivos CSV y JSON.
     */
    private static void exportarDatos() {
        ArrayList<Appliance> appliances = getAllAppliances();
        if (appliances != null && !appliances.isEmpty()) {
            try {
                // Exportar a CSV
                try (FileWriter fw = new FileWriter("appliances.csv")) {
                    fw.append("id,modelo,marca,tipo,precio\n");
                    for (Appliance a : appliances) {
                        fw.append(a.getId() + "," + a.getModelo() + "," + a.getMarca() + "," + a.getTipo() + "," + a.getPrecio() + "\n");
                    }
                }

                // Exportar a JSON
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(appliances);
                try (FileWriter fwJson = new FileWriter("appliances.json")) {
                    fwJson.write(jsonString);
                }

                mostrarMensaje("Datos exportados a CSV y JSON correctamente.");
            } catch (IOException e) {
                mostrarMensaje("Error al exportar los datos: " + e.getMessage());
            }
        } else {
            mostrarMensaje("No hay datos para exportar.");
        }
    }

    /**
     * Lee electrodomésticos desde un archivo CSV y los inserta en la base de datos.
     *
     * @param path Ruta al archivo CSV.
     */
    private static void leerDesdeCSV(String path) {
        try {
            boolean isFirstLine = true;
            for (String line : Files.readAllLines(Paths.get(path))) {
                if (isFirstLine) {
                    isFirstLine = false; // Saltar la cabecera
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 5) { // Verificar que la línea tiene todos los campos
                    String modelo = data[1];
                    String marca = data[2];
                    String tipo = data[3];
                    double precio = Double.parseDouble(data[4]);
                    Appliance appliance = new Appliance(modelo, marca, tipo, precio);
                    saveAppliance(appliance);
                }
            }
            mostrarMensaje("Datos importados desde CSV correctamente.");
        } catch (IOException e) {
            mostrarMensaje("Error al leer desde CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarMensaje("Error en el formato de los datos del CSV: " + e.getMessage());
        }
    }

    /**
     * Lee electrodomésticos desde un archivo JSON y los inserta en la base de datos.
     *
     * @param path Ruta al archivo JSON.
     */
    private static void leerDesdeJSON(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Appliance[] appliances = mapper.readValue(Paths.get(path).toFile(), Appliance[].class);
            int successCount = 0;
            for (Appliance a : appliances) {
                if (saveAppliance(a)) {
                    successCount++;
                }
            }
            mostrarMensaje("Se han importado " + successCount + " electrodomésticos desde JSON correctamente.");
        } catch (IOException e) {
            mostrarMensaje("Error al leer desde JSON: " + e.getMessage());
        }
    }

    /**
     * Guarda un electrodoméstico en la base de datos.
     *
     * @param appliance El electrodoméstico a guardar.
     * @return true si se guardó correctamente, false en caso contrario.
     */
    private static boolean saveAppliance(Appliance appliance) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(appliance);
            tx.commit();
            System.out.println("Electrodoméstico guardado correctamente en la base de datos: " + appliance);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error al guardar el electrodoméstico: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    /**
     * Actualiza el precio de un electrodoméstico basado en su ID.
     *
     * @param id         ID del electrodoméstico.
     * @param nuevoPrecio El nuevo precio.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    private static boolean updateAppliancePriceWithId(Long id, double nuevoPrecio) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String hql = "UPDATE Appliance SET precio = :nuevoPrecio WHERE id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("nuevoPrecio", nuevoPrecio);
            query.setParameter("id", id);
            int result = query.executeUpdate();
            tx.commit();
            if (result > 0) {
                System.out.println("Precio actualizado para el ID " + id);
                return true;
            } else {
                System.out.println("No se encontró un electrodoméstico con el ID " + id);
                return false;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error al actualizar el precio: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    /**
     * Obtiene todos los electrodomésticos de la base de datos.
     *
     * @return Lista de electrodomésticos.
     */
    private static ArrayList<Appliance> getAllAppliances() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM Appliance";
            Query<Appliance> query = session.createQuery(hql, Appliance.class);
            return (ArrayList<Appliance>) query.list();
        } catch (Exception e) {
            System.err.println("Error al obtener todos los electrodomésticos: " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }

    /**
     * Obtiene los electrodomésticos de una marca específica.
     *
     * @param marca La marca a filtrar.
     * @return Lista de electrodomésticos de la marca especificada.
     */
    private static ArrayList<Appliance> getAppliancesByMarca(String marca) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM Appliance WHERE marca = :marca";
            Query<Appliance> query = session.createQuery(hql, Appliance.class);
            query.setParameter("marca", marca);
            return (ArrayList<Appliance>) query.list();
        } catch (Exception e) {
            System.err.println("Error al obtener electrodomésticos por marca: " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }

    /**
     * Obtiene los electrodomésticos dentro de un rango de precios específico.
     *
     * @param minPrice Precio mínimo.
     * @param maxPrice Precio máximo.
     * @return Lista de electrodomésticos dentro del rango de precios.
     */
    private static ArrayList<Appliance> getAllAppliancesWithPriceBounds(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            System.err.println("Rangos de precio inválidos.");
            return null;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM Appliance WHERE precio BETWEEN :minPrice AND :maxPrice";
            Query<Appliance> query = session.createQuery(hql, Appliance.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return (ArrayList<Appliance>) query.list();
        } catch (Exception e) {
            System.err.println("Error al obtener electrodomésticos por rango de precio: " + e.getMessage());
            return null;
        } finally {
            session.close();
        }
    }

    /**
     * Muestra un mensaje al usuario mediante JOptionPane.
     *
     * @param mensaje El mensaje a mostrar.
     */
    private static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }
}
