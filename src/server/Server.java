import java.util.*;
import java.sql.*;

public class Server {
    //String usada para conectar no nosso banco de dados
    public static String connectionUrl =
    "jdbc:sqlserver://redes-jogo-da-velha.database.windows.net:1433;database=jogo_da_velha;user=redes@redes-jogo-da-velha;password=sered_123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    public static void main(String[] args) {
    	/* SUMÁRIO
    	 * sel_usuario_username: retorna usuários por username
    	 * sel_usuario: retorna usuário por ID
    	 * sel_login: retorna se usuário e senha corretos
    	 * sel_amizade: retorna amizades pelo ID dos amigos
    	 * del_amizade: deleta amizade pelo ID dos amigos
    	 * del_usuario: deleta usuário por ID
    	 * */
    	try {
			System.out.println(sel_usuario(15).toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    /*INSERIR USUÁRIO
     * PARÂMETROS: 
     * - Nome (OBRIGATÓRIO)
     * - Username (@), único, (OBRIGATÓRIO)
     * - Senha (OBRIGATÓRIO)
     * RETORNO: Nenhum
     * */
    public static void ins_usuario (String nome, String username, String senha) throws SQLException {
        //Tenta estabelecer a conexão
        try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
            //Chama a stored procedure de INSERÇÃO do banco de dados e passa dois parâmetros (nome, username).
            CallableStatement call = con.prepareCall("{ call ins_usuario(?, ?, ?) }");
            //Define os parâmetros (precisam ser definidos em ordem, ver procedure para saber a ordem)
            call.setString(1, nome);
            call.setString(2, username);
            call.setString(3,  senha);            //Executa a procedure
            call.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*INSERIR AMIZADE
     * PARÂMETROS: 
     * - id_1: id do usuário (OBRIGATÓRIO)
     * - id_2: id do usuário (OBRIGATÓRIO)
     * (id_1 é amigo de id_2)
     * (id_2 não necessariamente é amigo de id_1)
     * RETORNO: Nenhum
     * */
    public static void ins_amizade(int id_1, int id_2) throws SQLException {
        //Tenta estabelecer a conexão
        try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
            //Chama a stored procedure de INSERÇÃO do banco de dados e passa dois parâmetros (nome, username).
            CallableStatement call = con.prepareCall("{ call ins_amizade(?, ?) }");
            //Define os parâmetros (precisam ser definidos em ordem, ver procedure para saber a ordem)
            call.setInt(1, id_1);
            call.setInt(2, id_2);
            
            //Executa a procedure
            call.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*BUSCA AMIZADE
     * PARÂMETROS: 
     * - id_1: id do usuário (NÃO OBRIGATÓRIO)
     * - id_2: id do usuário (NÃO OBRIGATÓRIO)
     * (Caso dois parâmetros nulos, retorna todas amizades)
     * (Caso id_2 nulo, retorna todos usuários que id_1 é amigo)
     * (Caso id_1 nulo, retorna todas pessoas que possuem como amigo id_2)
     * */
    public static List<Map<String, Object>> sel_amizade(Integer id_1, Integer id_2) throws SQLException {
    	ResultSet rs = null;
    	List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        //Tenta estabelecer a conexão
        try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
            CallableStatement call = con.prepareCall("{ call sel_amizade(?, ?) }");
            if (id_1 == null) call.setNull(1, java.sql.Types.NVARCHAR);
            else call.setInt(1, id_1);
            if (id_2 == null) call.setNull(2, java.sql.Types.NVARCHAR);
            else call.setInt(2, id_2);
            
            //Executa a procedure
            call.execute();
            
            //ResultSet: onde o retorno do SQL é armazenado
            rs = call.getResultSet();
            
            Map<String, Object> row = null;

            ResultSetMetaData metaData = rs.getMetaData();
            Integer columnCount = metaData.getColumnCount();

            while (rs.next()) {
                row = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultList;
    }
    
    /*BUSCA USUÁRIO POR USERNAME
     * PARÂMETROS: 
     * - username: username do usuário (NÃO OBRIGATÓRIO)
     * (Caso nenhum usuário seja passado, retorna todos os usuários)
     * RETORNO: ResultSet
     * */
    public static List<Map<String, Object>> sel_usuario_username(String username) throws SQLException {
    	ResultSet rs = null;
    	List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    	
        //Tenta estabelecer a conexão
        try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
            CallableStatement call = con.prepareCall("{ call sel_usuario_username(?) }");
            //Define os parâmetros (precisam ser definidos em ordem, ver procedure para saber a ordem)
            if (username == null) call.setNull(1, java.sql.Types.NVARCHAR);
            else call.setString(1, username);
            
            //Executa a procedure
            call.execute();

           rs = call.getResultSet();
           
           Map<String, Object> row = null;

           ResultSetMetaData metaData = rs.getMetaData();
           Integer columnCount = metaData.getColumnCount();

           while (rs.next()) {
               row = new HashMap<String, Object>();
               for (int i = 1; i <= columnCount; i++) {
                   row.put(metaData.getColumnName(i), rs.getObject(i));
               }
               resultList.add(row);
           }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultList;
    }
    
    /*LOGIN
     * PARÂMETROS: 
     * - username: username do usuário (OBRIGATÓRIO)
     * - senha: senha do usuário (OBRIGATÓRIO)
     * (Caso nenhum usuário seja retornado, usuário e/ou senha incorretos)
     * RETORNO: ResultSet
     * */
    public static List<Map<String, Object>> sel_usuario(Integer id) throws SQLException {
    	ResultSet rs = null;
    	List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    	
        //Tenta estabelecer a conexão
        try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
            CallableStatement call = con.prepareCall("{ call sel_usuario(?) }");
            //Define os parâmetros (precisam ser definidos em ordem, ver procedure para saber a ordem)
            if (id == null) call.setNull(1, java.sql.Types.NVARCHAR);
            else call.setInt(1, id);
            
            
            //Executa a procedure
            call.execute();

           rs = call.getResultSet();
           
           Map<String, Object> row = null;

           ResultSetMetaData metaData = rs.getMetaData();
           Integer columnCount = metaData.getColumnCount();

           while (rs.next()) {
               row = new HashMap<String, Object>();
               for (int i = 1; i <= columnCount; i++) {
                   row.put(metaData.getColumnName(i), rs.getObject(i));
               }
               resultList.add(row);
           }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultList;
    }
    
    /*LOGIN
     * PARÂMETROS: 
     * - username: username do usuário (OBRIGATÓRIO)
     * - senha: senha do usuário (OBRIGATÓRIO)
     * (Caso nenhum usuário seja retornado, usuário e/ou senha incorretos)
     * RETORNO: ResultSet
     * */
    public static List<Map<String, Object>> sel_login(String username, String senha) throws SQLException {
    	ResultSet rs = null;
    	List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    	
        //Tenta estabelecer a conexão
        try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
            CallableStatement call = con.prepareCall("{ call sel_login(?, ?) }");
            //Define os parâmetros (precisam ser definidos em ordem, ver procedure para saber a ordem)
            if (username == null) call.setNull(1, java.sql.Types.NVARCHAR);
            else call.setString(1, username);
            if (senha == null) call.setNull(2, java.sql.Types.NVARCHAR);
            else call.setString(2, senha);
            
            //Executa a procedure
            call.execute();

           rs = call.getResultSet();
           
           Map<String, Object> row = null;

           ResultSetMetaData metaData = rs.getMetaData();
           Integer columnCount = metaData.getColumnCount();

           while (rs.next()) {
               row = new HashMap<String, Object>();
               for (int i = 1; i <= columnCount; i++) {
                   row.put(metaData.getColumnName(i), rs.getObject(i));
               }
               resultList.add(row);
           }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultList;
    }
    
    /*DELETA AMIZADE
     * PARÂMETROS: 
     * - id_1: id do usuário 1 (OBRIGATÓRIO)
     * - id_2: id do usuário 2 (OBRIGATÓRIO)
     * (Deleta relação de amizade entre o usuário de id_1 -> id_2)
     * RETORNO: Nenhum
     * */
    public static void del_amizade(int id_1, int id_2) throws SQLException {
        try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
            //Chama a stored procedure de INSERÇÃO do banco de dados e passa dois parâmetros (nome, username).
            CallableStatement call = con.prepareCall("{ call del_amizade(?, ?) }");
            //Define os parâmetros (precisam ser definidos em ordem, ver procedure para saber a ordem)
            call.setInt(1, id_1);
            call.setInt(2, id_2);
            
            //Executa a procedure
            call.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*DELETA USUÁRIO
     * PARÂMETROS: 
     * - id_usuario: id do usuário
     * (Deleta o usuário de id_1)
     * RETORNO: Nenhum
     * */
    public static void del_usuario(int id_usuario) throws SQLException {
    try (Connection con = DriverManager.getConnection(Server.connectionUrl);) {
        //Chama a stored procedure de INSERÇÃO do banco de dados e passa dois parâmetros (nome, username).
        CallableStatement call = con.prepareCall("{ call del_usuario(?) }");
        //Define os parâmetros (precisam ser definidos em ordem, ver procedure para saber a ordem)
        call.setInt(1, id_usuario);
        
        //Executa a procedure
        call.execute();
    }
    catch (SQLException e) {
        e.printStackTrace();
    }
}
}