package update_database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import update_database.entity.BudgetElement;
import update_database.entity.LayoutConstraint;
import update_database.entity.Value;

public class SigmahDAO{
    
    private Connection conn;

    /**
     * @param conn
     */
    public void setConnection(Connection conn){
        this.conn = conn;
    }
    
    public List<BudgetElement> findAllBudgetElement(){
        String sql = "select * from budget_element";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            List<BudgetElement> liste = new ArrayList<BudgetElement>();
            BudgetElement budget = null;
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                budget = new BudgetElement(
                    rs.getInt("id_flexible_element"),
                    rs.getInt("id_ratio_divisor"),
                    rs.getInt("id_ratio_dividend")
                );
                liste.add(budget);
            }
            rs.close();
            ps.close();
            return liste;
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        }
    }
    
    public BudgetElement findImportElement(BudgetElement b){
        String sql = "select b.var_id, f.sch_mod_id from importation_variable_budget_sub_field as b INNER JOIN importation_scheme_variable_flexible_element as f ON b.var_fle_id = f.var_fle_id WHERE id_budget_sub_field = ?";
        try {
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, b.getDividend());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                b.setDividendSchModId(rs.getInt("sch_mod_id"));
                b.setDividendVarId( rs.getInt("var_id"));
            }
            rs.close();
            ps.close();
            
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, b.getDivisor());
            rs = ps.executeQuery();
            if(rs.next()){
                b.setDivisorSchModId(rs.getInt("sch_mod_id"));
                b.setDivisorVarId( rs.getInt("var_id"));
            }
            rs.close();
            ps.close();
            return b;
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        }
    }
    
    public boolean budgetRatioElementIsExiste(){
        String sql = "select * from information_schema.tables where table_name= 'budget_ratio_element';";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            boolean existe = false;
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                existe = true;
            }
            rs.close();
            ps.close();
            return existe;
        } catch (SQLException e) {
            closeSession();
            return false;
            //throw new RuntimeException(e);
        }
    }
    
    public List<Value> findValueByBudgetId(int id){

        String sql = "SELECT * FROM value WHERE id_flexible_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            List<Value> values = new ArrayList<>();
            Value value;
            while(rs.next()){
                value = new Value();
                value.setId(rs.getInt("id_value"));
                value.setValue(rs.getString("value"));
                value.setProjectId(rs.getInt("id_project"));
                value.setUserId(rs.getInt("id_user_last_modif"));
                values.add(value);
            }
            rs.close();
            ps.close();
            return values;
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public LayoutConstraint findLayoutGroupByBudgetId(int id){

        String sql = "SELECT * FROM layout_constraint WHERE id_flexible_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            LayoutConstraint value = new LayoutConstraint();
            if(rs.next()){
                value.setLayoutGroup(rs.getInt("id_layout_group"));
                value.setSortOrder(rs.getInt("sort_order"));
            }
            rs.close();
            ps.close();
            return value;
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public int insertFlexibleElement(String code, String label){
        String sql = "INSERT INTO flexible_element (id_flexible_element, amendable, label, code, exportable, validates) VALUES (nextval('hibernate_sequence'), ?, ?, ?, ?, ?)";
        int id =0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setBoolean(1, true);
            ps.setString(2, label);
            ps.setString(3, code);
            ps.setBoolean(4, true);
            ps.setBoolean(5, false);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                id= rs.getInt(1);   
            ps.close();
            return id;
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void insertImportFlexibleElement(int flexibleId, int schModId, int varId){
        String sql = "INSERT INTO importation_scheme_variable_flexible_element (var_fle_id, var_fle_is_key, id_flexible_element, sch_mod_id, var_id)"
                                                                            + " VALUES (nextval('hibernate_sequence'), ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, false);
            ps.setInt(2, flexibleId);
            ps.setInt(3, schModId);
            ps.setInt(4, varId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void insertBudgetRatioElement(int budgetId, int spentId, int plannedId){
        String sql = "INSERT INTO budget_ratio_element (id_flexible_element, id_spent_field, id_planned_field) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, budgetId);
            ps.setInt(2, spentId);
            ps.setInt(3, plannedId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void insertValue(int id_project, String value, int flexibleId, int uderId){
        String sql = "INSERT INTO value (id_value, date_last_modif, id_project, action_last_modif, value, id_flexible_element, id_user_last_modif) "
                              + "VALUES (nextval('hibernate_sequence'), now(), ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id_project);
            ps.setString(2, "C");
            ps.setString(3, value);
            ps.setInt(4, flexibleId);
            ps.setInt(5, uderId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void insertLayoutConstraint(int sortOrder, int flexibleId, int LayoutGroupId){
        String sql = "INSERT INTO layout_constraint (id_layout_constraint, sort_order, id_flexible_element, id_layout_group) "
                + "VALUES (nextval('hibernate_sequence'), ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sortOrder);
            ps.setInt(2, flexibleId);
            ps.setInt(3, LayoutGroupId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void insertTextArea(int flexibleId){
        String sql = "INSERT INTO textarea_element (is_decimal, type, id_flexible_element) "
                + "VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, true);
            ps.setString(2, "N");
            ps.setInt(3, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void deleteLayoutConstraint(int flexibleId){
        String sql = "DELETE FROM layout_constraint WHERE  id_flexible_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void deleteValue(int flexibleId){
        String sql = "DELETE FROM value WHERE  id_flexible_element = ? ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void deleteFlexibleElement(int flexibleId){
        String sql = "DELETE FROM flexible_element WHERE  id_flexible_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    public void deleteDefaultFlexibleElement(int flexibleId){
        String sql = "DELETE FROM default_flexible_element WHERE  id_flexible_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    public void deleteImportationFlexibleElement(int flexibleId){
        String sql = "DELETE FROM importation_scheme_variable_flexible_element WHERE  id_flexible_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void deleteHistoryToken(int flexibleId){
        String sql = "DELETE FROM history_token WHERE id_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void deleteImportFlexibleElement(int flexibleId){
        String sql = "DELETE FROM importation_scheme_variable_flexible_element WHERE id_flexible_element = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flexibleId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void removeBudgetSubConstrainte(){
        String sql = "ALTER TABLE budget_sub_field ALTER COLUMN id_budget_element DROP NOT NULL; ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void setNullBudgetSub(){
        String sql = "UPDATE budget_sub_field SET id_budget_element = NULL ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }
    
    public void truncateAllBudgetElement(){
        String sql = "TRUNCATE TABLE budget_element, budget_sub_field, importation_variable_budget_sub_field, importation_scheme_variable_budget_element";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            closeSession();
            throw new RuntimeException(e);
        } 
    }

    /**
     * 
     */
    public void closeSession(){
        if (conn != null) {
            try {
            conn.close();
            } catch (SQLException e) {}
        }
    }
    
}

