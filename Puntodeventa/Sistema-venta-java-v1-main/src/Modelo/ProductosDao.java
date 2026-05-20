package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; // <--- ESTO ARREGLA EL ERROR DE ARRAYLIST
import java.util.List;      // <--- ESTO ARREGLA EL CONFLICTO CON LIST

public class ProductosDao {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    
    public boolean RegistrarProductos(Productos pro){
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, stock_minimo, precio, categoria, imagen) VALUES (?,?,?,?,?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setInt(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setInt(5, pro.getStockMinimo() > 0 ? pro.getStockMinimo() : 5);
            ps.setDouble(6, pro.getPrecio());
            ps.setInt(7, pro.getCategoria());
            ps.setBytes(8, pro.getImagen()); // Guarda los bytes de la foto
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
    }
    
public List ListarProductos(){
   List<Productos> Listapro = new ArrayList();
   // Añadimos p.imagen a la consulta SQL
   String sql = "SELECT pr.id AS id_proveedor, pr.nombre AS nombre_proveedor, cat.id AS id_categoria, cat.nombre AS nombre_categoria, p.id, p.codigo, p.nombre, p.proveedor, p.stock, p.stock_minimo, p.precio, p.categoria, p.imagen FROM productos p INNER JOIN proveedor pr ON pr.id = p.proveedor LEFT JOIN categorias cat ON cat.id = p.categoria ORDER BY p.id DESC";
   try {
       con = cn.getConnection();
       ps = con.prepareStatement(sql);
       rs = ps.executeQuery();
       while (rs.next()) {               
           Productos pro = new Productos();
           pro.setId(rs.getInt("id"));
           pro.setCodigo(rs.getString("codigo"));
           pro.setNombre(rs.getString("nombre"));
           pro.setProveedor(rs.getInt("proveedor"));
           pro.setProveedorPro(rs.getString("nombre_proveedor"));
           pro.setStock(rs.getInt("stock"));
           pro.setStockMinimo(rs.getInt("stock_minimo"));
           pro.setPrecio(rs.getDouble("precio"));
           pro.setCategoria(rs.getInt("categoria"));
           pro.setCategoriaPro(rs.getString("nombre_categoria") == null ? "Sin categoría" : rs.getString("nombre_categoria"));
           pro.setImagen(rs.getBytes("imagen")); // <--- ESTA LÍNEA ES CLAVE para traer la foto
           Listapro.add(pro);
       }
   } catch (SQLException e) {
       System.out.println(e.toString());
   }
   return Listapro;
}
    
    public boolean EliminarProductos(int id){
        String sql = "DELETE FROM productos WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
    }
    
    public boolean ModificarProductos(Productos pro){
        String sql = "UPDATE productos SET codigo=?, nombre=?, proveedor=?, stock=?, stock_minimo=?, precio=?, categoria=?, imagen=? WHERE id=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setInt(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setInt(5, pro.getStockMinimo() > 0 ? pro.getStockMinimo() : 5);
            ps.setDouble(6, pro.getPrecio());
            ps.setInt(7, pro.getCategoria());
            ps.setBytes(8, pro.getImagen());
            ps.setInt(8, pro.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
    }
    
    public Productos BuscarPro(String cod){
        Productos producto = new Productos();
        String sql = "SELECT p.*, cat.nombre AS nombre_categoria FROM productos p "
                + "LEFT JOIN categorias cat ON cat.id = p.categoria "
                + "WHERE p.codigo = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cod);
            rs = ps.executeQuery();
            if (rs.next()) {
                producto.setId(rs.getInt("id"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setProveedor(rs.getInt("proveedor"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
                producto.setStockMinimo(rs.getInt("stock_minimo"));
                producto.setCategoria(rs.getInt("categoria"));
                producto.setCategoriaPro(rs.getString("nombre_categoria") == null ? "Sin categoría" : rs.getString("nombre_categoria"));
                producto.setImagen(rs.getBytes("imagen"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return producto;
    }
    
    public Productos BuscarId(int id){
        Productos pro = new Productos();
        String sql = "SELECT p.*, cat.nombre AS nombre_categoria FROM productos p "
                + "LEFT JOIN categorias cat ON cat.id = p.categoria "
                + "WHERE p.id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                pro.setId(rs.getInt("id"));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setProveedor(rs.getInt("proveedor"));
                pro.setStock(rs.getInt("stock"));
                pro.setStockMinimo(rs.getInt("stock_minimo"));
                pro.setPrecio(rs.getDouble("precio"));
                pro.setCategoria(rs.getInt("categoria"));
                pro.setCategoriaPro(rs.getString("nombre_categoria") == null ? "Sin categoría" : rs.getString("nombre_categoria"));
                pro.setImagen(rs.getBytes("imagen"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return pro;
    }
    
    public List ListarProductosPorCategoria(int idCategoria){
        List<Productos> lista = new ArrayList();
        String sql = "SELECT p.*, cat.nombre AS nombre_categoria FROM productos p "
                + "LEFT JOIN categorias cat ON cat.id = p.categoria ";

        if (idCategoria > 0) {
            sql += "WHERE p.categoria = ? ";
        }

        sql += "ORDER BY p.nombre ASC";

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            if (idCategoria > 0) {
                ps.setInt(1, idCategoria);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Productos pro = new Productos();
                pro.setId(rs.getInt("id"));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setProveedor(rs.getInt("proveedor"));
                pro.setStock(rs.getInt("stock"));
                pro.setStockMinimo(rs.getInt("stock_minimo"));
                pro.setPrecio(rs.getDouble("precio"));
                pro.setCategoria(rs.getInt("categoria"));
                pro.setCategoriaPro(rs.getString("nombre_categoria") == null ? "Sin categoría" : rs.getString("nombre_categoria"));
                pro.setImagen(rs.getBytes("imagen"));
                lista.add(pro);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }

        return lista;
    }

    public List ListarCategorias(){
        List<Combo> lista = new ArrayList();
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre ASC";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Combo(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return lista;
    }

    public int BuscarIdCategoria(String nombre){
        int id = 0;
        String sql = "SELECT id FROM categorias WHERE nombre = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return id;
    }

    public int RegistrarCategoria(String nombre){
        int idExistente = BuscarIdCategoria(nombre);
        if (idExistente > 0) {
            return idExistente;
        }

        int idGenerado = 0;
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombre);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return idGenerado;
    }



    public List ListarProductosBajoStock(){
        List<Productos> lista = new ArrayList();
        String sql = "SELECT p.*, pr.nombre AS nombre_proveedor, cat.nombre AS nombre_categoria "
                + "FROM productos p "
                + "LEFT JOIN proveedor pr ON pr.id = p.proveedor "
                + "LEFT JOIN categorias cat ON cat.id = p.categoria "
                + "WHERE p.stock <= p.stock_minimo "
                + "ORDER BY p.stock ASC, p.nombre ASC";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Productos pro = new Productos();
                pro.setId(rs.getInt("id"));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setProveedor(rs.getInt("proveedor"));
                pro.setProveedorPro(rs.getString("nombre_proveedor"));
                pro.setStock(rs.getInt("stock"));
                pro.setStockMinimo(rs.getInt("stock_minimo"));
                pro.setPrecio(rs.getDouble("precio"));
                pro.setCategoria(rs.getInt("categoria"));
                pro.setCategoriaPro(rs.getString("nombre_categoria") == null ? "Sin categoría" : rs.getString("nombre_categoria"));
                pro.setImagen(rs.getBytes("imagen"));
                lista.add(pro);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return lista;
    }

    public boolean RegistrarEntradaMercancia(int idProducto, int cantidad, double costoUnitario, String observacion, String usuario){
        String insertCompra = "INSERT INTO compras (id_producto, cantidad, costo_unitario, fecha_hora, observacion, usuario) VALUES (?,?,?,NOW(),?,?)";
        String updateStock = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        try {
            con = cn.getConnection();
            con.setAutoCommit(false);
            ps = con.prepareStatement(insertCompra);
            ps.setInt(1, idProducto);
            ps.setInt(2, cantidad);
            ps.setDouble(3, costoUnitario);
            ps.setString(4, observacion);
            ps.setString(5, usuario);
            ps.executeUpdate();
            ps = con.prepareStatement(updateStock);
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
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

    public String ExportarBajoStockCSV(){
        String ruta = "";
        try {
            String home = System.getProperty("user.home");
            ruta = home + java.io.File.separator + "Documents" + java.io.File.separator + "reporte_bajo_stock.csv";
            java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(ruta), "UTF-8"));
            pw.println("ID,CODIGO,PRODUCTO,CATEGORIA,PROVEEDOR,STOCK,STOCK_MINIMO,PRECIO");
            List<Productos> lista = ListarProductosBajoStock();
            for (int i = 0; i < lista.size(); i++) {
                Productos p = lista.get(i);
                pw.println(p.getId() + "," + p.getCodigo() + ",\"" + p.getNombre() + "\",\"" + p.getCategoriaPro() + "\",\"" + p.getProveedorPro() + "\"," + p.getStock() + "," + p.getStockMinimo() + "," + p.getPrecio());
            }
            pw.close();
        } catch (Exception e) {
            System.out.println(e.toString());
            ruta = "";
        }
        return ruta;
    }

    public Config BuscarDatos(){
        Config conf = new Config();
        String sql = "SELECT * FROM config";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                conf.setId(rs.getInt("id"));
                conf.setRuc(rs.getString("ruc"));
                conf.setNombre(rs.getString("nombre"));
                conf.setTelefono(rs.getString("telefono"));
                conf.setDireccion(rs.getString("direccion"));
                conf.setMensaje(rs.getString("mensaje"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
        }
        return conf;
    }
    
    public boolean ModificarDatos(Config conf){
       String sql = "UPDATE config SET ruc=?, nombre=?, telefono=?, direccion=?, mensaje=? WHERE id=?";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setString(1, conf.getRuc());
           ps.setString(2, conf.getNombre());
           ps.setString(3, conf.getTelefono());
           ps.setString(4, conf.getDireccion());
           ps.setString(5, conf.getMensaje());
           ps.setInt(6, conf.getId());
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println(e.toString());
           return false;
       } finally {
            try { if(con != null) con.close(); } catch (SQLException e) {}
       }
   }
}