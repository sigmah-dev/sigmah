package update_database.entity;


public class BudgetElement{

    private int id;
    private int divisor;
    private int dividend;
    
    private int projetId;
    private String divisorVal;
    private String dividendVal;
    
    private int layoutOrder;
    private Integer layoutGroupId;
    private int userId;
    
    private int DividendVarId;
    private int DividendSchModId;
    private int DivisorVarId;
    private int DivisorSchModId;
    
    /**
     * Constructeur.
     * @param id
     * @param divisor
     * @param divident
     */
    public BudgetElement(int id, int divisor, int dividend){
        this.id = id;
        this.divisor = divisor;
        this.dividend = dividend;
    }
    /**
     * Accesseur en lecture du champ <code>id</code>.
     * @return le champ <code>id</code>.
     */
    public int getId()
    {
        return id;
    }
    /**
     * Accesseur en écriture du champ <code>id</code>.
     * @param id la valeur à écrire dans <code>id</code>.
     */
    public void setId(int id)
    {
        this.id = id;
    }
    /**
     * Accesseur en lecture du champ <code>divisor</code>.
     * @return le champ <code>divisor</code>.
     */
    public int getDivisor()
    {
        return divisor;
    }
    /**
     * Accesseur en écriture du champ <code>divisor</code>.
     * @param divisor la valeur à écrire dans <code>divisor</code>.
     */
    public void setDivisor(int divisor)
    {
        this.divisor = divisor;
    }
    /**
     * Accesseur en lecture du champ <code>dividend</code>.
     * @return le champ <code>dividend</code>.
     */
    public int getDividend()
    {
        return dividend;
    }
    /**
     * Accesseur en écriture du champ <code>dividend</code>.
     * @param divident la valeur à écrire dans <code>dividend</code>.
     */
    public void setDividend(int dividend)
    {
        this.dividend = dividend;
    }
    /**
     * Accesseur en lecture du champ <code>projetId</code>.
     * @return le champ <code>projetId</code>.
     */
    public int getProjetId()
    {
        return projetId;
    }
    /**
     * Accesseur en écriture du champ <code>projetId</code>.
     * @param projetId la valeur à écrire dans <code>projetId</code>.
     */
    public void setProjetId(int projetId)
    {
        this.projetId = projetId;
    }
    /**
     * Accesseur en lecture du champ <code>divisorVal</code>.
     * @return le champ <code>divisorVal</code>.
     */
    public String getDivisorVal()
    {
        return divisorVal;
    }
    /**
     * Accesseur en écriture du champ <code>divisorVal</code>.
     * @param divisorVal la valeur à écrire dans <code>divisorVal</code>.
     */
    public void setDivisorVal(String divisorVal)
    {
        this.divisorVal = divisorVal;
    }
    /**
     * Accesseur en lecture du champ <code>dividendVal</code>.
     * @return le champ <code>dividendVal</code>.
     */
    public String getDividendVal()
    {
        return dividendVal;
    }
    /**
     * Accesseur en écriture du champ <code>dividendVal</code>.
     * @param dividendVal la valeur à écrire dans <code>dividendVal</code>.
     */
    public void setDividendVal(String dividendVal)
    {
        this.dividendVal = dividendVal;
    }
    /**
     * Accesseur en lecture du champ <code>layoutGroupId</code>.
     * @return le champ <code>layoutGroupId</code>.
     */
    public Integer getLayoutGroupId()
    {
        return layoutGroupId;
    }
    /**
     * Accesseur en écriture du champ <code>layoutGroupId</code>.
     * @param layoutGroupId la valeur à écrire dans <code>layoutGroupId</code>.
     */
    public void setLayoutGroupId(Integer layoutGroupId)
    {
        this.layoutGroupId = layoutGroupId;
    }
    /**
     * Accesseur en lecture du champ <code>userId</code>.
     * @return le champ <code>userId</code>.
     */
    public int getUserId()
    {
        return userId;
    }
    /**
     * Accesseur en écriture du champ <code>userId</code>.
     * @param userId la valeur à écrire dans <code>userId</code>.
     */
    public void setUserId(int userId)
    {
        this.userId = userId;
    }
    /**
     * Accesseur en lecture du champ <code>layoutOrder</code>.
     * @return le champ <code>layoutOrder</code>.
     */
    public int getLayoutOrder()
    {
        return layoutOrder;
    }
    /**
     * Accesseur en écriture du champ <code>layoutOrder</code>.
     * @param layoutOrder la valeur à écrire dans <code>layoutOrder</code>.
     */
    public void setLayoutOrder(int layoutOrder)
    {
        this.layoutOrder = layoutOrder;
    }

    /**
     * Accesseur en lecture du champ <code>divisorVarId</code>.
     * @return le champ <code>divisorVarId</code>.
     */
    public int getDivisorVarId()
    {
        return DivisorVarId;
    }
    /**
     * Accesseur en écriture du champ <code>divisorVarId</code>.
     * @param divisorVarId la valeur à écrire dans <code>divisorVarId</code>.
     */
    public void setDivisorVarId(int divisorVarId)
    {
        DivisorVarId = divisorVarId;
    }
    /**
     * Accesseur en lecture du champ <code>divisorSchModId</code>.
     * @return le champ <code>divisorSchModId</code>.
     */
    public int getDivisorSchModId()
    {
        return DivisorSchModId;
    }
    /**
     * Accesseur en écriture du champ <code>divisorSchModId</code>.
     * @param divisorSchModId la valeur à écrire dans <code>divisorSchModId</code>.
     */
    public void setDivisorSchModId(int divisorSchModId)
    {
        DivisorSchModId = divisorSchModId;
    }
    /**
     * Accesseur en lecture du champ <code>dividendVarId</code>.
     * @return le champ <code>dividendVarId</code>.
     */
    public int getDividendVarId()
    {
        return DividendVarId;
    }
    /**
     * Accesseur en écriture du champ <code>dividendVarId</code>.
     * @param dividendVarId la valeur à écrire dans <code>dividendVarId</code>.
     */
    public void setDividendVarId(int dividendVarId)
    {
        DividendVarId = dividendVarId;
    }
    /**
     * Accesseur en lecture du champ <code>dividendSchModId</code>.
     * @return le champ <code>dividendSchModId</code>.
     */
    public int getDividendSchModId()
    {
        return DividendSchModId;
    }
    /**
     * Accesseur en écriture du champ <code>dividendSchModId</code>.
     * @param dividendSchModId la valeur à écrire dans <code>dividendSchModId</code>.
     */
    public void setDividendSchModId(int dividendSchModId)
    {
        DividendSchModId = dividendSchModId;
    }
    /** 
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "BudgetElement [id=" + id + ", divisor=" + divisor + ", dividend=" + dividend
                + ", projetId=" + projetId + ", divisorVal=" + divisorVal + ", dividendVal="
                + dividendVal + ", layoutOrder=" + layoutOrder + ", layoutGroupId=" + layoutGroupId
                + ", userId=" + userId + ", DividendVarId=" + DividendVarId + ", DividendSchModId="
                + DividendSchModId + ", DivisorVarId=" + DivisorVarId + ", DivisorSchModId="
                + DivisorSchModId + "]";
    }
    
}

