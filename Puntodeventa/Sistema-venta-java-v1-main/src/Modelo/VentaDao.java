
package Modelo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

public class VentaDao {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    int r;
    
    public int IdVenta(){
        int id = 0;
        String sql = "SELECT MAX(id) FROM ventas";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id;
    }
    
    public int RegistrarVenta(Venta v){
        String sql = "INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, v.getCliente());
            ps.setString(2, v.getVendedor());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, v.getFecha());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return r;
    }


    public int RegistrarVenta(Venta v, double descuento){
        String sql = "INSERT INTO ventas (cliente, vendedor, total, fecha, fecha_hora, descuento) VALUES (?,?,?,?,NOW(),?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, v.getCliente());
            ps.setString(2, v.getVendedor());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, v.getFecha());
            ps.setDouble(5, descuento);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) { System.out.println(e.toString()); }
        }
        return r;
    }
    
    public int RegistrarDetalle(Detalle Dv){
       String sql = "INSERT INTO detalle (id_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, Dv.getId_pro());
            ps.setInt(2, Dv.getCantidad());
            ps.setDouble(3, Dv.getPrecio());
            ps.setInt(4, Dv.getId());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return r;
    }
    
    public boolean ActualizarStock(int cant, int id){
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1,cant);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
    
    public List Listarventas(){
       List<Venta> ListaVenta = new ArrayList();
       String sql = "SELECT c.id AS id_cli, c.nombre, v.* FROM clientes c INNER JOIN ventas v ON c.id = v.cliente";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Venta vent = new Venta();
               vent.setId(rs.getInt("id"));
               vent.setNombre_cli(rs.getString("nombre"));
               vent.setVendedor(rs.getString("vendedor"));
               vent.setTotal(rs.getDouble("total"));
               ListaVenta.add(vent);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return ListaVenta;
   }
    public Venta BuscarVenta(int id){
        Venta cl = new Venta();
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cl.setId(rs.getInt("id"));
                cl.setCliente(rs.getInt("cliente"));
                cl.setTotal(rs.getDouble("total"));
                cl.setVendedor(rs.getString("vendedor"));
                cl.setFecha(rs.getString("fecha"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return cl;
    }
    public void pdfV(int idventa, int Cliente, double total, String usuario) {
        try {
            Date date = new Date();
            FileOutputStream archivo;
            String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            File salida = new File(url + "venta.pdf");
            archivo = new FileOutputStream(salida);
            Document doc = new Document();
            PdfWriter.getInstance(doc, archivo);
            doc.open();
            Image img = Image.getInstance(getClass().getResource("/Img/logo_pdf.png"));
            //Fecha
            Paragraph fecha = new Paragraph();
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
            fecha.add(Chunk.NEWLINE);
            fecha.add("Vendedor: " + usuario + "\nFolio: " + idventa + "\nFecha: "
                    + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date) + "\n\n");
            PdfPTable Encabezado = new PdfPTable(4);
            Encabezado.setWidthPercentage(100);
            Encabezado.getDefaultCell().setBorder(0);
            float[] columnWidthsEncabezado = new float[]{20f, 30f, 70f, 40f};
            Encabezado.setWidths(columnWidthsEncabezado);
            Encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            Encabezado.addCell(img);
            Encabezado.addCell("");
            //info empresa
            String config = "SELECT * FROM config";
            String mensaje = "";
            try {
                con = cn.getConnection();
                ps = con.prepareStatement(config);
                rs = ps.executeQuery();
                if (rs.next()) {
                    mensaje = rs.getString("mensaje");
                    Encabezado.addCell("Ruc:    " + rs.getString("ruc") + "\nNombre: " + rs.getString("nombre") + "\nTeléfono: " + rs.getString("telefono") + "\nDirección: " + rs.getString("direccion") + "\n\n");
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            //
            Encabezado.addCell(fecha);
            doc.add(Encabezado);
            //cliente
            Paragraph cli = new Paragraph();
            cli.add(Chunk.NEWLINE);
            cli.add("DATOS DEL CLIENTE" + "\n\n");
            doc.add(cli);

            PdfPTable proveedor = new PdfPTable(3);
            proveedor.setWidthPercentage(100);
            proveedor.getDefaultCell().setBorder(0);
            float[] columnWidthsCliente = new float[]{50f, 25f, 25f};
            proveedor.setWidths(columnWidthsCliente);
            proveedor.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell cliNom = new PdfPCell(new Phrase("Nombre", negrita));
            PdfPCell cliTel = new PdfPCell(new Phrase("Télefono", negrita));
            PdfPCell cliDir = new PdfPCell(new Phrase("Dirección", negrita));
            cliNom.setBorder(Rectangle.NO_BORDER);
            cliTel.setBorder(Rectangle.NO_BORDER);
            cliDir.setBorder(Rectangle.NO_BORDER);
            proveedor.addCell(cliNom);
            proveedor.addCell(cliTel);
            proveedor.addCell(cliDir);
            String prove = "SELECT * FROM clientes WHERE id = ?";
            try {
                ps = con.prepareStatement(prove);
                ps.setInt(1, Cliente);
                rs = ps.executeQuery();
                if (rs.next()) {
                    proveedor.addCell(rs.getString("nombre"));
                    proveedor.addCell(rs.getString("telefono"));
                    proveedor.addCell(rs.getString("direccion") + "\n\n");
                } else {
                    proveedor.addCell("Publico en General");
                    proveedor.addCell("S/N");
                    proveedor.addCell("S/N" + "\n\n");
                }

            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            doc.add(proveedor);

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.getDefaultCell().setBorder(0);
            float[] columnWidths = new float[]{15f, 12f, 43f, 15f, 15f};
            tabla.setWidths(columnWidths);
            tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell c0 = new PdfPCell(new Phrase("Código", negrita));
            PdfPCell c1 = new PdfPCell(new Phrase("Cant.", negrita));
            PdfPCell c2 = new PdfPCell(new Phrase("Descripción.", negrita));
            PdfPCell c3 = new PdfPCell(new Phrase("P. unt.", negrita));
            PdfPCell c4 = new PdfPCell(new Phrase("P. Total", negrita));
            c0.setBorder(Rectangle.NO_BORDER);
            c1.setBorder(Rectangle.NO_BORDER);
            c2.setBorder(Rectangle.NO_BORDER);
            c3.setBorder(Rectangle.NO_BORDER);
            c4.setBorder(Rectangle.NO_BORDER);
            c0.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(c0);
            tabla.addCell(c1);
            tabla.addCell(c2);
            tabla.addCell(c3);
            tabla.addCell(c4);
            String product = "SELECT d.id, d.id_pro,d.id_venta, d.precio, d.cantidad, p.id, p.codigo, p.nombre FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?";
            try {
                ps = con.prepareStatement(product);
                ps.setInt(1, idventa);
                rs = ps.executeQuery();
                while (rs.next()) {
                    double subTotal = rs.getInt("cantidad") * rs.getDouble("precio");
                    tabla.addCell(rs.getString("codigo"));
                    tabla.addCell(rs.getString("cantidad"));
                    tabla.addCell(rs.getString("nombre"));
                    tabla.addCell(rs.getString("precio"));
                    tabla.addCell(String.valueOf(subTotal));
                }

            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            doc.add(tabla);
            double descuentoTicket = BuscarDescuentoVenta(idventa);
            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);
            if (descuentoTicket > 0) {
                info.add("Descuento: $" + descuentoTicket + "\n");
            }
            info.add("Monto a pagar: $" + total);
            info.setAlignment(Element.ALIGN_RIGHT);
            doc.add(info);
            Paragraph firma = new Paragraph();
            firma.add(Chunk.NEWLINE);
            firma.add("Cancelacion \n\n");
            firma.add("------------------------------------\n");
            firma.add("Firma \n");
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);
            Paragraph gr = new Paragraph();
            gr.add(Chunk.NEWLINE);
            gr.add(mensaje);
            gr.setAlignment(Element.ALIGN_CENTER);
            doc.add(gr);
            doc.close();
            archivo.close();
            Desktop.getDesktop().open(salida);
        } catch (DocumentException | IOException e) {
            System.out.println(e.toString());
        }
    }

    

    public int CantidadVentasPorFecha(String fecha){
        int cantidad = 0;
        String sql = "SELECT COUNT(*) AS cantidad FROM ventas WHERE fecha = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fecha);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt("cantidad");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return cantidad;
    }

    public double TotalVentasPorFecha(String fecha){
        double total = 0.00;
        String sql = "SELECT IFNULL(SUM(total), 0) AS total FROM ventas WHERE fecha = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fecha);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return total;
    }

    public String ProductoMasVendidoPorFecha(String fecha){
        String resultado = "Sin ventas registradas";
        String sql = "SELECT p.nombre, SUM(d.cantidad) AS cantidad "
                + "FROM ventas v "
                + "INNER JOIN detalle d ON d.id_venta = v.id "
                + "INNER JOIN productos p ON p.id = d.id_pro "
                + "WHERE v.fecha = ? "
                + "GROUP BY p.id, p.nombre "
                + "ORDER BY cantidad DESC "
                + "LIMIT 1";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fecha);
            rs = ps.executeQuery();
            if (rs.next()) {
                resultado = rs.getString("nombre") + " (" + rs.getInt("cantidad") + " piezas)";
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return resultado;
    }

    public String CategoriaMasVendidaPorFecha(String fecha){
        String resultado = "Sin ventas registradas";
        String sql = "SELECT IFNULL(c.nombre, 'Sin categoría') AS categoria, SUM(d.cantidad) AS cantidad "
                + "FROM ventas v "
                + "INNER JOIN detalle d ON d.id_venta = v.id "
                + "INNER JOIN productos p ON p.id = d.id_pro "
                + "LEFT JOIN categorias c ON c.id = p.categoria "
                + "WHERE v.fecha = ? "
                + "GROUP BY c.id, c.nombre "
                + "ORDER BY cantidad DESC "
                + "LIMIT 1";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fecha);
            rs = ps.executeQuery();
            if (rs.next()) {
                resultado = rs.getString("categoria") + " (" + rs.getInt("cantidad") + " piezas)";
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return resultado;
    }


    public double BuscarDescuentoVenta(int idVenta){
        double descuento = 0.00;
        String sql = "SELECT IFNULL(descuento,0) AS descuento FROM ventas WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            if (rs.next()) {
                descuento = rs.getDouble("descuento");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return descuento;
    }

    public List ListarDetalleVenta(int idVenta){
        List<String[]> lista = new ArrayList();
        String sql = "SELECT d.id_pro, p.codigo, p.nombre, d.cantidad, d.precio, (d.cantidad*d.precio) AS total "
                + "FROM detalle d INNER JOIN productos p ON p.id = d.id_pro WHERE d.id_venta = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("id_pro"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("cantidad"),
                    rs.getString("precio"),
                    rs.getString("total")
                });
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return lista;
    }

    public boolean DevolverProducto(int idVenta, int idProducto, int cantidadDevolver, String usuario){
        try {
            con = cn.getConnection();
            con.setAutoCommit(false);
            int cantidadActual = 0;
            double precio = 0.00;
            ps = con.prepareStatement("SELECT cantidad, precio FROM detalle WHERE id_venta = ? AND id_pro = ?");
            ps.setInt(1, idVenta);
            ps.setInt(2, idProducto);
            rs = ps.executeQuery();
            if (rs.next()) {
                cantidadActual = rs.getInt("cantidad");
                precio = rs.getDouble("precio");
            } else {
                con.rollback();
                return false;
            }
            if (cantidadDevolver <= 0 || cantidadDevolver > cantidadActual) {
                con.rollback();
                return false;
            }
            if (cantidadDevolver == cantidadActual) {
                ps = con.prepareStatement("DELETE FROM detalle WHERE id_venta = ? AND id_pro = ?");
                ps.setInt(1, idVenta);
                ps.setInt(2, idProducto);
                ps.executeUpdate();
            } else {
                ps = con.prepareStatement("UPDATE detalle SET cantidad = cantidad - ? WHERE id_venta = ? AND id_pro = ?");
                ps.setInt(1, cantidadDevolver);
                ps.setInt(2, idVenta);
                ps.setInt(3, idProducto);
                ps.executeUpdate();
            }
            ps = con.prepareStatement("UPDATE productos SET stock = stock + ? WHERE id = ?");
            ps.setInt(1, cantidadDevolver);
            ps.setInt(2, idProducto);
            ps.executeUpdate();

            double montoDevuelto = cantidadDevolver * precio;
            ps = con.prepareStatement("UPDATE ventas SET total = GREATEST(total - ?, 0) WHERE id = ?");
            ps.setDouble(1, montoDevuelto);
            ps.setInt(2, idVenta);
            ps.executeUpdate();

            ps = con.prepareStatement("INSERT INTO devoluciones (id_venta, id_producto, cantidad, monto, fecha_hora, usuario) VALUES (?,?,?,?,NOW(),?)");
            ps.setInt(1, idVenta);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadDevolver);
            ps.setDouble(4, montoDevuelto);
            ps.setString(5, usuario);
            ps.executeUpdate();

            con.commit();
            return true;
        } catch (SQLException e) {
            try { if(con != null) con.rollback(); } catch (SQLException ex) {}
            System.out.println(e.toString());
            return false;
        } finally {
            try { if(con != null) { con.setAutoCommit(true); con.close(); } } catch (SQLException e) {}
        }
    }

    public String ExportarVentasCSV(String fechaInicio, String fechaFin){
        String ruta = "";
        String sql = "SELECT v.id, v.fecha, IFNULL(v.fecha_hora,'') AS fecha_hora, c.nombre AS cliente, v.vendedor, "
                + "p.codigo, p.nombre AS producto, d.cantidad, d.precio, (d.cantidad*d.precio) AS subtotal, "
                + "IFNULL(v.descuento,0) AS descuento, v.total "
                + "FROM ventas v "
                + "INNER JOIN clientes c ON c.id = v.cliente "
                + "INNER JOIN detalle d ON d.id_venta = v.id "
                + "INNER JOIN productos p ON p.id = d.id_pro "
                + "WHERE STR_TO_DATE(v.fecha, '%d/%m/%Y') BETWEEN STR_TO_DATE(?, '%d/%m/%Y') AND STR_TO_DATE(?, '%d/%m/%Y') "
                + "ORDER BY v.id DESC";
        try {
            String home = System.getProperty("user.home");
            ruta = home + java.io.File.separator + "Documents" + java.io.File.separator + "reporte_ventas_" + fechaInicio.replace('/','-') + "_a_" + fechaFin.replace('/','-') + ".csv";
            java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(ruta), "UTF-8"));
            pw.println("FOLIO,FECHA,FECHA_HORA,CLIENTE,VENDEDOR,CODIGO,PRODUCTO,CANTIDAD,PRECIO,SUBTOTAL,DESCUENTO,TOTAL_VENTA");
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            rs = ps.executeQuery();
            while (rs.next()) {
                pw.println(rs.getInt("id") + "," + rs.getString("fecha") + "," + rs.getString("fecha_hora") + ",\"" + rs.getString("cliente") + "\",\"" + rs.getString("vendedor") + "\"," + rs.getString("codigo") + ",\"" + rs.getString("producto") + "\"," + rs.getInt("cantidad") + "," + rs.getDouble("precio") + "," + rs.getDouble("subtotal") + "," + rs.getDouble("descuento") + "," + rs.getDouble("total"));
            }
            pw.close();
        } catch (Exception e) {
            System.out.println(e.toString());
            ruta = "";
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return ruta;
    }

}
