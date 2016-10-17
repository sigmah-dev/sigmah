package update_database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import update_database.dao.SigmahDAO;
import update_database.entity.BudgetElement;
import update_database.entity.FlexibleElement;
import update_database.entity.LayoutConstraint;
import update_database.entity.Value;
import update_database.util.ConfigReader;

public class Migrate{
    
    private static String JDBC_DRIVER = "org.postgresql.Driver";
    
    
    private static Set<FlexibleElement> flexibleElementSet = new HashSet<>();
    
    public static void main(String[] args) throws Exception{
        
        if(args.length != 1){
            System.out.println("usage: java -jar migrate.jar 'Path Config File'");
            System.out.println("ex: java -jar migrate.jar config.properties");
            return;
        }
        
        String configFilePath = args[0]; 

        String url = ConfigReader.getPropValues("db_url", configFilePath);
        String user = ConfigReader.getPropValues("db_user", configFilePath);
        String pwd = ConfigReader.getPropValues("db_psw", configFilePath);
        
        String spent_budget = ConfigReader.getPropValues("spent_budget", configFilePath);
        String planned_budget = ConfigReader.getPropValues("planned_budget", configFilePath);
        String budget_ratio = ConfigReader.getPropValues("budget_ratio", configFilePath);
        
        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(url,user,pwd);
        
        SigmahDAO sigmahDAO = new SigmahDAO();
        sigmahDAO.setConnection(conn);
        
        if(!sigmahDAO.budgetRatioElementIsExiste()){
            System.out.println("budget_ratio_element table is missing");
            return;
        }

        List<BudgetElement> liste = sigmahDAO.findAllBudgetElement();
        List<BudgetElement> liste2 = new ArrayList<>(); 
        int cmp=1;
        for(BudgetElement b : liste){
            LayoutConstraint layoutConstraint = sigmahDAO.findLayoutGroupByBudgetId(b.getId());
            b.setLayoutGroupId(layoutConstraint.getLayoutGroup());
            b.setLayoutOrder(layoutConstraint.getSortOrder());
            
            b = sigmahDAO.findImportElement(b);
            
            List<Value> values = sigmahDAO.findValueByBudgetId(b.getId());
            if(values.isEmpty()){
                liste2.add(b);
                //System.out.println(cmp++ + "/ " + b);
            }else{
                for(Value v : values) {
                    String[] parts = v.getValue().split("~");
                    String divisor=null;
                    String dividend=null;
                    for(int i=0; i<parts.length; i++){
                        if(parts[i].startsWith(String.valueOf(b.getDividend()))){
                            dividend = parts[i].substring(parts[i].indexOf('%')+1);
                        }else if(parts[i].startsWith(String.valueOf(b.getDivisor()))){
                            divisor =  parts[i].substring(parts[i].indexOf('%')+1);
                        }
                    }
                    BudgetElement be = new BudgetElement(b.getId(), b.getDivisor(), b.getDividend());
                    be.setProjetId(v.getProjectId());
                    be.setDividendVal(dividend);
                    be.setDivisorVal(divisor);
                    be.setLayoutGroupId(layoutConstraint.getLayoutGroup());
                    be.setLayoutOrder(layoutConstraint.getSortOrder());
                    be.setUserId(v.getUserId());
                    liste2.add(be);
                    //System.out.println(cmp++ + "/ " + b);
                }
            }
        }
        
        sigmahDAO.removeBudgetSubConstrainte();
        sigmahDAO.setNullBudgetSub();
        sigmahDAO.truncateAllBudgetElement();
        
        for(BudgetElement b : liste2){
            System.out.println(cmp++ + "/ " + b);
          
            FlexibleElement flex = new FlexibleElement(b.getId());
            
            if(!flexibleElementSet.contains(flex)){
                //1-create dividend in flexible_element
                int dividendId = sigmahDAO.insertFlexibleElement("budget_spent", spent_budget);
                //2-create divisor in flexible_element
                int divisorId = sigmahDAO.insertFlexibleElement("budget_planned", planned_budget);
                //3-create budget_ratio in flexible_element
                int budgetRatioId = sigmahDAO.insertFlexibleElement(null, budget_ratio);
                
                //4-create budget_ratio_element
                //id_flexible_element & id_spent_field & id_planned_field
                sigmahDAO.insertBudgetRatioElement(budgetRatioId, dividendId, divisorId);
                
                //5-create texteare element
                sigmahDAO.insertTextArea(dividendId);
                sigmahDAO.insertTextArea(divisorId);
                
                //6-create layout_constrainte x3 (dividend & divisor & budget_ratio)
                sigmahDAO.insertLayoutConstraint(b.getLayoutOrder()+1, dividendId, b.getLayoutGroupId());
                sigmahDAO.insertLayoutConstraint(b.getLayoutOrder()+2, divisorId, b.getLayoutGroupId());
                sigmahDAO.insertLayoutConstraint(b.getLayoutOrder()+3, budgetRatioId, b.getLayoutGroupId());
                
                //insert ds importation_scheme_variable_flexible_element
                if(b.getDividendSchModId() !=0 && b.getDividendVarId() !=0)
                    sigmahDAO.insertImportFlexibleElement(dividendId, b.getDividendSchModId(), b.getDividendVarId());
                if(b.getDivisorSchModId() !=0 && b.getDivisorVarId() !=0)
                    sigmahDAO.insertImportFlexibleElement(divisorId, b.getDivisorSchModId(), b.getDivisorVarId());
                
                //supprime de importation_scheme_variable_flexible_element
                sigmahDAO.deleteImportFlexibleElement(b.getDividend());
                sigmahDAO.deleteImportFlexibleElement(b.getDivisor());
                
                //7-remove old budget from layout_constrainte & value & flexible_element & history_token & importation_scheme_variable_flexible_element & default_flexible_element
                sigmahDAO.deleteLayoutConstraint(b.getId());
                sigmahDAO.deleteValue(b.getId());
                sigmahDAO.deleteImportationFlexibleElement(b.getId());
                sigmahDAO.deleteDefaultFlexibleElement(b.getId());
                sigmahDAO.deleteFlexibleElement(b.getId());
                sigmahDAO.deleteHistoryToken(b.getId());
                
                flex.setDividendId(dividendId);
                flex.setDivisorId(divisorId);
                flexibleElementSet.add(flex);
            }else{
                flex = getflexibleElementFromSet(flex);
            }
            //8-create value x2 (dividend & divisor)
            if(b.getUserId() != 0){
                sigmahDAO.insertValue(b.getProjetId(), b.getDividendVal(), flex.getDividendId(), b.getUserId());
                sigmahDAO.insertValue(b.getProjetId(), b.getDivisorVal(), flex.getDivisorId(), b.getUserId());
            }
        }
        
        sigmahDAO.closeSession();
        System.out.println("migration is done");
    }
    
    private static FlexibleElement getflexibleElementFromSet(FlexibleElement flex) throws Exception{
        for (Iterator<FlexibleElement> it = flexibleElementSet.iterator(); it.hasNext(); ) {
            FlexibleElement f = it.next();
            if (f.equals(flex))
                return f;
        }
        throw new Exception("corrupted data");
    }
}

