package update_database.entity;


public class Value{

    private int id;
    private String value;
    private int projectId;
    private int flexibleId;
    private int userId;
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
     * Accesseur en lecture du champ <code>value</code>.
     * @return le champ <code>value</code>.
     */
    public String getValue()
    {
        return value;
    }
    /**
     * Accesseur en écriture du champ <code>value</code>.
     * @param value la valeur à écrire dans <code>value</code>.
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    /**
     * Accesseur en lecture du champ <code>projectId</code>.
     * @return le champ <code>projectId</code>.
     */
    public int getProjectId()
    {
        return projectId;
    }
    /**
     * Accesseur en écriture du champ <code>projectId</code>.
     * @param projectId la valeur à écrire dans <code>projectId</code>.
     */
    public void setProjectId(int projectId)
    {
        this.projectId = projectId;
    }
    /**
     * Accesseur en lecture du champ <code>flexibleId</code>.
     * @return le champ <code>flexibleId</code>.
     */
    public int getFlexibleId()
    {
        return flexibleId;
    }
    /**
     * Accesseur en écriture du champ <code>flexibleId</code>.
     * @param flexibleId la valeur à écrire dans <code>flexibleId</code>.
     */
    public void setFlexibleId(int flexibleId)
    {
        this.flexibleId = flexibleId;
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
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Value [id=" + id + ", value=" + value + ", projectId=" + projectId + ", flexibleId="
                + flexibleId + ", userId=" + userId + "]";
    }
    
    
}

