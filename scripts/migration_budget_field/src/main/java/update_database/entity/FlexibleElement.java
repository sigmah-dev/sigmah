package update_database.entity;

public class FlexibleElement{
    
    private int id;
    private int dividendId;
    private int divisorId;
    
    
    
    
    /**
     * Constructeur.
     * @param id
     */
    public FlexibleElement(int id)
    {
        this.id = id;
    }
    /**
     * Constructeur.
     * @param id
     * @param dividendId
     * @param divisorId
     */
    public FlexibleElement(int id, int dividendId, int divisorId){
        this.id=id;
        this.dividendId=dividendId;
        this.divisorId=divisorId;
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
     * Accesseur en lecture du champ <code>dividendId</code>.
     * @return le champ <code>dividendId</code>.
     */
    public int getDividendId()
    {
        return dividendId;
    }
    /**
     * Accesseur en écriture du champ <code>dividendId</code>.
     * @param dividendId la valeur à écrire dans <code>dividendId</code>.
     */
    public void setDividendId(int dividendId)
    {
        this.dividendId = dividendId;
    }
    /**
     * Accesseur en lecture du champ <code>divisorId</code>.
     * @return le champ <code>divisorId</code>.
     */
    public int getDivisorId()
    {
        return divisorId;
    }
    /**
     * Accesseur en écriture du champ <code>divisorId</code>.
     * @param divisorId la valeur à écrire dans <code>divisorId</code>.
     */
    public void setDivisorId(int divisorId)
    {
        this.divisorId = divisorId;
    }
    /** 
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
    /** 
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlexibleElement other = (FlexibleElement) obj;
        if (id != other.id)
            return false;
        return true;
    }

}

