package update_database.entity;

public class LayoutConstraint{

    private int sortOrder;
    private int layoutGroup;
    /**
     * Accesseur en lecture du champ <code>sortOrder</code>.
     * @return le champ <code>sortOrder</code>.
     */
    public int getSortOrder()
    {
        return sortOrder;
    }
    /**
     * Accesseur en écriture du champ <code>sortOrder</code>.
     * @param sortOrder la valeur à écrire dans <code>sortOrder</code>.
     */
    public void setSortOrder(int sortOrder)
    {
        this.sortOrder = sortOrder;
    }
    /**
     * Accesseur en lecture du champ <code>layoutGroup</code>.
     * @return le champ <code>layoutGroup</code>.
     */
    public int getLayoutGroup()
    {
        return layoutGroup;
    }
    /**
     * Accesseur en écriture du champ <code>layoutGroup</code>.
     * @param layoutGroup la valeur à écrire dans <code>layoutGroup</code>.
     */
    public void setLayoutGroup(int layoutGroup)
    {
        this.layoutGroup = layoutGroup;
    }
    
    
}

